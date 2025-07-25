package com.strout.api.gis.util;

import com.strout.api.gis.application.ShapefileParsingStrategy;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShapefileParser {

    private final List<ShapefileParsingStrategy> strategies;
    
    public void parse(ShapefileUploadCommand command, ShapefileType type) {
        ShapefileParsingStrategy strategy = strategies.stream()
            .filter(s -> s.supports(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 타입: " + type));
            
        strategy.parse(command);
    }
}