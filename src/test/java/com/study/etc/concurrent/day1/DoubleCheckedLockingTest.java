package com.study.etc.concurrent.day1;

import org.junit.jupiter.api.Test;

public class DoubleCheckedLockingTest {

    /*
    * AS-IS
    * 예상: getInstance 최초 호출 시
    *  - instance == null >>>> new Singleton(); 인스턴스 생성 되고, instance = new Singleton 대입
    *
    * 재정렬 발생 시: instance = new Singleton 대입 => new Singleton(); 생성
    * ==> 인스턴스가 완전히 생성되지 않은 시점에, this.instance 에 값이 대입 됨, 이 시점에서 다른 스레드가 getInstance 를 요청하면 instance 는 null 이 아닌 상황
    *
    * TO-BE :: volatile 키워드 사용, 자바 1.5 부터 해당 키워드가 붙은 변수는 재정렬을 하지 않음
    * */
    class Singleton {
        // AS-IS
//        private Singleton instance;

        // TO-BE
        private volatile Singleton instance;

        public Singleton getInstance() {
            Singleton instanceRef = instance;

            if (instanceRef == null) {
                synchronized (this) {
                    if (instanceRef == null) {
                        instance = new Singleton();
                    }
                }
            }

            return instance;
        }
    }
}
