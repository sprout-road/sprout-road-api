package com.strout.api.gis.application;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import com.strout.api.gis.infrastructure.geotools.ShapefileParser;
import com.strout.api.gis.util.ShapefileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisUploadService {

    private final ShapefileParser shapefileParser;
    private final ShapefileValidator shapefileValidator;

    /**
     * 시/도 Shapefile 업로드 처리
     */
    public void uploadSidoShapefile(ShapefileUploadCommand command) {
        log.info("=== 시/도 Shapefile 업로드 시작 ===");

        // 파일 검증
        shapefileValidator.validate(command);

        // 파일 정보 로깅
        logFileInfo(command.shp(), "시/도 SHP");
        logFileInfo(command.dbf(), "시/도 DBF");
        logFileInfo(command.shx(), "시/도 SHX");
        logFileInfo(command.prj(), "시/도 PRJ");

        // Shapefile 파싱 (Infrastructure에 위임)
        shapefileParser.parseSidoShapefile(command);

        log.info("=== 시/도 Shapefile 업로드 완료 ===");
    }

    private void logFileInfo(ShapefileDto shapefile, String type) {
        log.info("{} 파일 정보:", type);
        log.info("  - 파일명: {}", shapefile.filename());
        log.info("  - 크기: {} bytes ({} KB)", shapefile.data().length, shapefile.data().length / 1024);
    }
}