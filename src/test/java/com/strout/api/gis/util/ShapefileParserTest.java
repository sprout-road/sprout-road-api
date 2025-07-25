package com.strout.api.gis.util;

import com.strout.api.gis.application.ShapefileParsingStrategy;
import com.strout.api.gis.application.command.ShapefileUploadCommand;
import com.strout.api.gis.domain.ShapefileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShapefileParser 단위 테스트")
class ShapefileParserTest {

    @Mock
    private ShapefileParsingStrategy mockStrategy1;
    
    @Mock
    private ShapefileParsingStrategy mockStrategy2;
    
    @Mock
    private ShapefileUploadCommand mockCommand;
    
    private ShapefileParser shapefileParser;
    
    @BeforeEach
    void setUp() {
        List<ShapefileParsingStrategy> strategies = Arrays.asList(mockStrategy1, mockStrategy2);
        shapefileParser = new ShapefileParser(strategies);
    }
    
    @Nested
    @DisplayName("parse 메서드 테스트")
    class ParseMethodTests {
        
        @Test
        @DisplayName("SIDO 타입에 대해 첫 번째 전략이 선택되어 파싱이 수행된다")
        void shouldParseWithFirstSupportingStrategyForSido() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenReturn(true);
            when(mockStrategy2.supports(type)).thenReturn(false);
            
            // When
            assertDoesNotThrow(() -> shapefileParser.parse(mockCommand, type));
            
            // Then
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1).parse(mockCommand);
            verify(mockStrategy2).supports(type);
            verify(mockStrategy2, never()).parse(any());
        }
        
        @Test
        @DisplayName("SIGUNGU 타입에 대해 두 번째 전략이 선택되어 파싱이 수행된다")
        void shouldParseWithSecondSupportingStrategyForSigungu() {
            // Given
            ShapefileType type = ShapefileType.SIGUNGU;
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(true);
            
            // When
            assertDoesNotThrow(() -> shapefileParser.parse(mockCommand, type));
            
            // Then
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1, never()).parse(any());
            verify(mockStrategy2).supports(type);
            verify(mockStrategy2).parse(mockCommand);
        }
        
        @Test
        @DisplayName("여러 전략이 동일한 타입을 지원할 때 첫 번째 전략이 선택된다")
        void shouldSelectFirstStrategyWhenMultipleSupport() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenReturn(true);
            when(mockStrategy2.supports(type)).thenReturn(true);
            
            // When
            assertDoesNotThrow(() -> shapefileParser.parse(mockCommand, type));
            
            // Then
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1).parse(mockCommand);
            verify(mockStrategy2).supports(type);
            verify(mockStrategy2, never()).parse(any());
        }
        
        @Test
        @DisplayName("지원하지 않는 타입에 대해 IllegalArgumentException이 발생한다")
        void shouldThrowExceptionForUnsupportedType() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 타입: " + type);
            
            verify(mockStrategy1).supports(type);
            verify(mockStrategy2).supports(type);
            verify(mockStrategy1, never()).parse(any());
            verify(mockStrategy2, never()).parse(any());
        }
        
        @Test
        @DisplayName("null 타입에 대해 예외가 발생한다")
        void shouldHandleNullType() {
            // Given
            ShapefileType type = null;
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 타입: null");
        }
        
        @Test
        @DisplayName("null 커맨드로 파싱을 시도할 때 전략의 parse 메서드가 호출된다")
        void shouldCallStrategyParseWithNullCommand() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            ShapefileUploadCommand nullCommand = null;
            when(mockStrategy1.supports(type)).thenReturn(true);
            
            // When
            assertDoesNotThrow(() -> shapefileParser.parse(nullCommand, type));
            
            // Then
            verify(mockStrategy1).parse(nullCommand);
        }
    }
    
    @Nested
    @DisplayName("생성자 및 초기화 테스트")
    class ConstructorTests {
        
        @Test
        @DisplayName("빈 전략 리스트로 초기화된 파서는 모든 타입에 대해 예외를 발생시킨다")
        void shouldThrowExceptionWithEmptyStrategyList() {
            // Given
            List<ShapefileParsingStrategy> emptyStrategies = Collections.emptyList();
            ShapefileParser parser = new ShapefileParser(emptyStrategies);
            ShapefileType type = ShapefileType.SIDO;
            
            // When & Then
            assertThatThrownBy(() -> parser.parse(mockCommand, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 타입: " + type);
        }
        
        @Test
        @DisplayName("단일 전략으로 초기화된 파서가 정상적으로 동작한다")
        void shouldWorkWithSingleStrategy() {
            // Given
            List<ShapefileParsingStrategy> singleStrategy = Collections.singletonList(mockStrategy1);
            ShapefileParser parser = new ShapefileParser(singleStrategy);
            ShapefileType type = ShapefileType.SIGUNGU;
            when(mockStrategy1.supports(type)).thenReturn(true);
            
            // When
            assertDoesNotThrow(() -> parser.parse(mockCommand, type));
            
            // Then
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1).parse(mockCommand);
        }
        
        @Test
        @DisplayName("null 전략 리스트로 초기화할 때 NullPointerException이 발생한다")
        void shouldHandleNullStrategyList() {
            // Given
            List<ShapefileParsingStrategy> nullStrategies = null;
            
            // When & Then
            assertThatThrownBy(() -> new ShapefileParser(nullStrategies))
                .isInstanceOf(NullPointerException.class);
        }
    }
    
    @Nested
    @DisplayName("전략 예외 처리 테스트")
    class StrategyExceptionHandlingTests {
        
        @Test
        @DisplayName("전략의 supports 메서드에서 예외가 발생하면 전파된다")
        void shouldPropagateSupportsException() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            RuntimeException supportException = new RuntimeException("Strategy supports error");
            when(mockStrategy1.supports(type)).thenThrow(supportException);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isEqualTo(supportException);
            
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1, never()).parse(any());
        }
        
        @Test
        @DisplayName("전략의 parse 메서드에서 예외가 발생하면 그대로 전파된다")
        void shouldPropagateParseException() {
            // Given
            ShapefileType type = ShapefileType.SIGUNGU;
            RuntimeException parseException = new RuntimeException("Parse error");
            when(mockStrategy1.supports(type)).thenReturn(true);
            doThrow(parseException).when(mockStrategy1).parse(mockCommand);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isEqualTo(parseException);
            
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1).parse(mockCommand);
        }
        
        @Test
        @DisplayName("첫 번째 전략의 supports에서 예외가 발생해도 두 번째 전략을 확인한다")
        void shouldContinueWhenFirstStrategySupportsThrowsException() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenThrow(new RuntimeException("Strategy1 error"));
            when(mockStrategy2.supports(type)).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Strategy1 error");
            
            verify(mockStrategy1).supports(type);
            verify(mockStrategy2, never()).supports(any());
            verify(mockStrategy1, never()).parse(any());
            verify(mockStrategy2, never()).parse(any());
        }
    }
    
    @Nested
    @DisplayName("모든 ShapefileType 열거형 값에 대한 테스트")
    class AllShapefileTypeTests {
        
        @Test
        @DisplayName("SIDO 타입이 지원되는 전략으로 파싱된다")
        void shouldParseSidoType() {
            testTypeWithSupportingStrategy(ShapefileType.SIDO);
        }
        
        @Test
        @DisplayName("SIGUNGU 타입이 지원되는 전략으로 파싱된다")
        void shouldParseSigunguType() {
            testTypeWithSupportingStrategy(ShapefileType.SIGUNGU);
        }
        
        @Test
        @DisplayName("SIDO 타입이 지원되지 않을 때 예외가 발생한다")
        void shouldThrowExceptionForUnsupportedSidoType() {
            testTypeWithoutSupportingStrategy(ShapefileType.SIDO);
        }
        
        @Test
        @DisplayName("SIGUNGU 타입이 지원되지 않을 때 예외가 발생한다")
        void shouldThrowExceptionForUnsupportedSigunguType() {
            testTypeWithoutSupportingStrategy(ShapefileType.SIGUNGU);
        }
        
        private void testTypeWithSupportingStrategy(ShapefileType type) {
            // Given
            when(mockStrategy1.supports(type)).thenReturn(true);
            
            // When
            assertDoesNotThrow(() -> shapefileParser.parse(mockCommand, type));
            
            // Then
            verify(mockStrategy1).supports(type);
            verify(mockStrategy1).parse(mockCommand);
        }
        
        private void testTypeWithoutSupportingStrategy(ShapefileType type) {
            // Given
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 타입: " + type);
        }
    }
    
    @Nested
    @DisplayName("전략 호출 순서 및 상호작용 테스트")
    class StrategyInteractionTests {
        
        @Test
        @DisplayName("전략들이 순서대로 확인되고 첫 번째 지원 전략만 실행된다")
        void shouldCheckStrategiesInOrderAndExecuteFirst() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(true);
            
            // When
            shapefileParser.parse(mockCommand, type);
            
            // Then
            var inOrder = inOrder(mockStrategy1, mockStrategy2);
            inOrder.verify(mockStrategy1).supports(type);
            inOrder.verify(mockStrategy2).supports(type);
            inOrder.verify(mockStrategy2).parse(mockCommand);
            
            verify(mockStrategy1, never()).parse(any());
        }
        
        @Test
        @DisplayName("동일한 타입으로 여러 번 파싱해도 매번 전략 확인이 수행된다")
        void shouldCheckStrategiesOnEachParse() {
            // Given
            ShapefileType type = ShapefileType.SIGUNGU;
            when(mockStrategy1.supports(type)).thenReturn(true);
            
            // When
            shapefileParser.parse(mockCommand, type);
            shapefileParser.parse(mockCommand, type);
            
            // Then
            verify(mockStrategy1, times(2)).supports(type);
            verify(mockStrategy1, times(2)).parse(mockCommand);
        }
        
        @Test
        @DisplayName("서로 다른 커맨드 객체로 파싱할 때 적절한 커맨드가 전달된다")
        void shouldPassCorrectCommandToStrategy() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            ShapefileUploadCommand command1 = mock(ShapefileUploadCommand.class);
            ShapefileUploadCommand command2 = mock(ShapefileUploadCommand.class);
            when(mockStrategy1.supports(type)).thenReturn(true);
            
            // When
            shapefileParser.parse(command1, type);
            shapefileParser.parse(command2, type);
            
            // Then
            verify(mockStrategy1).parse(command1);
            verify(mockStrategy1).parse(command2);
        }
        
        @Test
        @DisplayName("동일한 커맨드로 다른 타입을 파싱할 때 각각 적절한 전략이 선택된다")
        void shouldSelectAppropriateStrategyForDifferentTypes() {
            // Given
            when(mockStrategy1.supports(ShapefileType.SIDO)).thenReturn(true);
            when(mockStrategy1.supports(ShapefileType.SIGUNGU)).thenReturn(false);
            when(mockStrategy2.supports(ShapefileType.SIDO)).thenReturn(false);
            when(mockStrategy2.supports(ShapefileType.SIGUNGU)).thenReturn(true);
            
            // When
            shapefileParser.parse(mockCommand, ShapefileType.SIDO);
            shapefileParser.parse(mockCommand, ShapefileType.SIGUNGU);
            
            // Then
            verify(mockStrategy1).parse(mockCommand);
            verify(mockStrategy2).parse(mockCommand);
        }
    }
    
    @Nested
    @DisplayName("경계 조건 및 Edge Case 테스트")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("전략 리스트에 null 전략이 포함되어 있을 때 NullPointerException이 발생한다")
        void shouldHandleNullStrategyInList() {
            // Given
            List<ShapefileParsingStrategy> strategiesWithNull = Arrays.asList(mockStrategy1, null);
            ShapefileParser parser = new ShapefileParser(strategiesWithNull);
            ShapefileType type = ShapefileType.SIDO;
            when(mockStrategy1.supports(type)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> parser.parse(mockCommand, type))
                .isInstanceOf(NullPointerException.class);
        }
        
        @Test
        @DisplayName("매우 많은 전략이 있을 때도 올바르게 동작한다")
        void shouldHandleManyStrategies() {
            // Given
            ShapefileParsingStrategy strategy3 = mock(ShapefileParsingStrategy.class);
            ShapefileParsingStrategy strategy4 = mock(ShapefileParsingStrategy.class);
            ShapefileParsingStrategy strategy5 = mock(ShapefileParsingStrategy.class);
            
            List<ShapefileParsingStrategy> manyStrategies = Arrays.asList(
                mockStrategy1, mockStrategy2, strategy3, strategy4, strategy5
            );
            ShapefileParser parser = new ShapefileParser(manyStrategies);
            ShapefileType type = ShapefileType.SIGUNGU;
            
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(false);
            when(strategy3.supports(type)).thenReturn(false);
            when(strategy4.supports(type)).thenReturn(true);
            when(strategy5.supports(type)).thenReturn(true);
            
            // When
            parser.parse(mockCommand, type);
            
            // Then
            verify(strategy4).parse(mockCommand);
            verify(strategy5, never()).parse(any());
        }
        
        @Test
        @DisplayName("전략이 순서대로 체크되는지 확인한다")
        void shouldCheckStrategiesInSequentialOrder() {
            // Given
            ShapefileType type = ShapefileType.SIDO;
            
            // 모든 전략이 지원하지 않는 경우
            when(mockStrategy1.supports(type)).thenReturn(false);
            when(mockStrategy2.supports(type)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> shapefileParser.parse(mockCommand, type))
                .isInstanceOf(IllegalArgumentException.class);
            
            // 순서대로 호출되었는지 확인
            var inOrder = inOrder(mockStrategy1, mockStrategy2);
            inOrder.verify(mockStrategy1).supports(type);
            inOrder.verify(mockStrategy2).supports(type);
        }
    }
}