package com.sprout.api.gis.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "sido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Sido extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String sidoCode;

    @Column(length = 50, nullable = false)
    private String sidoNameKo;

    @Column(length = 50)
    private String sidoNameEn;

    @Column(columnDefinition = "geometry(MultiPolygon)")
    private MultiPolygon geometry;

    public static Sido create(String sidoCode, String sidoNameKo, String sidoNameEn, MultiPolygon geometry) {
        return Sido.builder()
            .sidoCode(sidoCode)
            .sidoNameKo(sidoNameKo)
            .sidoNameEn(sidoNameEn)
            .geometry(geometry)
            .build();
    }

    public void updateNames(String sidoNameKo, String sidoNameEn, MultiPolygon geometry) {
        this.sidoNameKo = sidoNameKo;
        this.sidoNameEn = sidoNameEn;
        this.geometry = geometry;
    }
}