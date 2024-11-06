package com.study.etc.concurrent.day1;

import org.junit.jupiter.api.Test;

class ㅈReorderingTest {

    // AS-IS
//    private static int x = 0;
//    private static int y = 0;
//    private static int a = 0;
//    private static int b = 0;

    // TO-BE :: 혹은 AtomicInteger 사용
    private volatile static int x = 0;
    private volatile static int y = 0;
    private volatile static int a = 0;
    private volatile static int b = 0;
    private static final Object lock = new Object();

    /*
     * 실행 결과가 달라지는 이유
     * 1. 재정렬(Reordering): 컴파일러, CPU는 최적의 성능을 위해 명령어의 실행 순서를 재배열할 수 있다. JMM은 스레드 간의 일관성을 유지하기 위해 모든 명령의 순서를 보장하지 않는다.
     * 2. 메모리 가시성 문제: 스레드는 자신만의 캐시 또는 레지스터에서 값을 읽고 쓸 수 있으며, 변경된 값이 즉시 다른 스레드에 보이지 않을 수 있다.
     *
     * 해당 코드에는 동기화가 없기 때문에, 재정렬과 메모리 가시성으로 인해 예상치 못한 결과가 나타날 수 있다.
     * 이를 해결하기 위해 volatile, synchronized 등을 활용
     * */
    @Test
    void testReorderingAsIs() throws InterruptedException {

        // x와 a 값을 변경
        Thread t1 = new Thread(() -> {
           a = 1;
           x = b;
        });

        Thread t2 = new Thread(() -> {
           b = 1;
           y = a;
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 예상 값: x = 0, y = 1
        System.out.println("x = " + x + " y = " + y);

        // 실행1: x = 1, y = 0
        // 실행2: x = 0, y = 1
        // 실행3: x = 1, y = 1
    }

    @Test
    void testReorderingToBe() throws InterruptedException {

        // x와 a 값을 변경
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                a = 1;
                x = b;
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                b = 1;
                y = a;
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 예상 값: x = 0, y = 1
        System.out.println("x = " + x + " y = " + y);

        // 실행1: x = 0, y = 1
        // 실행2: x = 0, y = 1
        // 실행3: x = 0, y = 1
    }
}
