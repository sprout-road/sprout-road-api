package com.strout.api.gis.domain;

import com.strout.api.common.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(
    name = "sigungu",
    uniqueConstraints = {@UniqueConstraint(name = "uk_sig_code", columnNames = "sigCode")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Sigungu extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String sidoCode;

    @Column(length = 5, nullable = false)
    private String sigCode;

    @Column(length = 50, nullable = false)
    private String sigNameKo;

    @Column(length = 50)
    private String sigNameEn;

    @Column(columnDefinition = "geometry(MultiPolygon)")
    private MultiPolygon geometry;

    public static Sigungu create(String sigCode, String sigNameKo, String sigNameEn, MultiPolygon geometry) {
        return Sigungu.builder()
            .sidoCode(sigCode.substring(0, 2))
            .sigCode(sigCode)
            .sigNameKo(sigNameKo)
            .sigNameEn(sigNameEn)
            .geometry(geometry)
            .build();
    }

    public void updateNames(String sigNameKo, String sigNameEn, MultiPolygon geometry) {
        this.sigNameKo = sigNameKo;
        this.sigNameEn = sigNameEn;
        this.geometry = geometry;
    }
}