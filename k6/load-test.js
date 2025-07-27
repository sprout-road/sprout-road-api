import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const redirectTrend = new Trend('redirect_response_time');
const cdnTrend = new Trend('cdn_response_time');

// 테스트 시나리오 설정
export let options = {
    stages: [
        { duration: '30s', target: 5 },   // 2분간 5명 사용자
        { duration: '30s', target: 10 },  // 5분간 10명 사용자
        { duration: '30s', target: 15 },  // 2분간 15명 사용자 (스트레스)
        { duration: '30s', target: 5 },   // 3분간 5명으로 감소
        { duration: '30s', target: 0 },   // 1분간 종료
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95% 요청이 2초 이내
        http_req_failed: ['rate<0.05'],    // 실패율 5% 이하
        errors: ['rate<0.1'],              // 에러율 10% 이하
        redirect_response_time: ['p(95)<100'], // 리다이렉트 응답시간 100ms 이내
    },
    // 자동 리다이렉트 비활성화 (CDN 호출 제어)
    insecureSkipTLSVerify: true,
    noConnectionReuse: false,
};

const BASE_URL = 'http://localhost:8080';

// 테스트용 시도 코드들
const SIDO_CODES = ['11', '26', '27', '28', '29', '30', '31', '36', '41', '43', '44', '46', '47', '48', '50', '51', '52'];

// 테스트용 좌표들 (한국 주요 도시)
const TEST_COORDINATES = [
    { lat: 37.5665, lng: 126.9780 }, // 서울 시청
    { lat: 35.1796, lng: 129.0756 }, // 부산 시청
    { lat: 35.8714, lng: 128.6014 }, // 대구 시청
];

export default function () {
    // 1. 시도 정보 조회 - 리다이렉트 테스트 (CDN 호출 안 함)
    let sidoResponse = http.get(`${BASE_URL}/api/gis/sido`, {
        redirects: 0, // 리다이렉트 따라가지 않음
        headers: {
            'Cache-Control': 'public, max-age=3600',
        },
    });

    // 리다이렉트 응답 검증
    let sidoRedirectSuccess = check(sidoResponse, {
        '시도 리다이렉트 성공': (r) => r.status === 301,
        '시도 리다이렉트 응답시간 < 100ms': (r) => r.timings.duration < 100,
        '시도 Location 헤더 존재': (r) => r.headers['Location'] !== undefined,
        '시도 CDN URL 확인': (r) => r.headers['Location'] && r.headers['Location'].includes('cdn.deepdivers.store'),
    });

    if (!sidoRedirectSuccess) {
        errorRate.add(1);
    }

    redirectTrend.add(sidoResponse.timings.duration);

    // 가끔 실제 CDN 호출 테스트 (10%만)
    if (Math.random() < 0.1) {
        if (sidoResponse.headers['Location']) {
            let cdnResponse = http.get(sidoResponse.headers['Location'], {
                headers: {
                    'Accept': 'application/json',
                },
            });

            check(cdnResponse, {
                'CDN 시도 데이터 성공': (r) => r.status === 200,
                'CDN 응답 크기 확인': (r) => r.body && r.body.length > 10000, // 10KB 이상
                'CDN 응답시간 < 1s': (r) => r.timings.duration < 1000,
            }) || errorRate.add(1);

            cdnTrend.add(cdnResponse.timings.duration);
        }
    }

    sleep(1);

    // 2. 랜덤 시군구 조회 - 리다이렉트 테스트
    const randomSidoCode = SIDO_CODES[Math.floor(Math.random() * SIDO_CODES.length)];
    let sigunguResponse = http.get(`${BASE_URL}/api/gis/sigungu/${randomSidoCode}`, {
        redirects: 0, // 리다이렉트 따라가지 않음
        headers: {
            'Cache-Control': 'public, max-age=1800',
        },
    });

    check(sigunguResponse, {
        '시군구 리다이렉트 성공': (r) => r.status === 301,
        '시군구 리다이렉트 응답시간 < 100ms': (r) => r.timings.duration < 100,
        '시군구 Location 헤더 존재': (r) => r.headers['Location'] !== undefined,
    }) || errorRate.add(1);

    sleep(0.5);

    // 3. 좌표 하이라이트 - 실제 DB 쿼리 테스트
    const randomCoord = TEST_COORDINATES[Math.floor(Math.random() * TEST_COORDINATES.length)];

    let locateResponse = http.post(`${BASE_URL}/api/gis/locate/highlight`,
        JSON.stringify({
            lat: randomCoord.lat,
            lng: randomCoord.lng
        }), {
            headers: {
                'Content-Type': 'application/json',
                'Cache-Control': 'public, max-age=300',
            },
        }
    );

    check(locateResponse, {
        '좌표 조회 성공': (r) => r.status === 200,
        '좌표 응답시간 < 1s': (r) => r.timings.duration < 1000,
        '좌표 응답 데이터 확인': (r) => {
            try {
                if (!r.body) {
                    console.log('❌ 좌표 응답 body가 비어있음');
                    return false;
                }

                const data = JSON.parse(r.body);
                console.log('🔍 좌표 응답 데이터:', JSON.stringify(data, null, 2));

                // 응답 데이터 구조 확인 (실제 API 응답에 맞게 수정)
                if (data.targetCode) {
                    return true;
                } else {
                    console.log('❌ 예상 필드가 없음:', Object.keys(data));
                    return false;
                }
            } catch (e) {
                console.log('❌ JSON 파싱 오류:', e.message);
                console.log('📝 응답 내용:', r.body);
                return false;
            }
        },
    }) || errorRate.add(1);

    sleep(Math.random() * 2 + 1); // 1-3초 랜덤 대기
}

// 테스트 종료 후 요약
export function handleSummary(data) {
    return {
        'summary.json': JSON.stringify(data, null, 2),
        'summary.html': `
      <h2>GIS API 부하 테스트 결과</h2>
      <p>총 요청: ${data.metrics.http_reqs.count}</p>
      <p>실패율: ${(data.metrics.http_req_failed.rate * 100)}%</p>
      <p>평균 응답시간: ${data.metrics.http_req_duration.avg}ms</p>
      <p>95% 응답시간: ${data.metrics.http_req_duration['p(95)']}ms</p>
    `,
    };
}