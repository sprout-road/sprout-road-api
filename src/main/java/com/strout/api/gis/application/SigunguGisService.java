package com.strout.api.gis.application;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.Sigungu;
import com.strout.api.gis.domain.SigunguRepository;
import com.strout.api.gis.infrastructure.geotools.SigunguParser;
import com.strout.api.gis.util.ShapefileValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SigunguGisService {

    private final SigunguParser sigunguParser;
    private final ShapefileValidator shapefileValidator;
    private final SigunguRepository sigunguRepository;

    public void uploadSigunguShapefile(ShapefileUploadCommand command) {
        shapefileValidator.validate(command);
        if (sigunguRepository.count() > 0) {
            throw new IllegalStateException();
        }

        List<Sigungu> parse = sigunguParser.parse(command);
        sigunguRepository.saveAll(parse);
    }
}