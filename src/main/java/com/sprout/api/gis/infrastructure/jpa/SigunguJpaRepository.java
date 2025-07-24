package com.sprout.api.gis.infrastructure.jpa;

import com.sprout.api.gis.domain.Sigungu;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SigunguJpaRepository extends JpaRepository<Sigungu, Long> {

    @Query(value = """
        SELECT json_build_object(
            'type', 'FeatureCollection',
            'features', COALESCE(
                (SELECT json_agg(
                    json_build_object(
                        'type', 'Feature',
                        'properties', json_build_object(
                            'sidoCode', sido_code,
                            'sigCode', sig_code,
                            'sigNameKo', sig_name_ko,
                            'sigNameEn', sig_name_en
                        ),
                        'geometry', ST_AsGeoJSON(geometry)::json
                    ) ORDER BY sig_code
                )
                FROM sigungu), 
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
                            'sigCode', sig_code,
                            'sigNameKo', sig_name_ko,
                            'sigNameEn', sig_name_en
                        ),
                        'geometry', ST_AsGeoJSON(geometry)::json
                    ) ORDER BY sig_code
                )
                FROM sigungu
                WHERE sido_code = :sidoCode), 
                '[]'::json
            )
        )::text
        """, nativeQuery = true)
    String findBySidoCodeAsGeoJson(String sidoCode);


    @Query(value = """
        SELECT
            sig_code as sigCode,
            sig_name_ko as sigNameKo,
            sido_code as sidoCode,
            ST_Y(ST_Centroid(geometry)) as centerLat,
            ST_X(ST_Centroid(geometry)) as centerLng
        FROM sigungu
        WHERE ST_Contains(geometry, ST_SetSRID(ST_Point(:lng, :lat), 4326))
        LIMIT 1
        """, nativeQuery = true)
    Optional<SigunguLocationInfo> findByContainsPoint(double lng, double lat);
}
