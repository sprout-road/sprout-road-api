package com.strout.api.gis.application;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.Sido;
import com.strout.api.gis.domain.SidoRepository;
import com.strout.api.gis.infrastructure.geotools.SidoGisParser;
import com.strout.api.gis.util.ShapefileValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SidoGisService {

    private final SidoGisParser sidoGisParser;
    private final ShapefileValidator shapefileValidator;
    private final SidoRepository sidoRepository;

    public void uploadSidoShapefile(ShapefileUploadCommand command) {
        shapefileValidator.validate(command);
        if (sidoRepository.count() > 0) {
            throw new IllegalStateException();
        }

        List<Sido> entities = sidoGisParser.parse(command);
        sidoRepository.saveAll(entities);
    }
}