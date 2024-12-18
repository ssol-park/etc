# **Spring @Transactional 동작 흐름**

---

## **1. `@Transactional` 감지**
**: Spring 컨테이너는 `@Transactional` 애너테이션을 감지하고 처리한다.**

### **감지 시점**
- 애플리케이션 시작 시 빈(bean) 생성 및 초기화 단계

### **관련 설정**
- 트랜잭션 관리 활성화 `@EnableTransactionManagement` 또는 `\<tx:annotation-driven>`


### **핵심 클래스 및 메서드**
- `org.springframework.context.annotation`
    - **클래스**: `TransactionManagementConfigurationSelector`
        - **메서드**: `selectImports()`
- `org.springframework.transaction.annotation`
    - **클래스**: `AnnotationTransactionAttributeSource`
        - **메서드**: `getTransactionAttribute()`

### **처리 흐름**
1. `@EnableTransactionManagement` → `TransactionManagementConfigurationSelector`
    - 트랜잭션 AOP 관련 빈과 설정 자동 추가
2. `BeanPostProcessor`가 빈을 생성하면서 `@Transactional`이 붙은 메서드를 감지
3. `AnnotationTransactionAttributeSource`가 메타데이터 수집
4. 트랜잭션 설정(전파 수준, 롤백 규칙 등)이 `TransactionAttribute`에 저장됨
5. **`TransactionInterceptor`** 준비

---

## **2. 프록시 객체 생성**

- Spring AOP가 트랜잭션 관리가 적용된 프록시 객체를 생성

### **프록시 생성 원리**
- **JDK 동적 프록시**: 인터페이스 기반
- **CGLIB 프록시**: 클래스 기반

### **관련 설정**
- `@EnableTransactionManagement` : AOP 및 트랜잭션 설정 활성화

- `proxyTargetClass = true` : 클래스 기반 CGLIB 프록시 강제 설정

### **핵심 클래스 및 메서드**
- `org.springframework.aop.framework`
    - **클래스**: `ProxyFactory`
        - **메서드**: `getProxy()`
    - **클래스**: `JdkDynamicAopProxy`
        - **메서드**: `invoke()`
    - **클래스**: `CglibAopProxy`
        - **메서드**: `intercept()`

### **프록시 생성 및 인터셉터 등록 흐름**
1. `BeanPostProcessor`가 프록시 적용 가능 여부 검사
2. `ProxyFactory`를 사용해 프록시 객체 생성
3. `TransactionInterceptor` 를 프록시에 연결

---

## **3. 메서드 호출 시 프록시가 가로챔**

클라이언트가 메서드를 호출하면 프록시 객체가 호출을 가로채 트랜잭션을 처리

### **핵심 클래스 및 메서드**
- `org.springframework.transaction.interceptor`
    - **클래스**: `TransactionInterceptor`
        - **메서드**: `invoke()`
- `org.springframework.aop.framework`
    - **클래스**: `ReflectiveMethodInvocation`
        - **메서드**: `proceed()`

---

## **4. 트랜잭션 시작**

프록시는 `PlatformTransactionManager`를 호출하여 트랜잭션을 시작

### **핵심 클래스 및 메서드**
- `org.springframework.transaction`
    - **클래스**: `PlatformTransactionManager`
        - **메서드**: `getTransaction()`
- `org.springframework.transaction.support`
    - **클래스**: `AbstractPlatformTransactionManager`
        - **메서드**: `startTransaction()`

---

## **5. 실제 메서드 실행**

프록시 객체는 원본 객체의 **실제 메서드**를 호출

- **원본 객체 클래스**: 예시: `MyService`
    - **메서드**: `targetMethod()`

---

## **6. 트랜잭션 커밋 또는 롤백**

메서드 실행 결과에 따라 트랜잭션을 **커밋**하거나 **롤백**

### **핵심 클래스 및 메서드**
- `org.springframework.transaction`
    - **클래스**: `PlatformTransactionManager`
        - **메서드**: `commit()` 또는 `rollback()`
- `org.springframework.transaction.support`
    - **클래스**: `AbstractPlatformTransactionManager`
        - **메서드**: `processCommit()` 또는 `rollbackOnException()`

---

## **7. 결과 반환**

트랜잭션이 완료된 후, 프록시 객체는 결과를 클라이언트에 반환

- **관련 패키지**:
    - `org.springframework.aop.framework`
        - **클래스**: `ReflectiveMethodInvocation`
            - **메서드**: `proceed()`

---

## **Self-Invocation 문제**

같은 클래스 내부에서 다른 `@Transactional` 메서드를 호출하면 프록시를 거치지 않기 때문에 트랜잭션이 적용되지 않는다.
- 이를 해결하려면 **AspectJ 모드**를 사용해야 한다.
- https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html#transaction-declarative-annotations-method-visibility

## **@PostConstruct와 초기화 문제**
### **프록시의 초기화**
- 프록시 객체는 **빈(bean) 초기화 과정**에서 생성 되고, **완전히 초기화된 후에만** 예상된 동작(예: 트랜잭션)이 실행됨
- **@PostConstruct**는 빈이 생성되고 의존성 주입이 완료된 직후에 실행되는 메서드 이므로, 프록시 객체가 완전히 초기화되기 전에 **@PostConstruct** 메서드가 실행될 수 있다.

**: 이 경우 프록시 객체가 아직 완전히 초기화되지 않았으므로, 프록시가 아닌 원본 객체의 메서드가 호출되고, 이로 인해 트랜잭션 관리가 적용되지 않는다.**

---

## **전체 흐름 요약**

```plaintext
1. @Transactional 애너테이션 감지  
2. 프록시 객체 생성 (JDK 동적 프록시 또는 CGLIB 프록시)  
3. 메서드 호출 시 프록시가 가로챔  
4. 트랜잭션 시작 (PlatformTransactionManager)  
5. 실제 메서드 실행  
6. 트랜잭션 커밋 또는 롤백  
7. 결과 반환  
```
---
1. Spring docs: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html
2. Spring Refer: https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html#transaction-declarative-annotations-method-visibility