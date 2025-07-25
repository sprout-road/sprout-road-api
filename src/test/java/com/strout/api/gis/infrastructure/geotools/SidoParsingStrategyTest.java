package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Envelope;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SidoParsingStrategy 테스트")
class SidoParsingStrategyTest {

    @Mock
    private ShapefileParsingSupport parsingSupport;

    @Mock
    private ShapefileDataStore dataStore;

    @Mock
    private SimpleFeatureSource featureSource;

    @Mock
    private SimpleFeatureType schema;

    @Mock
    private SimpleFeatureCollection features;

    @Mock
    private SimpleFeatureIterator iterator;

    @Mock
    private SimpleFeature feature;

    @Mock
    private Geometry geometry;

    @InjectMocks
    private SidoParsingStrategy sidoParsingStrategy;

    private ShapefileUploadCommand command;
    private File tempShpFile;

    @BeforeEach
    void setUp() {
        command = mock(ShapefileUploadCommand.class);
        tempShpFile = new File("test.shp");
    }

    @Test
    @DisplayName("SIDO 타입을 지원하는지 확인")
    void shouldSupportSidoType() {
        // when & then
        assertThat(sidoParsingStrategy.supports(ShapefileType.SIDO)).isTrue();
    }

    @Test
    @DisplayName("SIDO가 아닌 다른 타입은 지원하지 않음")
    void shouldNotSupportNonSidoTypes() {
        // when & then
        assertThat(sidoParsingStrategy.supports(ShapefileType.SIGUNGU)).isFalse();
    }

    @Test
    @DisplayName("정상적인 시/도 Shapefile 파싱 성공")
    void shouldParseValidSidoShapefileSuccessfully() throws Exception {
        // given
        setupValidShapefileData();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport).createDataStore(tempShpFile);
        verify(parsingSupport).logBasicSchemaInfo(schema, features, "시/도");
        verify(dataStore).dispose();
        verify(iterator).close();
    }

    @Test
    @DisplayName("여러 시/도 데이터가 있을 때 모든 데이터 처리")
    void shouldProcessMultipleSidoFeatures() throws Exception {
        // given
        setupMultipleSidoFeatures();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(iterator, times(3)).hasNext();
        verify(iterator, times(2)).next();
        verify(dataStore).dispose();
        verify(iterator).close();
    }

    @Test
    @DisplayName("기하정보가 null인 경우에도 정상 처리")
    void shouldHandleNullGeometry() throws Exception {
        // given
        setupValidShapefileData();
        when(feature.getAttribute("the_geom")).thenReturn(null);
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport).createDataStore(tempShpFile);
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("속성값이 null인 경우 정상 처리")
    void shouldHandleNullAttributes() throws Exception {
        // given
        setupShapefileWithNullAttributes();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport).createDataStore(tempShpFile);
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("임시 파일 생성 실패 시 예외 발생")
    void shouldThrowExceptionWhenTemporaryFileCreationFails() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command))
            .thenThrow(new IOException("임시 파일 생성 실패"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("시/도 Shapefile 파싱 실패")
            .hasCauseInstanceOf(IOException.class);

        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport, never()).createDataStore(any());
    }

    @Test
    @DisplayName("DataStore 생성 실패 시 예외 발생")
    void shouldThrowExceptionWhenDataStoreCreationFails() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile))
            .thenThrow(new IOException("DataStore 생성 실패"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("시/도 Shapefile 파싱 실패")
            .hasCauseInstanceOf(IOException.class);

        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport).createDataStore(tempShpFile);
    }

    @Test
    @DisplayName("Feature 읽기 실패 시 예외 발생")
    void shouldThrowExceptionWhenFeatureReadingFails() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenThrow(new IOException("Feature 읽기 실패"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("시/도 Shapefile 파싱 실패")
            .hasCauseInstanceOf(IOException.class);

        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("Iterator 처리 중 예외 발생 시 리소스 정리")
    void shouldCleanupResourcesWhenIteratorFails() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenThrow(new RuntimeException("Iterator 오류"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("시/도 Shapefile 파싱 실패");

        verify(iterator).close();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("빈 feature collection 처리")
    void shouldHandleEmptyFeatureCollection() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(false);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(iterator).hasNext();
        verify(iterator, never()).next();
        verify(iterator).close();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("5개 이상의 feature가 있을 때 처음 5개만 상세 로깅되는지 확인")
    void shouldLogOnlyFirstFiveFeaturesInDetail() throws Exception {
        // given
        setupManyFeatures(10);
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(iterator, times(11)).hasNext(); // 10번 true, 1번 false
        verify(iterator, times(10)).next();
        verify(feature, times(10)).getAttribute("CTPRVN_CD");
        verify(feature, times(10)).getAttribute("CTP_KOR_NM");
        verify(feature, times(10)).getAttribute("CTP_ENG_NM");
        verify(feature, times(5)).getAttribute("the_geom"); // 처음 5개만 기하정보 조회
    }

    @Test
    @DisplayName("command가 null인 경우 예외 발생")
    void shouldThrowExceptionWhenCommandIsNull() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(null))
            .thenThrow(new NullPointerException("Command cannot be null"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(null))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("시/도 Shapefile 파싱 실패")
            .hasCauseInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("시/도 코드 매핑이 올바르게 처리되는지 검증")
    void shouldCorrectlyMapSidoCodes() throws Exception {
        // given
        setupSeoulAndBusanFeatures();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(feature, times(2)).getAttribute("CTPRVN_CD");
        verify(feature, times(2)).getAttribute("CTP_KOR_NM");
        verify(feature, times(2)).getAttribute("CTP_ENG_NM");
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("Shapefile 스키마 정보 로깅이 올바르게 호출되는지 검증")
    void shouldCallLogBasicSchemaInfoCorrectly() throws Exception {
        // given
        setupValidShapefileData();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(parsingSupport).logBasicSchemaInfo(eq(schema), eq(features), eq("시/도"));
    }

    @Test
    @DisplayName("DataStore가 항상 정리되는지 검증")
    void shouldAlwaysDisposeDataStore() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenThrow(new RuntimeException("처리 중 오류"));

        // when & then
        assertThatThrownBy(() -> sidoParsingStrategy.parse(command))
            .isInstanceOf(RuntimeException.class);

        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("특정 시/도 속성값들의 정확한 추출 검증")
    void shouldExtractCorrectSidoAttributes() throws Exception {
        // given
        setupSpecificSidoData();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(feature).getAttribute("CTPRVN_CD");
        verify(feature).getAttribute("CTP_KOR_NM");
        verify(feature).getAttribute("CTP_ENG_NM");
        verify(feature).getAttribute("the_geom");
    }

    @Test
    @DisplayName("기하정보의 속성들이 올바르게 로깅되는지 검증")
    void shouldLogGeometryPropertiesCorrectly() throws Exception {
        // given
        setupGeometryWithProperties();
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempShpFile);
        when(parsingSupport.createDataStore(tempShpFile)).thenReturn(dataStore);

        // when
        sidoParsingStrategy.parse(command);

        // then
        verify(geometry).getGeometryType();
        verify(geometry).getNumPoints();
        verify(geometry).getEnvelopeInternal();
    }

    private void setupValidShapefileData() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("11");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("서울특별시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Seoul");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("MultiPolygon");
        when(geometry.getNumPoints()).thenReturn(1000);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupMultipleSidoFeatures() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("11", "26");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("서울특별시", "부산광역시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Seoul", "Busan");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("MultiPolygon");
        when(geometry.getNumPoints()).thenReturn(1000);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupSeoulAndBusanFeatures() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("11", "26");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("서울특별시", "부산광역시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Seoul", "Busan");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("MultiPolygon");
        when(geometry.getNumPoints()).thenReturn(1000);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupShapefileWithNullAttributes() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn(null);
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn(null);
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn(null);
        when(feature.getAttribute("the_geom")).thenReturn(null);
    }

    private void setupManyFeatures(int count) throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);

        // Setup hasNext() calls - true for 'count' times, then false
        Boolean[] hasNextResults = new Boolean[count + 1];
        for (int i = 0; i < count; i++) {
            hasNextResults[i] = true;
        }
        hasNextResults[count] = false;
        when(iterator.hasNext()).thenReturn(true, hasNextResults);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("11");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("서울특별시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Seoul");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("MultiPolygon");
        when(geometry.getNumPoints()).thenReturn(1000);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupSpecificSidoData() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("28");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("인천광역시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Incheon");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("Polygon");
        when(geometry.getNumPoints()).thenReturn(500);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.4, 126.8, 37.2, 37.6));
    }

    private void setupGeometryWithProperties() throws Exception {
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);

        when(feature.getAttribute("CTPRVN_CD")).thenReturn("29");
        when(feature.getAttribute("CTP_KOR_NM")).thenReturn("광주광역시");
        when(feature.getAttribute("CTP_ENG_NM")).thenReturn("Gwangju");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);

        when(geometry.getGeometryType()).thenReturn("MultiPolygon");
        when(geometry.getNumPoints()).thenReturn(750);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.7, 127.0, 35.1, 35.3));
    }
}