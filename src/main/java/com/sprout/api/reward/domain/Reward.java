package com.sprout.api.reward.domain;

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
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "rewards",
    uniqueConstraints = @UniqueConstraint(name = "uk_region_code_name", columnNames = {"regionCode", "regionName"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String regionCode;
    @Column(nullable = false)
    private String regionName;
    @Column(nullable = false)
    private String imageUrl;

    public static Reward create(String regionCode, String regionName, String imageUrl) {
        return Reward.builder()
            .regionCode(regionCode)
            .regionName(regionName)
            .imageUrl(imageUrl)
            .build();
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
