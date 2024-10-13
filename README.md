## 7가지 동시성 모델
### Day1
- Java Memory Model: https://www.cs.umd.edu/~pugh/java/memoryModel/
- JSR 133 FAQ: https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html
  - 초기화 안전성(Initialization Safety) - 자바 메모리 모델(JMM)이 보장하는 것
    - Initialization Safety: 자바 메모리 모델은 객체의 생성과 초기화가 안전하게 이루어지도록 보장한다.
      객체가 완전히 초기화되기 전에 다른 스레드가 해당 객체에 접근하는 것을 방지한다. 생성자가 완료되기 전에 다른 스레드가 해당 객체를 사용하지 않도록 보장 함
  - 불변 객체 또는 final 필드만을 사용하는 객체는 lock 없이도 안전하게 스레드 간 공유 가능
  - 변경 가능한 객체는 동기화 또는 락을 사용해야만 스레드 간 안전하게 공유될 수 있다.
  - 재정렬(Reordering): 컴파일러, CPU는 최적의 성능을 위해 명령어의 실행 순서를 재배열할 수 있다. JMM은 스레드 간의 일관성을 유지하기 위해 모든 명령의 순서를 보장하지 않는다.
  - 메모리 가시성 문제: 스레드는 자신만의 캐시 또는 레지스터에서 값을 읽고 쓸 수 있으며, 변경된 값이 즉시 다른 스레드에 보이지 않을 수 있다.