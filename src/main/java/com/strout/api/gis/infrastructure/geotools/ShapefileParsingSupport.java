package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShapefileParsingSupport {

    /**
     * Shapefile 세트 임시 파일 생성 (자동 삭제됨)
     */
    public File createTemporaryShapefileSet(ShapefileUploadCommand command) throws IOException {
        String uniqueId = "shapefile_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        File tempDir = Files.createTempDirectory("shapefile_upload").toFile();
        tempDir.deleteOnExit();

        File tempShpFile = new File(tempDir, uniqueId + ".shp");
        File tempDbfFile = new File(tempDir, uniqueId + ".dbf");
        File tempShxFile = new File(tempDir, uniqueId + ".shx");
        File tempPrjFile = new File(tempDir, uniqueId + ".prj");

        tempShpFile.deleteOnExit();
        tempDbfFile.deleteOnExit();
        tempShxFile.deleteOnExit();
        tempPrjFile.deleteOnExit();

        Files.write(tempShpFile.toPath(), command.shp().data(), StandardOpenOption.CREATE);
        Files.write(tempDbfFile.toPath(), command.dbf().data(), StandardOpenOption.CREATE);
        Files.write(tempShxFile.toPath(), command.shx().data(), StandardOpenOption.CREATE);
        Files.write(tempPrjFile.toPath(), command.prj().data(), StandardOpenOption.CREATE);

        log.info("임시 Shapefile 세트 생성 (자동 삭제 예정):");
        log.info("  - SHP: {}", tempShpFile.getAbsolutePath());
        log.info("  - DBF: {}", tempDbfFile.getAbsolutePath());
        log.info("  - SHX: {}", tempShxFile.getAbsolutePath());
        log.info("  - PRJ: {}", tempPrjFile.getAbsolutePath());

        return tempShpFile;
    }

    /**
     * ShapefileDataStore 생성 및 기본 설정
     */
    public ShapefileDataStore createDataStore(File shpFile) throws Exception {
        ShapefileDataStore dataStore = new ShapefileDataStore(shpFile.toURI().toURL());
        dataStore.setCharset(Charset.forName("EUC-KR"));
        return dataStore;
    }

    /**
     * 기본 스키마 정보 로깅
     */
    public void logBasicSchemaInfo(SimpleFeatureType schema, SimpleFeatureCollection features, String dataType) {
        log.info("=== {} Shapefile 데이터 분석 ===", dataType);
        log.info("Feature Type: {}", schema.getTypeName());
        log.info("좌표계: {}", schema.getCoordinateReferenceSystem());
        log.info("속성 개수: {}", schema.getAttributeCount());

        // 속성 정보 로깅
        log.info("=== {} 속성 정보 ===", dataType);
        for (AttributeDescriptor attr : schema.getAttributeDescriptors()) {
            log.info("- {}: {} ({})",
                attr.getLocalName(),
                attr.getType().getBinding().getSimpleName(),
                attr.getType().getName());
        }

        log.info("총 {} 개수: {}", dataType, features.size());
    }
}