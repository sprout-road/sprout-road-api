package com.sprout.api.gis.infrastructure.jpa;

import com.sprout.api.gis.domain.Sigungu;
import com.sprout.api.gis.domain.dto.SigunguLocationInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SigunguJpaRepository extends JpaRepository<Sigungu, Long> {

    @Query(value = """
    WITH cleaned_data AS (
        SELECT
            sido_code,
            sig_name_ko,
            sig_code,
            ST_MakeValid(ST_Buffer(geometry, 0.00001)) as clean_geometry
        FROM sigungu
        WHERE sido_code = :sidoCode
    ),
    unified_sigungu AS (
        SELECT
            sido_code,
            CASE
                WHEN sig_name_ko ~ '.+시.+(구|군)$'
                THEN REGEXP_REPLACE(sig_name_ko, '\\s+(.*구|.*군)$', '')
                ELSE sig_name_ko
            END as unified_name,
            ST_Union(clean_geometry) as geometry,
            MIN(sig_code) as representative_code
            FROM cleaned_data
            GROUP BY
                sido_code,
                CASE
                    WHEN sig_name_ko ~ '.+시.+(구|군)$'
                    THEN REGEXP_REPLACE(sig_name_ko, '\\s+(.*구|.*군)$', '')
                    ELSE sig_name_ko
                END
    )
    SELECT json_build_object(
        'type', 'FeatureCollection',
        'features', COALESCE(
            (SELECT json_agg(
                json_build_object(
                    'type', 'Feature',
                    'properties', json_build_object(
                        'sidoCode', sido_code,
                        'sigCode', representative_code,
                        'sigNameKo', unified_name,
                        'sigNameEn', unified_name,
                        'centerLat', ST_Y(ST_Centroid(geometry)),
                        'centerLng', ST_X(ST_Centroid(geometry))
                    ),
                    'geometry', ST_AsGeoJSON(geometry)::json
                ) ORDER BY representative_code
            )
            FROM unified_sigungu), 
            '[]'::json
        )
    )::text
    """, nativeQuery = true)
    String findBySidoCodeAsGeoJsonUnified(String sidoCode);

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
