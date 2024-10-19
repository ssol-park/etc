package com.study.etc.function.day1;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionTest {
    Logger logger = LoggerFactory.getLogger(FunctionTest.class);

    // 고차 함수, 함수를 인자로 받아서 새로운 함수를 반환
    Function<Integer, Integer> applyFunction(Function<Integer, Integer> func) {
        return value -> func.apply(value) + 10;
    }

    @Test
    void 고차_함수_테스트() {

        // 입력 값에 2를 곱하는 함수
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;

        // multiplyByTwo 에 10을 더하는 함수
        Function<Integer, Integer> addTenAndMultiplyByTwo = applyFunction(multiplyByTwo);

        assertThat(addTenAndMultiplyByTwo.apply(5)).isEqualTo(20);
        assertThat(addTenAndMultiplyByTwo.apply(10)).isEqualTo(30);
    }

    @Test
    void 커링_테스트() {
        // 두 개의 인자를 받는 커링 함수
        Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;

        // 첫 번째 인자를 5로 설정
        Function<Integer, Integer> addFive = curriedAdd.apply(5);

        assertThat(addFive.apply(10)).isEqualTo(15);
        assertThat(addFive.apply(20)).isEqualTo(25);
    }

    List<Integer> getFirstFiveEvenNumbers() {
        List<Integer> evenNumbers = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            logger.info("[Method] {} 번 째 실행중..", i);
            if (i % 2 == 0) evenNumbers.add(i);
        }

        return evenNumbers;
    }

    List<Integer> getLazyFirstFiveEvenNumbers() {
        return Stream.iterate(1, n -> n + 1)
                .filter(n -> {
                    logger.info("[Lazy] {} 번 째 실행중..", n);
                    return n % 2 == 0;
                })
                .limit(5)
                .toList();
    }

    @Test
    void 지연평가_테스트() {
        // 100 번 loop 실행
        getFirstFiveEvenNumbers();

        // 10 번 loop 실행
        getLazyFirstFiveEvenNumbers();
    }
}
