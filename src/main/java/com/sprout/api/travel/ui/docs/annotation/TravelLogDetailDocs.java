package com.sprout.api.travel.ui.docs.annotation;

import com.sprout.api.travel.ui.docs.constants.SwaggerExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
    summary = "내 여행 일지 상세 조회",
    responses = @ApiResponse(
        responseCode = "200",
        description = "여행일지 상세 정보",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "여행일지 상세 조회 응답",
                value = SwaggerExamples.TRAVEL_LOG_DETAIL_RESPONSE
            )
        )
    )
)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TravelLogDetailDocs {
}