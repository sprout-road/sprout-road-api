package com.sprout.api.travel.ui.docs.annotation;

import com.sprout.api.travel.ui.docs.constants.SwaggerExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
    summary = "여행일지 수정",
    requestBody = @RequestBody(
        description = "여행일지 수정 요청",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "텍스트와 이미지 예시",
                    description = "텍스트 블록과 이미지 블록이 포함된 예시",
                    value = SwaggerExamples.TRAVEL_LOG_UPDATE_EXAMPLE
                )
            }
        )
    )
)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TravelLogUpdateDocs {
}
