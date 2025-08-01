package com.sprout.api.gis.infrastructure.jpa;

import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.gis.application.result.LocationResult;
import com.sprout.api.gis.domain.Sido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SidoJpaRepository extends JpaRepository<Sido, Long> {

    @Query(value = """
        SELECT json_build_object(
            'type', 'FeatureCollection',
            'features', COALESCE(
                (SELECT json_agg(
                    json_build_object(
                        'type', 'Feature',
                        'properties', json_build_object(
                            'sidoCode', sido_code,
                            'sidoNameKo', sido_name_ko,
                            'sidoNameEn', sido_name_en,
                            'centerLat', ST_Y(ST_Centroid(geometry)),
                            'centerLng', ST_X(ST_Centroid(geometry))
                        ),
                        'geometry', ST_AsGeoJSON(ST_Simplify(geometry, 0.005))::json
                    ) ORDER BY sido_code
                )
                FROM sido), 
                '[]'::json
            )
        )::text
        """, nativeQuery = true)
    String findAllAsGeoJson();

    @Query(value = """
    SELECT json_build_object(
        'type', 'FeatureCollection',
        'features', COALESCE(
            (SELECT json_agg(
                json_build_object(
                    'type', 'Feature',
                    'properties', json_build_object(
                        'sidoCode', sido_code,
                        'sidoNameKo', sido_name_ko,
                        'sidoNameEn', sido_name_en,
                        'centerLat', ST_Y(ST_Centroid(geometry)),
                        'centerLng', ST_X(ST_Centroid(geometry))
                    ),
                    'geometry', ST_AsGeoJSON(ST_Boundary(geometry))::json
                ) ORDER BY sido_code
            )
            FROM sido
            WHERE sido_code = :sidoCode), 
            '[]'::json
        )
    )::text
    """, nativeQuery = true)
    String findSidoBoundaries(String sidoCode);

    @Query(value = """
        SELECT json_build_object(
            'type', 'FeatureCollection',
            'features', COALESCE(
                (SELECT json_agg(
                    json_build_object(
                        'type', 'Feature',
                        'properties', json_build_object(
                            'regionCode', sido_code,
                            'regionName', sido_name_ko,
                            'centerLat', ST_Y(ST_Centroid(geometry)),
                            'centerLng', ST_X(ST_Centroid(geometry))
                        ),
                        'geometry', ST_AsGeoJSON(ST_Simplify(geometry, 0.00001))::json
                    ) ORDER BY sido_code
                )
                FROM sido
                WHERE sido_code = :sidoCode), 
                '[]'::json
            )
        )::text
        """, nativeQuery = true)
    String findSidoRegionBySidoCode(String sidoCode);


    @Query(value = """
        SELECT
            sido_code as regionCode,
            sido_name_ko as regionName,
            ST_Y(ST_Centroid(geometry)) as centerLat,
            ST_X(ST_Centroid(geometry)) as centerLng
        FROM sido
        WHERE ST_Contains(geometry, ST_SetSRID(ST_Point(:lng, :lat), 4326))
            AND sido_code IN ('11', '26', '27', '28', '29', '30', '31', '36')
        LIMIT 1
        """, nativeQuery = true)
    Optional<LocationResult> findSpecialCityPoint(Double lng, Double lat);

    @Query(value = """
    SELECT sido_code as regionCode, sido_name_ko as regionName
    FROM sido
    WHERE sido_code IN ('11', '26', '27', '28', '29', '30', '31', '36')
    """, nativeQuery = true)
    List<RegionInfoDto> findAllSpecialCityNames();
}
