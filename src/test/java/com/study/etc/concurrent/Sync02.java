package com.study.etc.concurrent;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Semaphore;

class Sync02 {

    Logger logger = LoggerFactory.getLogger(Sync02.class);

    class Chopstick {
        private int id;

        public Chopstick(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    class Philosopher extends Thread {
        private Chopstick left;
        private Chopstick right;
        private Random random;
        private int thinkCnt;
        private Semaphore waiter;

        public Philosopher(Chopstick left, Chopstick right) {
            this.left = left;
            this.right = right;
            this.random = new Random();
        }

        public Philosopher(Chopstick left, Chopstick right, Semaphore waiter) {
            this.left = left;
            this.right = right;
            this.random = new Random();
            this.waiter = waiter;
        }

        public void run() {
            try {

                while (true) {
                    ++thinkCnt;

                    if (thinkCnt % 10 == 0) {
                        logger.info("철학자 {} {} 번째 생각중...", this, thinkCnt);
                    }

                    Thread.sleep(random.nextInt(1000));

                    logger.info("[Waiter] 자원 획득 전: {}", waiter.availablePermits());
                    waiter.acquire(); // 세마포어를 획득하여 자원 사용 허가
                    logger.info("[Waiter] 자원 획득 후: {}", waiter.availablePermits());

                    synchronized (left) {
                        synchronized (right) {
                            Thread.sleep(random.nextInt(1000));
                        }
                    }

                    waiter.release(); // 세마포어 반납
                    logger.info("[Waiter] 자원 반환 후: {}", waiter.availablePermits());
                }

            } catch (InterruptedException e) {
                logger.error("InterruptedException: {}", e.getMessage());
            }
        }
    }

    @Test
    void testDiningPhilosophers() throws InterruptedException {
        Philosopher[] philosophers = new Philosopher[5];
        Chopstick[] chopsticks = new Chopstick[5];

        for (int i = 0; i < 5; i++) {
            chopsticks[i] = new Chopstick(i);
        }

        // 교착상태 및 기아상태를 방지하기 위해 자원에 접근할 수 있는 스레드 수를 제한하고 관리함
        Semaphore waiter = new Semaphore(4);

        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(chopsticks[i], chopsticks[(i + 1) % 5], waiter);
            philosophers[i].start();
        }

        for (int i = 0; i < 5; i++) {
            philosophers[i].join();
        }
    }
}
