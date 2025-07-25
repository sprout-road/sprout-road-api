package com.sprout.api.gis.infrastructure.geotools;

import com.sprout.api.gis.application.command.ShapefileUploadCommand;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.shapefile.ShapefileDataStore;
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
}