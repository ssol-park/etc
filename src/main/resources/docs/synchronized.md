## synchronized
**synchronized**는 자바에서 **동기화(synchronization)** 를 제공하는 기본적인 키워드로, 멀티스레드 환경에서 동시성 문제를 해결하기 위한 도구이다. 동기화는 여러 스레드가 동시에 공유 자원에 접근하거나 수정하는 것을 제어하여, 데이터의 일관성과 무결성을 보장하는 데 사용된다.

#### 기능 제공 및 사용 목적
- 상호 배타성(Mutual Exclusion): synchronized는 한 번에 오직 하나의 스레드만이 특정 코드 블록이나 메서드에 접근할 수 있도록 보장한다. 이를 통해 **경쟁 상태(Race Condition)** 를 방지하고, 데이터의 일관성을 유지할 수 있다.

#### 내부 작동 원리
- 모니터 락(Monitor Lock) 사용: 자바에서 각 객체는 고유한 모니터 락(또는 모니터)을 가지고 있으며, synchronized 블록이나 메서드는 이 모니터 락을 획득한 후에 실행된다. 다른 스레드가 이 객체에 대해 락을 획득하려고 하면, 기존 스레드가 락을 해제할 때까지 대기해야 한다.
  - **모니터 락(Monitor Lock)** : 자바에서 동기화를 관리하는 기본 메커니즘으로, 동기화된 메서드나 블록에 스레드가 진입할 때 객체에 대한 고유한 락을 획득하는 것
 
 
- 락과 대기 상태: 다른 스레드가 동기화된 블록 또는 메서드에 진입하려 할 때, 해당 객체나 클래스에 대한 락이 이미 점유된 경우, 해당 스레드는 대기 상태로 들어가며, 락이 해제될 때까지 블록되지 않고 기다린다.


- 재진입 가능(Reentrant): 동일한 스레드가 이미 락을 획득한 상태라면, 동일한 객체에 대한 다른 synchronized 블록이나 메서드로 다시 들어가는 것이 가능하다. 이는 재귀적으로 호출되는 동기화된 메서드에서 유용하다.


- JVM이 락을 관리함

#### 단점
- 교착 상태(Deadlock): 부적절하게 동기화가 이루어질 경우, 데드락이 발생할 수 있다.
- 세밀한 제어가 어렵다. ex) timeout 설정, 인터럽트 설정 등


