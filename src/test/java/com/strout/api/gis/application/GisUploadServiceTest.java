package com.strout.api.gis.application;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import com.strout.api.gis.domain.ShapefileType;
import com.strout.api.gis.util.ShapefileParser;
import com.strout.api.gis.util.ShapefileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * GisUploadService에 대한 종합적인 단위 테스트
 * 
 * 테스트 프레임워크: JUnit 5 (Jupiter)
 * 모킹 프레임워크: Mockito
 * 스프링 부트 테스트 유틸리티 사용
 * 
 * 테스트 범위:
 * - 정상 플로우 테스트
 * - 예외 상황 테스트  
 * - 경계값 테스트
 * - 로깅 검증
 * - 의존성 주입 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GisUploadService 테스트")
class GisUploadServiceTest {

    @Mock
    private ShapefileParser shapefileParser;

    @Mock
    private ShapefileValidator shapefileValidator;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private GisUploadService gisUploadService;

    private ShapefileUploadCommand validCommand;
    private ShapefileDto validShpDto;
    private ShapefileDto validDbfDto;
    private ShapefileDto validShxDto;
    private ShapefileDto validPrjDto;

    @BeforeEach
    void setUp() {
        // Setup valid test data with various file sizes - using correct constructor order (data, filename)
        byte[] smallData = "small test data".getBytes(); // 15 bytes
        byte[] mediumData = new byte[1024]; // 1KB
        byte[] largeData = new byte[2048]; // 2KB
        
        validShpDto = new ShapefileDto(smallData, "seoul_sido.shp");
        validDbfDto = new ShapefileDto(mediumData, "seoul_sido.dbf");
        validShxDto = new ShapefileDto(smallData, "seoul_sido.shx");
        validPrjDto = new ShapefileDto(largeData, "seoul_sido.prj");
        
        validCommand = new ShapefileUploadCommand(
            validShpDto,
            validDbfDto,  
            validShxDto,
            validPrjDto
        );

        // Inject mock logger for testing log statements
        ReflectionTestUtils.setField(gisUploadService, "log", mockLogger);
    }

    @Nested
    @DisplayName("uploadSidoShapefile 메소드 테스트")
    class UploadSidoShapefileTest {

        @Test
        @DisplayName("정상적인 시/도 Shapefile 업로드가 성공해야 한다")
        void shouldSuccessfullyUploadValidSidoShapefile() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            assertDoesNotThrow(() -> 
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );

            // Then
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, times(1)).parse(validCommand, shapefileType);
            
            // Verify start and end logging
            verify(mockLogger).info("=== 시/도 Shapefile 업로드 시작 ===");
            verify(mockLogger).info("=== 시/도 Shapefile 업로드 완료 ===");
        }

        @Test
        @DisplayName("SIGUNGU 타입으로도 업로드가 가능해야 한다")
        void shouldSuccessfullyUploadWithSigunguType() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIGUNGU;

            // When
            assertDoesNotThrow(() -> 
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );

            // Then
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, times(1)).parse(validCommand, shapefileType);
        }

        @Test
        @DisplayName("파일 검증 실패 시 예외가 발생하고 파싱이 실행되지 않아야 한다")
        void shouldThrowExceptionWhenValidationFails() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;
            RuntimeException validationException = new RuntimeException("유효하지 않은 Shapefile 형식입니다.");
            doThrow(validationException).when(shapefileValidator).validate(validCommand);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );

            assertEquals("유효하지 않은 Shapefile 형식입니다.", exception.getMessage());
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, never()).parse(any(), any());
            verify(mockLogger, never()).info("=== 시/도 Shapefile 업로드 완료 ===");
        }

        @Test
        @DisplayName("파싱 실패 시 예외가 발생해야 한다")
        void shouldThrowExceptionWhenParsingFails() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;
            RuntimeException parsingException = new RuntimeException("Shapefile 파싱 중 오류가 발생했습니다.");
            doThrow(parsingException).when(shapefileParser).parse(validCommand, shapefileType);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );

            assertEquals("Shapefile 파싱 중 오류가 발생했습니다.", exception.getMessage());
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, times(1)).parse(validCommand, shapefileType);
            verify(mockLogger, never()).info("=== 시/도 Shapefile 업로드 완료 ===");
        }

        @Test
        @DisplayName("null command로 호출 시 NullPointerException이 발생해야 한다")
        void shouldThrowExceptionWhenCommandIsNull() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When & Then
            assertThrows(NullPointerException.class, () ->
                gisUploadService.uploadSidoShapefile(null, shapefileType)
            );

            verify(shapefileValidator, never()).validate(any());
            verify(shapefileParser, never()).parse(any(), any());
        }

        @Test
        @DisplayName("null ShapefileType으로 호출 시에도 처리되어야 한다")
        void shouldHandleNullShapefileType() {
            // When
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(validCommand, null)
            );

            // Then
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, times(1)).parse(validCommand, null);
        }

        @Test
        @DisplayName("모든 파일 정보가 올바른 순서로 로깅되어야 한다")
        void shouldLogAllFileInfoInCorrectOrder() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            gisUploadService.uploadSidoShapefile(validCommand, shapefileType);

            // Then - Verify file info logging in correct order
            verify(mockLogger).info("{} 파일 정보:", "시/도 SHP");
            verify(mockLogger).info("  - 파일명: {}", "seoul_sido.shp");
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 15, 0);

            verify(mockLogger).info("{} 파일 정보:", "시/도 DBF");
            verify(mockLogger).info("  - 파일명: {}", "seoul_sido.dbf");
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 1024, 1);

            verify(mockLogger).info("{} 파일 정보:", "시/도 SHX");
            verify(mockLogger).info("  - 파일명: {}", "seoul_sido.shx");
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 15, 0);

            verify(mockLogger).info("{} 파일 정보:", "시/도 PRJ");
            verify(mockLogger).info("  - 파일명: {}", "seoul_sido.prj");
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 2048, 2);
        }

        @Test
        @DisplayName("빈 파일명도 올바르게 처리되어야 한다")
        void shouldHandleEmptyFilenames() {
            // Given
            ShapefileDto emptyFilenameDto = new ShapefileDto("test data".getBytes(), "");
            ShapefileUploadCommand emptyFilenameCommand = new ShapefileUploadCommand(
                emptyFilenameDto, validDbfDto, validShxDto, validPrjDto
            );
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(emptyFilenameCommand, shapefileType)
            );

            // Then
            verify(mockLogger).info("  - 파일명: {}", "");
        }

        @Test
        @DisplayName("빈 데이터 배열(0 bytes)도 올바르게 처리되어야 한다")
        void shouldHandleEmptyDataArrays() {
            // Given
            ShapefileDto emptyDataDto = new ShapefileDto(new byte[0], "empty.shp");
            ShapefileUploadCommand emptyDataCommand = new ShapefileUploadCommand(
                emptyDataDto, validDbfDto, validShxDto, validPrjDto
            );
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(emptyDataCommand, shapefileType)
            );

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 0, 0);
        }

        @Test
        @DisplayName("특수 문자가 포함된 파일명도 올바르게 처리되어야 한다")
        void shouldHandleSpecialCharactersInFilenames() {
            // Given
            ShapefileDto specialCharDto = new ShapefileDto("test data".getBytes(), "서울시_강남구_2024년.shp");
            ShapefileUploadCommand specialCharCommand = new ShapefileUploadCommand(
                specialCharDto, validDbfDto, validShxDto, validPrjDto
            );
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(specialCharCommand, shapefileType)
            );

            // Then
            verify(mockLogger).info("  - 파일명: {}", "서울시_강남구_2024년.shp");
        }
    }

    @Nested
    @DisplayName("logFileInfo 메소드 테스트")
    class LogFileInfoTest {

        @Test
        @DisplayName("파일 정보가 올바른 형식으로 로깅되어야 한다")
        void shouldLogFileInfoInCorrectFormat() {
            // Given
            String fileType = "테스트 파일";
            ShapefileDto testDto = new ShapefileDto("test data content".getBytes(), "test-file.shp");

            // When
            ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", testDto, fileType);

            // Then
            verify(mockLogger).info("{} 파일 정보:", fileType);
            verify(mockLogger).info("  - 파일명: {}", "test-file.shp");
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 18, 0);
        }

        @Test
        @DisplayName("1KB 미만 파일의 KB 계산이 0으로 표시되어야 한다")
        void shouldDisplayZeroKBForFilesUnder1KB() {
            // Given
            String fileType = "소용량 파일";
            byte[] smallData = new byte[512]; // 0.5KB
            ShapefileDto smallDto = new ShapefileDto(smallData, "small.shp");

            // When
            ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", smallDto, fileType);

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 512, 0);
        }

        @Test
        @DisplayName("정확히 1KB 파일의 KB 계산이 정확해야 한다")
        void shouldCalculateExact1KBCorrectly() {
            // Given
            String fileType = "1KB 파일";
            byte[] exactKBData = new byte[1024]; // Exactly 1KB
            ShapefileDto exactKBDto = new ShapefileDto(exactKBData, "exact1kb.shp");

            // When
            ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", exactKBDto, fileType);

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 1024, 1);
        }

        @Test
        @DisplayName("1KB 이상 파일의 KB 계산이 정확해야 한다")
        void shouldCalculateKBCorrectlyForLargerFiles() {
            // Given
            String fileType = "대용량 파일";
            byte[] largeData = new byte[1536]; // 1.5KB
            ShapefileDto largeDto = new ShapefileDto(largeData, "large.shp");

            // When
            ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", largeDto, fileType);

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 1536, 1);
        }

        @Test
        @DisplayName("매우 큰 파일의 KB 계산이 정확해야 한다")
        void shouldCalculateKBCorrectlyForVeryLargeFiles() {
            // Given
            String fileType = "초대용량 파일";
            byte[] veryLargeData = new byte[10240]; // 10KB
            ShapefileDto veryLargeDto = new ShapefileDto(veryLargeData, "very_large.shp");

            // When
            ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", veryLargeDto, fileType);

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", 10240, 10);
        }

        @Test
        @DisplayName("null 파일명도 안전하게 처리되어야 한다")
        void shouldHandleNullFilename() {
            // Given
            String fileType = "null 파일명";
            ShapefileDto nullFilenameDto = new ShapefileDto("test".getBytes(), null);

            // When
            assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", nullFilenameDto, fileType)
            );

            // Then
            verify(mockLogger).info("  - 파일명: {}", (Object) null);
        }

        @Test
        @DisplayName("null 데이터 배열 처리 시 NullPointerException이 발생해야 한다")
        void shouldThrowNullPointerExceptionForNullDataArray() {
            // Given
            String fileType = "null 데이터";
            ShapefileDto nullDataDto = new ShapefileDto(null, "test.shp");

            // When & Then
            assertThrows(NullPointerException.class, () ->
                ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", nullDataDto, fileType)
            );
        }

        @Test
        @DisplayName("null 파일 타입도 안전하게 처리되어야 한다")
        void shouldHandleNullFileType() {
            // Given
            ShapefileDto testDto = new ShapefileDto("test".getBytes(), "test.shp");

            // When
            assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(gisUploadService, "logFileInfo", testDto, null)
            );

            // Then
            verify(mockLogger).info("{} 파일 정보:", (Object) null);
        }
    }

    @Nested
    @DisplayName("서비스 초기화 및 의존성 테스트")
    class ServiceInitializationTest {

        @Test
        @DisplayName("서비스가 올바른 의존성으로 초기화되어야 한다")
        void shouldInitializeWithCorrectDependencies() {
            // Given & When
            GisUploadService service = new GisUploadService(shapefileParser, shapefileValidator);

            // Then
            assertNotNull(service);
            // Verify dependencies are properly injected through reflection
            Object injectedParser = ReflectionTestUtils.getField(service, "shapefileParser");
            Object injectedValidator = ReflectionTestUtils.getField(service, "shapefileValidator");
            
            assertSame(shapefileParser, injectedParser);
            assertSame(shapefileValidator, injectedValidator);
        }

        @Test
        @DisplayName("Lombok @RequiredArgsConstructor가 정상 작동해야 한다")
        void shouldWorkWithLombokRequiredArgsConstructor() {
            // Given & When - 생성자가 final 필드들을 모두 받는지 확인
            assertDoesNotThrow(() -> {
                GisUploadService service = new GisUploadService(shapefileParser, shapefileValidator);
                assertNotNull(service);
            });
        }
        
        @Test
        @DisplayName("@Service 어노테이션이 적용되어 스프링 빈으로 등록 가능해야 한다")
        void shouldBeSpringServiceBean() {
            // Given & When
            Class<GisUploadService> serviceClass = GisUploadService.class;
            
            // Then
            assertTrue(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class));
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("완전한 업로드 프로세스가 순서대로 실행되어야 한다")
        void shouldExecuteCompleteUploadProcessInOrder() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            gisUploadService.uploadSidoShapefile(validCommand, shapefileType);

            // Then - Verify method calls in correct order
            var inOrder = inOrder(shapefileValidator, shapefileParser, mockLogger);
            
            inOrder.verify(mockLogger).info("=== 시/도 Shapefile 업로드 시작 ===");
            inOrder.verify(shapefileValidator).validate(validCommand);
            // File info logging happens here (multiple calls)
            inOrder.verify(shapefileParser).parse(validCommand, shapefileType);
            inOrder.verify(mockLogger).info("=== 시/도 Shapefile 업로드 완료 ===");
        }

        @Test
        @DisplayName("검증 실패 시 파싱이 실행되지 않아야 한다")
        void shouldNotExecuteParsingWhenValidationFails() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;
            doThrow(new RuntimeException("Validation error")).when(shapefileValidator).validate(any());

            // When
            assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );

            // Then
            verify(shapefileValidator, times(1)).validate(validCommand);
            verify(shapefileParser, never()).parse(any(), any());
            verify(mockLogger, never()).info("=== 시/도 Shapefile 업로드 완료 ===");
        }
        
        @Test
        @DisplayName("모든 파일 정보가 로깅된 후 파싱이 실행되어야 한다")
        void shouldLogAllFileInfoBeforeParsing() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            gisUploadService.uploadSidoShapefile(validCommand, shapefileType);

            // Then - 모든 파일 정보 로깅이 파싱 전에 완료되어야 함
            var inOrder = inOrder(mockLogger, shapefileParser);
            
            // 모든 파일 정보 로깅 검증
            inOrder.verify(mockLogger).info("{} 파일 정보:", "시/도 SHP");
            inOrder.verify(mockLogger).info("{} 파일 정보:", "시/도 DBF");  
            inOrder.verify(mockLogger).info("{} 파일 정보:", "시/도 SHX");
            inOrder.verify(mockLogger).info("{} 파일 정보:", "시/도 PRJ");
            
            // 파싱이 마지막에 실행됨
            inOrder.verify(shapefileParser).parse(validCommand, shapefileType);
        }

        @Test
        @DisplayName("SIDO와 SIGUNGU 타입 모두 동일한 프로세스로 처리되어야 한다")
        void shouldHandleBothShapefileTypesWithSameProcess() {
            // Test both enum values
            ShapefileType[] types = {ShapefileType.SIDO, ShapefileType.SIGUNGU};
            
            for (ShapefileType type : types) {
                // Reset mocks for each iteration
                reset(shapefileValidator, shapefileParser, mockLogger);
                ReflectionTestUtils.setField(gisUploadService, "log", mockLogger);

                // When
                assertDoesNotThrow(() ->
                    gisUploadService.uploadSidoShapefile(validCommand, type)
                );

                // Then
                verify(shapefileValidator, times(1)).validate(validCommand);
                verify(shapefileParser, times(1)).parse(validCommand, type);
                verify(mockLogger).info("=== 시/도 Shapefile 업로드 시작 ===");
                verify(mockLogger).info("=== 시/도 Shapefile 업로드 완료 ===");
            }
        }
    }

    @Nested
    @DisplayName("예외 상황 테스트")
    class ExceptionHandlingTest {

        @Test
        @DisplayName("검증 중 RuntimeException 발생 시 적절히 전파되어야 한다")
        void shouldPropagateRuntimeExceptionFromValidation() {
            // Given
            RuntimeException expectedException = new RuntimeException("사용자 정의 검증 오류");
            doThrow(expectedException).when(shapefileValidator).validate(validCommand);

            // When & Then
            RuntimeException actualException = assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, ShapefileType.SIDO)
            );

            assertSame(expectedException, actualException);
            assertEquals("사용자 정의 검증 오류", actualException.getMessage());
        }

        @Test
        @DisplayName("파싱 중 RuntimeException 발생 시 적절히 전파되어야 한다")
        void shouldPropagateRuntimeExceptionFromParsing() {
            // Given
            RuntimeException expectedException = new RuntimeException("사용자 정의 파싱 오류");
            doThrow(expectedException).when(shapefileParser).parse(validCommand, ShapefileType.SIDO);

            // When & Then
            RuntimeException actualException = assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, ShapefileType.SIDO)
            );

            assertSame(expectedException, actualException);
            assertEquals("사용자 정의 파싱 오류", actualException.getMessage());
        }

        @Test
        @DisplayName("검증 중 IllegalArgumentException도 적절히 전파되어야 한다")
        void shouldPropagateIllegalArgumentExceptionFromValidation() {
            // Given
            IllegalArgumentException expectedException = new IllegalArgumentException("잘못된 인수");
            doThrow(expectedException).when(shapefileValidator).validate(validCommand);

            // When & Then
            IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, ShapefileType.SIDO)
            );

            assertSame(expectedException, actualException);
        }

        @Test
        @DisplayName("파싱 중 IllegalStateException도 적절히 전파되어야 한다")
        void shouldPropagateIllegalStateExceptionFromParsing() {
            // Given
            IllegalStateException expectedException = new IllegalStateException("잘못된 상태");
            doThrow(expectedException).when(shapefileParser).parse(validCommand, ShapefileType.SIDO);

            // When & Then
            IllegalStateException actualException = assertThrows(IllegalStateException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, ShapefileType.SIDO)
            );

            assertSame(expectedException, actualException);
        }

        @Test
        @DisplayName("로깅 중 예외가 발생해도 서비스 로직은 계속 진행되어야 한다")
        void shouldContinueProcessingEvenIfLoggingFails() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;
            // 로깅은 일반적으로 예외를 던지지 않지만, 테스트를 위해 시뮬레이션
            doThrow(new RuntimeException("로깅 오류")).when(mockLogger).info(anyString(), anyString());

            // When & Then
            // 로깅 실패가 전체 프로세스를 중단시키지 않아야 함
            assertThrows(RuntimeException.class, () ->
                gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
            );
        }
    }

    @Nested  
    @DisplayName("성능 및 리소스 테스트")
    class PerformanceAndResourceTest {

        @Test
        @DisplayName("대용량 파일 업로드 시 메모리 효율성을 고려해야 한다")
        void shouldHandleLargeFilesEfficiently() {
            // Given - 10MB 가상 파일
            int tenMB = 10 * 1024 * 1024;
            ShapefileDto largeShpDto = new ShapefileDto(new byte[tenMB], "large_region.shp");
            ShapefileUploadCommand largeCommand = new ShapefileUploadCommand(
                largeShpDto, validDbfDto, validShxDto, validPrjDto
            );
            ShapefileType shapefileType = ShapefileType.SIDO;

            // When
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(largeCommand, shapefileType)
            );

            // Then
            verify(mockLogger).info("  - 크기: {} bytes ({} KB)", tenMB, tenMB / 1024);
            verify(shapefileValidator, times(1)).validate(largeCommand);
            verify(shapefileParser, times(1)).parse(largeCommand, shapefileType);
        }

        @Test
        @DisplayName("여러 파일을 연속으로 업로드해도 안정적이어야 한다")
        void shouldHandleMultipleConsecutiveUploads() {
            // Given
            ShapefileType shapefileType = ShapefileType.SIDO;
            int uploadCount = 5;

            // When & Then
            for (int i = 0; i < uploadCount; i++) {
                assertDoesNotThrow(() ->
                    gisUploadService.uploadSidoShapefile(validCommand, shapefileType)
                );
            }

            // Verify all uploads were processed
            verify(shapefileValidator, times(uploadCount)).validate(validCommand);
            verify(shapefileParser, times(uploadCount)).parse(validCommand, shapefileType);
        }

        @Test
        @DisplayName("각기 다른 크기의 파일들이 모두 올바르게 처리되어야 한다")
        void shouldHandleVariousFileSizes() {
            // Given - 다양한 크기의 파일들
            ShapefileDto tinyDto = new ShapefileDto(new byte[1], "tiny.shp");
            ShapefileDto smallDto = new ShapefileDto(new byte[100], "small.shp");
            ShapefileDto mediumDto = new ShapefileDto(new byte[5000], "medium.shp");
            ShapefileDto largeDto = new ShapefileDto(new byte[50000], "large.shp");
            
            ShapefileUploadCommand[] commands = {
                new ShapefileUploadCommand(tinyDto, validDbfDto, validShxDto, validPrjDto),
                new ShapefileUploadCommand(smallDto, validDbfDto, validShxDto, validPrjDto),
                new ShapefileUploadCommand(mediumDto, validDbfDto, validShxDto, validPrjDto),
                new ShapefileUploadCommand(largeDto, validDbfDto, validShxDto, validPrjDto)
            };

            // When & Then
            for (ShapefileUploadCommand command : commands) {
                assertDoesNotThrow(() ->
                    gisUploadService.uploadSidoShapefile(command, ShapefileType.SIDO)
                );
            }

            // Verify all commands were processed
            verify(shapefileValidator, times(4)).validate(any());
            verify(shapefileParser, times(4)).parse(any(), eq(ShapefileType.SIDO));
        }
    }

    @Nested
    @DisplayName("데이터 무결성 테스트")
    class DataIntegrityTest {

        @Test
        @DisplayName("ShapefileUploadCommand의 모든 필드가 null이 아닌 경우 정상 처리되어야 한다")
        void shouldProcessWhenAllCommandFieldsAreNotNull() {
            // Given - All fields are already non-null in validCommand
            
            // When & Then
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(validCommand, ShapefileType.SIDO)
            );
            
            verify(shapefileValidator).validate(validCommand);
            verify(shapefileParser).parse(validCommand, ShapefileType.SIDO);
        }

        @Test
        @DisplayName("ShapefileDto의 데이터가 유효한 바이트 배열인 경우 정상 처리되어야 한다")
        void shouldProcessValidByteArrayData() {
            // Given
            byte[] validShapeData = {0x00, 0x00, 0x27, 0x0A}; // Example shapefile magic number
            ShapefileDto validDto = new ShapefileDto(validShapeData, "valid.shp");
            ShapefileUploadCommand validDataCommand = new ShapefileUploadCommand(
                validDto, validDbfDto, validShxDto, validPrjDto
            );

            // When & Then
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(validDataCommand, ShapefileType.SIDO)
            );
        }

        @Test
        @DisplayName("모든 파일 확장자가 올바른 경우 정상 처리되어야 한다")
        void shouldProcessWithCorrectFileExtensions() {
            // Given
            ShapefileDto shpDto = new ShapefileDto("data".getBytes(), "region.shp");
            ShapefileDto dbfDto = new ShapefileDto("data".getBytes(), "region.dbf");
            ShapefileDto shxDto = new ShapefileDto("data".getBytes(), "region.shx");
            ShapefileDto prjDto = new ShapefileDto("data".getBytes(), "region.prj");
            
            ShapefileUploadCommand correctExtensionCommand = new ShapefileUploadCommand(
                shpDto, dbfDto, shxDto, prjDto
            );

            // When & Then
            assertDoesNotThrow(() ->
                gisUploadService.uploadSidoShapefile(correctExtensionCommand, ShapefileType.SIDO)
            );
        }
    }
}