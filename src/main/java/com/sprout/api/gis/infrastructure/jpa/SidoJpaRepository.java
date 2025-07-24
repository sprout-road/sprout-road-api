package com.sprout.api.gis.infrastructure.jpa;

import com.sprout.api.gis.domain.Sido;
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
                            'sidoNameEn', sido_name_en
                        ),
                        'geometry', ST_AsGeoJSON(geometry)::json
                    ) ORDER BY sido_code
                )
                FROM sido), 
                '[]'::json
            )
        )::text
        """, nativeQuery = true)
    String findAllAsGeoJson();
}
