package com.strout.api.gis.infrastructure.geotools;

import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.application.command.dto.ShapefileDto;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.AttributeType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ShapefileParsingSupport class.
 * Tests all three public methods with various scenarios including happy paths, edge cases, and error conditions.
 * 
 * Testing Framework: JUnit 5 with Mockito
 */
@ExtendWith(MockitoExtension.class)
class ShapefileParsingSupportTest {

    @InjectMocks
    private ShapefileParsingSupport shapefileParsingSupport;

    @TempDir
    Path tempDir;

    private ShapefileUploadCommand validCommand;
    private byte[] testShpData;
    private byte[] testDbfData;
    private byte[] testShxData;
    private byte[] testPrjData;

    @BeforeEach
    void setUp() {
        // Create test data
        testShpData = "mock shapefile data".getBytes();
        testDbfData = "mock database data".getBytes();
        testShxData = "mock index data".getBytes();
        testPrjData = "mock projection data".getBytes();

        // Create valid command with real ShapefileDto instances
        ShapefileDto shpDto = new ShapefileDto(testShpData, "test.shp");
        ShapefileDto dbfDto = new ShapefileDto(testDbfData, "test.dbf");
        ShapefileDto shxDto = new ShapefileDto(testShxData, "test.shx");
        ShapefileDto prjDto = new ShapefileDto(testPrjData, "test.prj");
        
        validCommand = new ShapefileUploadCommand(shpDto, dbfDto, shxDto, prjDto);
    }

    // ==================== createTemporaryShapefileSet Tests ====================

    @Test
    void testCreateTemporaryShapefileSet_Success() throws IOException {
        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().endsWith(".shp"));
        assertTrue(result.getName().startsWith("shapefile_"));
        
        // Verify all associated files exist
        String baseName = result.getName().substring(0, result.getName().lastIndexOf("."));
        File parentDir = result.getParentFile();
        
        File dbfFile = new File(parentDir, baseName + ".dbf");
        File shxFile = new File(parentDir, baseName + ".shx");
        File prjFile = new File(parentDir, baseName + ".prj");
        
        assertTrue(dbfFile.exists());
        assertTrue(shxFile.exists());
        assertTrue(prjFile.exists());
        
        // Verify file contents
        assertArrayEquals(testShpData, Files.readAllBytes(result.toPath()));
        assertArrayEquals(testDbfData, Files.readAllBytes(dbfFile.toPath()));
        assertArrayEquals(testShxData, Files.readAllBytes(shxFile.toPath()));
        assertArrayEquals(testPrjData, Files.readAllBytes(prjFile.toPath()));
    }

    @Test
    void testCreateTemporaryShapefileSet_UniqueFileNames() throws IOException {
        // Act - create multiple shapefile sets
        File result1 = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);
        
        // Small delay to ensure different timestamp
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        File result2 = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);

        // Assert - filenames should be unique
        assertNotEquals(result1.getName(), result2.getName());
        assertNotEquals(result1.getParentFile().getName(), result2.getParentFile().getName());
    }

    @Test
    void testCreateTemporaryShapefileSet_NullCommand() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            shapefileParsingSupport.createTemporaryShapefileSet(null);
        });
    }

    @Test
    void testCreateTemporaryShapefileSet_EmptyData() throws IOException {
        // Arrange
        ShapefileDto emptyShpDto = new ShapefileDto(new byte[0], "empty.shp");
        ShapefileDto emptyDbfDto = new ShapefileDto(new byte[0], "empty.dbf");
        ShapefileDto emptyShxDto = new ShapefileDto(new byte[0], "empty.shx");
        ShapefileDto emptyPrjDto = new ShapefileDto(new byte[0], "empty.prj");
        
        ShapefileUploadCommand emptyCommand = new ShapefileUploadCommand(
            emptyShpDto, emptyDbfDto, emptyShxDto, emptyPrjDto);

        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(emptyCommand);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(0, result.length());
    }

    @Test
    void testCreateTemporaryShapefileSet_LargeData() throws IOException {
        // Arrange - create large data arrays (1MB each)
        byte[] largeData = new byte[1024 * 1024];
        Arrays.fill(largeData, (byte) 'A');
        
        ShapefileDto largeShpDto = new ShapefileDto(largeData, "large.shp");
        ShapefileDto largeDbfDto = new ShapefileDto(largeData, "large.dbf");
        ShapefileDto largeShxDto = new ShapefileDto(largeData, "large.shx");
        ShapefileDto largePrjDto = new ShapefileDto(largeData, "large.prj");
        
        ShapefileUploadCommand largeCommand = new ShapefileUploadCommand(
            largeShpDto, largeDbfDto, largeShxDto, largePrjDto);

        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(largeCommand);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(largeData.length, result.length());
    }

    @Test
    void testCreateTemporaryShapefileSet_FileNamingConvention() throws IOException {
        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);

        // Assert - verify file naming follows expected pattern
        String fileName = result.getName();
        assertTrue(fileName.matches("shapefile_\\d+_\\d+\\.shp"));
        
        // Verify the unique ID contains timestamp and thread ID components
        String uniqueId = fileName.substring(0, fileName.lastIndexOf("."));
        assertTrue(uniqueId.startsWith("shapefile_"));
        
        // Split and verify format: shapefile_timestamp_threadId
        String[] parts = uniqueId.split("_");
        assertEquals(3, parts.length);
        assertEquals("shapefile", parts[0]);
        assertTrue(parts[1].matches("\\d+"));  // timestamp
        assertTrue(parts[2].matches("\\d+"));  // thread ID
    }

    @Test
    void testCreateTemporaryShapefileSet_BinaryDataHandling() throws IOException {
        // Arrange - create binary data with various byte values
        byte[] binaryData = new byte[256];
        for (int i = 0; i < 256; i++) {
            binaryData[i] = (byte) i;
        }
        
        ShapefileDto binaryShpDto = new ShapefileDto(binaryData, "binary.shp");
        ShapefileDto binaryDbfDto = new ShapefileDto(binaryData, "binary.dbf");
        ShapefileDto binaryShxDto = new ShapefileDto(binaryData, "binary.shx");
        ShapefileDto binaryPrjDto = new ShapefileDto(binaryData, "binary.prj");
        
        ShapefileUploadCommand binaryCommand = new ShapefileUploadCommand(
            binaryShpDto, binaryDbfDto, binaryShxDto, binaryPrjDto);

        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(binaryCommand);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        
        byte[] writtenData = Files.readAllBytes(result.toPath());
        assertArrayEquals(binaryData, writtenData);
    }

    @Test
    void testCreateTemporaryShapefileSet_ThreadSafety() throws InterruptedException {
        // Arrange
        final int threadCount = 10;
        final boolean[] results = new boolean[threadCount];
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        // Act - create threads that simultaneously create shapefiles
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    File result = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);
                    results[index] = result != null && result.exists();
                } catch (Exception e) {
                    exceptions[index] = e;
                    results[index] = false;
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - all operations should succeed
        for (int i = 0; i < threadCount; i++) {
            if (exceptions[i] != null) {
                fail("Thread " + i + " threw exception: " + exceptions[i].getMessage());
            }
            assertTrue(results[i], "Thread " + i + " failed to create shapefile");
        }
    }

    @Test
    void testCreateTemporaryShapefileSet_FileCleanupScheduled() throws IOException {
        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(validCommand);
        
        // Assert - verify files are created and exist initially
        assertNotNull(result);
        assertTrue(result.exists());
        
        String baseName = result.getName().substring(0, result.getName().lastIndexOf("."));
        File parentDir = result.getParentFile();
        
        File dbfFile = new File(parentDir, baseName + ".dbf");
        File shxFile = new File(parentDir, baseName + ".shx");
        File prjFile = new File(parentDir, baseName + ".prj");
        
        assertTrue(dbfFile.exists());
        assertTrue(shxFile.exists());
        assertTrue(prjFile.exists());
        assertTrue(parentDir.exists());
        
        // Note: We cannot easily test deleteOnExit behavior in unit tests
        // as it only triggers on JVM shutdown, but we can verify the files exist
        // and would be marked for deletion
    }

    // ==================== createDataStore Tests ====================

    @Test
    void testCreateDataStore_Success() throws Exception {
        // Arrange - create a temporary shapefile
        File tempShpFile = tempDir.resolve("test.shp").toFile();
        tempShpFile.createNewFile();

        // Act
        ShapefileDataStore result = shapefileParsingSupport.createDataStore(tempShpFile);

        // Assert
        assertNotNull(result);
        assertEquals(Charset.forName("EUC-KR"), result.getCharset());
        
        // Cleanup
        result.dispose();
    }

    @Test
    void testCreateDataStore_FileNotExists() {
        // Arrange
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.shp");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shapefileParsingSupport.createDataStore(nonExistentFile);
        });
    }

    @Test
    void testCreateDataStore_NullFile() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            shapefileParsingSupport.createDataStore(null);
        });
    }

    @Test
    void testCreateDataStore_InvalidFile_Directory() {
        // Arrange - create a directory instead of a file
        File directory = tempDir.resolve("testdir").toFile();
        directory.mkdir();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shapefileParsingSupport.createDataStore(directory);
        });
    }

    @Test
    void testCreateDataStore_CharsetSetting() throws Exception {
        // Arrange
        File tempShpFile = tempDir.resolve("charset_test.shp").toFile();
        tempShpFile.createNewFile();

        // Act
        ShapefileDataStore result = shapefileParsingSupport.createDataStore(tempShpFile);

        // Assert - verify EUC-KR charset is set
        assertEquals("EUC-KR", result.getCharset().name());
        assertEquals(Charset.forName("EUC-KR"), result.getCharset());
        
        // Cleanup
        result.dispose();
    }

    @Test
    void testCreateDataStore_EmptyFile() throws Exception {
        // Arrange - create an empty shapefile
        File emptyShpFile = tempDir.resolve("empty.shp").toFile();
        emptyShpFile.createNewFile();

        // Act & Assert - should handle empty files gracefully or throw appropriate exception
        assertThrows(Exception.class, () -> {
            shapefileParsingSupport.createDataStore(emptyShpFile);
        });
    }

    @Test
    void testCreateDataStore_InvalidFileExtension() {
        // Arrange - create file with wrong extension
        File txtFile = tempDir.resolve("test.txt").toFile();
        try {
            txtFile.createNewFile();
        } catch (IOException e) {
            fail("Failed to create test file");
        }

        // Act & Assert
        assertThrows(Exception.class, () -> {
            shapefileParsingSupport.createDataStore(txtFile);
        });
    }

    // ==================== logBasicSchemaInfo Tests ====================

    @Test
    void testLogBasicSchemaInfo_CompleteInfo() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        CoordinateReferenceSystem mockCrs = mock(CoordinateReferenceSystem.class);
        AttributeDescriptor mockAttr = mock(AttributeDescriptor.class);
        AttributeType mockAttrType = mock(AttributeType.class);
        Name mockName = mock(Name.class);
        
        when(mockSchema.getTypeName()).thenReturn("TestFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(mockCrs);
        when(mockSchema.getAttributeCount()).thenReturn(2);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList(mockAttr));
        when(mockFeatures.size()).thenReturn(100);
        when(mockCrs.toString()).thenReturn("EPSG:4326");
        
        // Setup attribute descriptor mocks
        when(mockAttr.getLocalName()).thenReturn("testAttribute");
        when(mockAttr.getType()).thenReturn(mockAttrType);
        when(mockAttrType.getBinding()).thenReturn((Class) String.class);
        when(mockAttrType.getName()).thenReturn(mockName);
        when(mockName.toString()).thenReturn("testAttributeType");

        // Act
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "Test");
        });

        // Assert - verify method calls were made
        verify(mockSchema).getTypeName();
        verify(mockSchema).getCoordinateReferenceSystem();
        verify(mockSchema).getAttributeCount();
        verify(mockSchema).getAttributeDescriptors();
        verify(mockFeatures).size();
    }

    @Test
    void testLogBasicSchemaInfo_NullSchema() {
        // Arrange
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        when(mockFeatures.size()).thenReturn(0);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            shapefileParsingSupport.logBasicSchemaInfo(null, mockFeatures, "Test");
        });
    }

    @Test
    void testLogBasicSchemaInfo_NullFeatures() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        when(mockSchema.getTypeName()).thenReturn("TestFeatureType");
        when(mockSchema.getAttributeCount()).thenReturn(0);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, null, "Test");
        });
    }

    @Test
    void testLogBasicSchemaInfo_EmptyDataType() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("TestFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(0);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());
        when(mockFeatures.size()).thenReturn(0);

        // Act - should not throw exception with empty dataType
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "");
        });
    }

    @Test
    void testLogBasicSchemaInfo_NullDataType() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("TestFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(0);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());
        when(mockFeatures.size()).thenReturn(0);

        // Act & Assert - should handle null dataType gracefully
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, null);
        });
    }

    @Test
    void testLogBasicSchemaInfo_ZeroFeatures() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("EmptyFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(3);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());
        when(mockFeatures.size()).thenReturn(0);

        // Act
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "Empty");
        });

        // Assert
        verify(mockFeatures).size();
    }

    @Test
    void testLogBasicSchemaInfo_LargeFeatureCount() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("LargeFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(10);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());
        when(mockFeatures.size()).thenReturn(1000000); // 1 million features

        // Act
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "Large");
        });

        // Assert
        verify(mockFeatures).size();
    }

    @Test
    void testLogBasicSchemaInfo_MultipleAttributes() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        AttributeDescriptor attr1 = mock(AttributeDescriptor.class);
        AttributeDescriptor attr2 = mock(AttributeDescriptor.class);
        AttributeDescriptor attr3 = mock(AttributeDescriptor.class);
        AttributeType attrType1 = mock(AttributeType.class);
        AttributeType attrType2 = mock(AttributeType.class);
        AttributeType attrType3 = mock(AttributeType.class);
        Name name1 = mock(Name.class);
        Name name2 = mock(Name.class);
        Name name3 = mock(Name.class);
        
        when(mockSchema.getTypeName()).thenReturn("MultiAttrFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(3);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList(attr1, attr2, attr3));
        when(mockFeatures.size()).thenReturn(50);
        
        // Setup first attribute
        when(attr1.getLocalName()).thenReturn("id");
        when(attr1.getType()).thenReturn(attrType1);
        when(attrType1.getBinding()).thenReturn((Class) Integer.class);
        when(attrType1.getName()).thenReturn(name1);
        when(name1.toString()).thenReturn("IntegerType");
        
        // Setup second attribute
        when(attr2.getLocalName()).thenReturn("name");
        when(attr2.getType()).thenReturn(attrType2);
        when(attrType2.getBinding()).thenReturn((Class) String.class);
        when(attrType2.getName()).thenReturn(name2);
        when(name2.toString()).thenReturn("StringType");
        
        // Setup third attribute
        when(attr3.getLocalName()).thenReturn("geometry");
        when(attr3.getType()).thenReturn(attrType3);
        when(attrType3.getBinding()).thenReturn((Class) Object.class);
        when(attrType3.getName()).thenReturn(name3);
        when(name3.toString()).thenReturn("GeometryType");

        // Act
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "MultiAttr");
        });

        // Assert - verify all attributes were processed
        verify(attr1).getLocalName();
        verify(attr1).getType();
        verify(attr2).getLocalName();
        verify(attr2).getType();
        verify(attr3).getLocalName();
        verify(attr3).getType();
    }

    @Test
    void testLogBasicSchemaInfo_NullCoordinateReferenceSystem() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("NoCRSFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(1);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList());
        when(mockFeatures.size()).thenReturn(10);

        // Act - should handle null CRS gracefully
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "NoCRS");
        });

        // Assert
        verify(mockSchema).getCoordinateReferenceSystem();
    }

    @Test
    void testLogBasicSchemaInfo_EmptyAttributeList() {
        // Arrange
        SimpleFeatureType mockSchema = mock(SimpleFeatureType.class);
        SimpleFeatureCollection mockFeatures = mock(SimpleFeatureCollection.class);
        
        when(mockSchema.getTypeName()).thenReturn("NoAttrsFeatureType");
        when(mockSchema.getCoordinateReferenceSystem()).thenReturn(null);
        when(mockSchema.getAttributeCount()).thenReturn(0);
        when(mockSchema.getAttributeDescriptors()).thenReturn(Arrays.asList()); // Empty list
        when(mockFeatures.size()).thenReturn(5);

        // Act
        assertDoesNotThrow(() -> {
            shapefileParsingSupport.logBasicSchemaInfo(mockSchema, mockFeatures, "NoAttrs");
        });

        // Assert
        verify(mockSchema).getAttributeDescriptors();
    }

    // ==================== Annotation Tests ====================

    @Test
    void testComponent_Annotation() {
        // Assert - verify that the class is annotated with @Component
        assertTrue(ShapefileParsingSupport.class.isAnnotationPresent(org.springframework.stereotype.Component.class));
    }

    @Test
    void testSlf4j_Annotation() {
        // Assert - verify that the class is annotated with @Slf4j
        assertTrue(ShapefileParsingSupport.class.isAnnotationPresent(lombok.extern.slf4j.Slf4j.class));
    }

    // ==================== Integration-style Tests ====================

    @Test
    void testCreateTemporaryShapefileSet_WithSpecialCharactersInFilename() throws IOException {
        // Arrange - create DTOs with special characters in filenames
        ShapefileDto shpDto = new ShapefileDto(testShpData, "test-file_123.shp");
        ShapefileDto dbfDto = new ShapefileDto(testDbfData, "test-file_123.dbf");
        ShapefileDto shxDto = new ShapefileDto(testShxData, "test-file_123.shx");
        ShapefileDto prjDto = new ShapefileDto(testPrjData, "test-file_123.prj");
        
        ShapefileUploadCommand command = new ShapefileUploadCommand(shpDto, dbfDto, shxDto, prjDto);

        // Act
        File result = shapefileParsingSupport.createTemporaryShapefileSet(command);

        // Assert - the generated filename should still follow the pattern regardless of input filename
        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().matches("shapefile_\\d+_\\d+\\.shp"));
    }

    @Test
    void testCreateTemporaryShapefileSet_WithNullFilenames() throws IOException {
        // Arrange - create DTOs with null filenames
        ShapefileDto shpDto = new ShapefileDto(testShpData, null);
        ShapefileDto dbfDto = new ShapefileDto(testDbfData, null);
        ShapefileDto shxDto = new ShapefileDto(testShxData, null);
        ShapefileDto prjDto = new ShapefileDto(testPrjData, null);
        
        ShapefileUploadCommand command = new ShapefileUploadCommand(shpDto, dbfDto, shxDto, prjDto);

        // Act - should still work since the method doesn't use the filename from the DTO
        File result = shapefileParsingSupport.createTemporaryShapefileSet(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().matches("shapefile_\\d+_\\d+\\.shp"));
    }
}