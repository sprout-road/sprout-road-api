package com.sprout.api.travel.domain;

import com.sprout.api.travel.domain.vo.ContentType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Table(name = "content_blocks")
public class ContentBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_log_id")
    private TravelLog travelLog;

    private Integer displayOrder;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> content;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    public static ContentBlock of(Integer order, Map<String, String> content, ContentType contentType) {
        return ContentBlock.builder()
            .displayOrder(order)
            .content(content)
            .contentType(contentType)
            .build();
    }
}