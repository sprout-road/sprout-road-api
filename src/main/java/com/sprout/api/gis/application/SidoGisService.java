package com.sprout.api.gis.application;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.gis.application.command.ShapefileUploadCommand;
import com.sprout.api.gis.domain.Sido;
import com.sprout.api.gis.domain.SidoRepository;
import com.sprout.api.gis.infrastructure.geotools.SidoGisParser;
import com.sprout.api.gis.util.ShapefileValidator;
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
            throw new BusinessException(400, "이미 지도 정보를 업로드 했음");
        }

        List<Sido> entities = sidoGisParser.parse(command);
        sidoRepository.saveAll(entities);
    }
}