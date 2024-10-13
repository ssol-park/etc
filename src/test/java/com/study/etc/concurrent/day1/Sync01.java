package com.study.etc.concurrent.day1;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Sync01 {
    Logger logger = LoggerFactory.getLogger(Sync01.class);

    @Test
    void testThread() throws InterruptedException {
        Thread subThread = new Thread(() -> logger.info("[subThread] ######### subThread 실행"));

        subThread.start();

        /*
        * 실행중에 우선순위가 동일한 다른 스레드에게 실행을 양보하고 실행 대기 상태가 됨
        * yield 를 호출하지 않으면 subThread 를 생성하는 오버헤드로 인해 항상 main thread 의 log 가 먼저 출력 됨
        * */
        Thread.yield();

        logger.info("[mainThread] ######### mainThread 실행");

        subThread.join();
    }

    public class Counter {
        private volatile int count = 0;

        public void increament() {
            this.count++;
        }

        public synchronized void increamentSync() {
            this.count++;
        }

        public int getCount() {
            return this.count;
        }
    }

    @Test
    void testCounter() throws InterruptedException {

        final Counter counter = new Counter();

        class CountingThread extends Thread {

            private Counter counter;

            public CountingThread(Counter counter) {
                this.counter = counter;
            }

            public void run() {
                for (int i = 0; i < 10000; ++i) {
//                    counter.increament();
                    counter.increamentSync();
                }
            }
        }

        CountingThread ct1 = new CountingThread(counter);
        CountingThread ct2 = new CountingThread(counter);

        ct1.start(); ct2.start();
        ct1.join(); ct2.join();


        /*
        * 1. counter.increament()
        * : 두 개의 쓰레드가 Counter 내부의 count 값을 읽을 때 발생하는 경쟁 조건 때문에 매번 실행 할 때 마다 값이 달라짐
        * ==> // [threadTest02] getCount :: 16153
        *
        * 2. counter.increamentSync();
        * : synchronized 키워드를 적용하면 Counter 객체가 가지고 있는 lock 을 요구하고, 메서드가 리턴될 때 lock 을 반환하므로,
        * 동시에 여러 스레드가 접근 시 lock 을 획득하지 못한 스레드는 블로킹 됨
        * ==> [threadTest02] getCount :: 20000
        *
        * getCount()를 실행할 때 count 필드가 캐싱된 값을 읽어올 수 있으므로, 메모리 가시성을 보장하기 위해 동기화 필요
        * */

        logger.info("[threadTest02] getCount :: {}", counter.getCount());


    }
}
