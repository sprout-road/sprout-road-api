package com.sprout.api.travel.domain;

import com.sprout.api.travel.domain.vo.ContentType;
import com.sprout.api.travel.domain.vo.ContentValue;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
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
    private String id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_log_id")
    private TravelLog travelLog;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private Integer displayOrder;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private ContentValue content;

    public static ContentBlock of(
        String id,
        ContentType type,
        Integer order,
        ContentValue content
    ) {
        return ContentBlock.builder()
            .id(id)
            .type(type)
            .displayOrder(order)
            .content(content)
            .build();
    }
}