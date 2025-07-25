package com.strout.api.gis.infrastructure.geotools;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import com.strout.api.gis.domain.ShapefileType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SigunguParsingStrategy 단위 테스트
 * 
 * 테스트 프레임워크: JUnit 5 + Mockito
 * 테스트 대상: 시/군/구 Shapefile 파싱 전략 클래스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SigunguParsingStrategy 단위 테스트")
class SigunguParsingStrategyTest {

    @Mock
    private ShapefileParsingSupport parsingSupport;

    @Mock
    private ShapefileDataStore dataStore;

    @Mock
    private SimpleFeatureSource featureSource;

    @Mock
    private SimpleFeatureCollection features;

    @Mock
    private SimpleFeatureIterator iterator;

    @Mock
    private SimpleFeature feature;

    @Mock
    private SimpleFeatureType schema;

    @Mock
    private Geometry geometry;

    @InjectMocks
    private SigunguParsingStrategy sigunguParsingStrategy;

    private ShapefileUploadCommand command;
    private File tempFile;

    @BeforeEach
    void setUp() {
        // Create mock command with valid ShapefileDto objects
        ShapefileDto shpDto = new ShapefileDto("test.shp", "application/octet-stream", new byte[100]);
        ShapefileDto dbfDto = new ShapefileDto("test.dbf", "application/octet-stream", new byte[50]);
        ShapefileDto shxDto = new ShapefileDto("test.shx", "application/octet-stream", new byte[25]);
        ShapefileDto prjDto = new ShapefileDto("test.prj", "text/plain", new byte[200]);
        
        command = new ShapefileUploadCommand(shpDto, dbfDto, shxDto, prjDto);
        tempFile = mock(File.class);
    }

    @Test
    @DisplayName("supports() - SIGUNGU 타입일 때 true 반환")
    void supports_SigunguType_ReturnsTrue() {
        // when & then
        assertTrue(sigunguParsingStrategy.supports(ShapefileType.SIGUNGU));
    }

    @Test
    @DisplayName("supports() - 다른 ShapefileType에 대해 false 반환")
    void supports_OtherTypes_ReturnsFalse() {
        // when & then
        assertFalse(sigunguParsingStrategy.supports(ShapefileType.SIDO));
        assertFalse(sigunguParsingStrategy.supports(ShapefileType.EMD));
    }

    @Test
    @DisplayName("parse() - 정상적인 시군구 데이터 파싱 성공")
    void parse_ValidSigunguData_ParsesSuccessfully() throws Exception {
        // given
        setupValidParsingMocks();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(parsingSupport).createTemporaryShapefileSet(command);
        verify(parsingSupport).createDataStore(tempFile);
        verify(parsingSupport).logBasicSchemaInfo(schema, features, "시/군/구");
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parse() - 임시 파일 생성 실패 시 RuntimeException 발생")
    void parse_TemporaryFileCreationFails_ThrowsRuntimeException() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command))
            .thenThrow(new IOException("파일 생성 실패"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sigunguParsingStrategy.parse(command));
        
        assertEquals("시/군/구 Shapefile 파싱 실패", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    @DisplayName("parse() - 데이터스토어 생성 실패 시 RuntimeException 발생")
    void parse_DataStoreCreationFails_ThrowsRuntimeException() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile))
            .thenThrow(new IOException("데이터스토어 생성 실패"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sigunguParsingStrategy.parse(command));
        
        assertEquals("시/군/구 Shapefile 파싱 실패", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    @DisplayName("parse() - null command 처리")
    void parse_NullCommand_ThrowsRuntimeException() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(null))
            .thenThrow(new NullPointerException("Command가 null입니다"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sigunguParsingStrategy.parse(null));
        
        assertEquals("시/군/구 Shapefile 파싱 실패", exception.getMessage());
    }

    @Test
    @DisplayName("parseSigunguData() - 빈 피처 컬렉션 처리")
    void parseSigunguData_EmptyFeatureCollection_HandlesGracefully() throws Exception {
        // given
        setupEmptyFeatureCollection();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(parsingSupport).logBasicSchemaInfo(schema, features, "시/군/구");
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 다양한 시도코드의 시군구 데이터 그룹화")
    void parseSigunguData_MultipleProvinces_GroupsCorrectly() throws Exception {
        // given
        setupMultipleProvincesData();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(4)).hasNext();
        verify(iterator, times(3)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - null 시군구 코드가 있는 피처 건너뛰기")
    void parseSigunguData_NullSigCode_SkipsFeature() throws Exception {
        // given
        setupFeatureWithNullCode();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 짧은 시군구 코드가 있는 피처 건너뛰기") 
    void parseSigunguData_ShortSigCode_SkipsFeature() throws Exception {
        // given
        setupFeatureWithShortCode();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 기하정보가 있는 피처의 상세 로깅")
    void parseSigunguData_FeatureWithGeometry_LogsGeometryInfo() throws Exception {
        // given
        setupFeatureWithGeometry();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(geometry).getGeometryType();
        verify(geometry).getNumPoints();
        verify(geometry).getEnvelopeInternal();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 기하정보가 null인 피처 정상 처리")
    void parseSigunguData_FeatureWithNullGeometry_HandlesGracefully() throws Exception {
        // given
        setupFeatureWithNullGeometry();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 피처 반복자에서 예외 발생 시 RuntimeException")
    void parseSigunguData_IteratorException_ThrowsRuntimeException() throws Exception {
        // given
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenThrow(new RuntimeException("Iterator 오류"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> sigunguParsingStrategy.parse(command));
        
        assertEquals("시/군/구 Shapefile 파싱 실패", exception.getMessage());
    }

    @Test
    @DisplayName("parseSigunguData() - 많은 피처에서 처음 5개만 상세 로깅")
    void parseSigunguData_ManyFeatures_LogsOnlyFirstFive() throws Exception {
        // given
        setupManyFeatures();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(11)).hasNext(); // 10개 피처 + 마지막 false
        verify(iterator, times(10)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 동일한 시도코드 내의 여러 시군구 그룹화")
    void parseSigunguData_SameProvince_GroupsTogether() throws Exception {
        // given
        setupSameProvinceFeatures();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(4)).hasNext();
        verify(iterator, times(3)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 속성값이 빈 문자열인 경우 처리")
    void parseSigunguData_EmptyStringAttributes_HandlesGracefully() throws Exception {
        // given
        setupFeatureWithEmptyStringAttributes();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        verify(dataStore).dispose();
    }

    @Test
    @DisplayName("parseSigunguData() - 특수문자가 포함된 지역명 처리")
    void parseSigunguData_SpecialCharactersInNames_HandlesCorrectly() throws Exception {
        // given
        setupFeatureWithSpecialCharacters();

        // when
        assertDoesNotThrow(() -> sigunguParsingStrategy.parse(command));

        // then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        verify(dataStore).dispose();
    }

    // Helper methods for setting up mocks

    private void setupValidParsingMocks() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("종로구");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);
        when(geometry.getGeometryType()).thenReturn("Polygon");
        when(geometry.getNumPoints()).thenReturn(100);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupEmptyFeatureCollection() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(false);
    }

    private void setupMultipleProvincesData() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        
        SimpleFeature feature1 = mock(SimpleFeature.class);
        SimpleFeature feature2 = mock(SimpleFeature.class);
        SimpleFeature feature3 = mock(SimpleFeature.class);
        
        when(iterator.hasNext()).thenReturn(true, true, true, false);
        when(iterator.next()).thenReturn(feature1, feature2, feature3);
        
        // 서울특별시 (11)
        when(feature1.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature1.getAttribute("SIG_KOR_NM")).thenReturn("종로구");
        when(feature1.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu");
        
        // 부산광역시 (26)
        when(feature2.getAttribute("SIG_CD")).thenReturn("2601");
        when(feature2.getAttribute("SIG_KOR_NM")).thenReturn("중구");
        when(feature2.getAttribute("SIG_ENG_NM")).thenReturn("Jung-gu");
        
        // 경기도 (41)
        when(feature3.getAttribute("SIG_CD")).thenReturn("4101");
        when(feature3.getAttribute("SIG_KOR_NM")).thenReturn("수원시");
        when(feature3.getAttribute("SIG_ENG_NM")).thenReturn("Suwon-si");
    }

    private void setupFeatureWithNullCode() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn(null);
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("테스트구");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Test-gu");
    }

    private void setupFeatureWithShortCode() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1"); // 길이가 2보다 작음
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("테스트구");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Test-gu");
    }

    private void setupFeatureWithGeometry() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("종로구");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu");
        when(feature.getAttribute("the_geom")).thenReturn(geometry);
        when(geometry.getGeometryType()).thenReturn("Polygon");
        when(geometry.getNumPoints()).thenReturn(50);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupFeatureWithNullGeometry() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("종로구");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu");
        when(feature.getAttribute("the_geom")).thenReturn(null);
    }

    private void setupManyFeatures() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        
        List<Boolean> hasNextResults = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hasNextResults.add(true);
        }
        hasNextResults.add(false);
        
        when(iterator.hasNext()).thenReturn(true, hasNextResults.toArray(new Boolean[0]));
        
        List<SimpleFeature> mockFeatures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SimpleFeature mockFeature = mock(SimpleFeature.class);
            when(mockFeature.getAttribute("SIG_CD")).thenReturn(String.format("11%02d", i + 1));
            when(mockFeature.getAttribute("SIG_KOR_NM")).thenReturn("구" + (i + 1));
            when(mockFeature.getAttribute("SIG_ENG_NM")).thenReturn("District" + (i + 1));
            when(mockFeature.getAttribute("the_geom")).thenReturn(geometry);
            mockFeatures.add(mockFeature);
        }
        
        when(iterator.next()).thenReturn(
            mockFeatures.get(0), mockFeatures.toArray(new SimpleFeature[0])
        );
        
        when(geometry.getGeometryType()).thenReturn("Polygon");
        when(geometry.getNumPoints()).thenReturn(100);
        when(geometry.getEnvelopeInternal()).thenReturn(new Envelope(126.0, 127.0, 37.0, 38.0));
    }

    private void setupSameProvinceFeatures() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        
        SimpleFeature feature1 = mock(SimpleFeature.class);
        SimpleFeature feature2 = mock(SimpleFeature.class);
        SimpleFeature feature3 = mock(SimpleFeature.class);
        
        when(iterator.hasNext()).thenReturn(true, true, true, false);
        when(iterator.next()).thenReturn(feature1, feature2, feature3);
        
        // 모두 서울특별시 (11)
        when(feature1.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature1.getAttribute("SIG_KOR_NM")).thenReturn("종로구");
        when(feature1.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu");
        
        when(feature2.getAttribute("SIG_CD")).thenReturn("1102");
        when(feature2.getAttribute("SIG_KOR_NM")).thenReturn("중구");
        when(feature2.getAttribute("SIG_ENG_NM")).thenReturn("Jung-gu");
        
        when(feature3.getAttribute("SIG_CD")).thenReturn("1103");
        when(feature3.getAttribute("SIG_KOR_NM")).thenReturn("용산구");
        when(feature3.getAttribute("SIG_ENG_NM")).thenReturn("Yongsan-gu");
    }

    private void setupFeatureWithEmptyStringAttributes() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("");
        when(feature.getAttribute("the_geom")).thenReturn(null);
    }

    private void setupFeatureWithSpecialCharacters() throws Exception {
        when(parsingSupport.createTemporaryShapefileSet(command)).thenReturn(tempFile);
        when(parsingSupport.createDataStore(tempFile)).thenReturn(dataStore);
        when(dataStore.getFeatureSource()).thenReturn(featureSource);
        when(featureSource.getSchema()).thenReturn(schema);
        when(featureSource.getFeatures()).thenReturn(features);
        when(features.features()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(feature);
        when(feature.getAttribute("SIG_CD")).thenReturn("1101");
        when(feature.getAttribute("SIG_KOR_NM")).thenReturn("종로구 (특별구역)");
        when(feature.getAttribute("SIG_ENG_NM")).thenReturn("Jongno-gu (Special Zone)");
        when(feature.getAttribute("the_geom")).thenReturn(null);
    }
}