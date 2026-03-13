# 📚 Spring Data JPA 연관관계 학습 프로젝트

Spring Boot와 Spring Data JPA를 사용해서  
**Entity 연관관계 매핑과 조회 전략**을 학습한 예제 프로젝트입니다.

`User`, `Food`, `Order` 엔티티를 중심으로 다음 내용을 실습했습니다.

- `@OneToOne`
- `@ManyToOne`
- `@OneToMany`
- `@ManyToMany`
- 중간 테이블을 엔티티로 분리한 `Order` 설계
- `FetchType.LAZY` / `FetchType.EAGER`
- `CascadeType.PERSIST`
- `orphanRemoval = true`

학습 자료 기준으로는 고객(`users`)과 음식(`food`) 테이블의 관계를 출발점으로 연관관계 설계, 로딩 전략, 영속성 전이, 고아 객체 삭제까지 다룹니다. :contentReference[oaicite:8]{index=8} :contentReference[oaicite:9]{index=9} :contentReference[oaicite:10]{index=10} :contentReference[oaicite:11]{index=11}

---

## 🛠 Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Web MVC
- MySQL
- Lombok
- Gradle

프로젝트 설정 가이드는 Java 17, Gradle - Groovy, MySQL 기반으로 진행됩니다. :contentReference[oaicite:12]{index=12}

---

## 📂 프로젝트 구조

```text
jpa-advance/
├── src/main/java/org/example/jpaadvance/
│   ├── JpaAdvanceApplication.java
│   ├── entity/
│   │   ├── User.java                  # 고객 엔티티
│   │   ├── Food.java                  # 음식 엔티티
│   │   └── Order.java                 # 주문 중간 엔티티
│   └── repository/
│       ├── UserRepository.java
│       ├── FoodRepository.java
│       └── OrderRepository.java
│
├── src/test/java/org/example/jpaadvance/
│   ├── relation/
│   │   ├── OneToOneTest.java          # 1:1 관계 테스트
│   │   ├── ManyToOneTest.java         # N:1 관계 테스트
│   │   ├── OneToManyTest.java         # 1:N 관계 테스트
│   │   ├── ManyToManyTest.java        # N:M 관계 테스트
│   │   └── OrderTest.java             # 중간 엔티티 Order 테스트
│   ├── fetch/
│   │   └── FetchTypeTest.java         # 지연 로딩 / 즉시 로딩 테스트
│   ├── cascade/
│   │   └── CascadeTest.java           # 영속성 전이 테스트
│   └── orphan/
│       └── OrphanTest.java            # 고아 엔티티 삭제 테스트
│
└── src/main/resources/
    ├── application.properties
    └── application-local.properties
```

---

##  학습 내용

### 1️⃣ Entity 연관관계 설계

고객(`users`)과 음식(`food`) 테이블을 기준으로  
연관관계를 어느 테이블에 둘지 고민하는 과정부터 시작합니다.  
단순히 `users.food_id` 또는 `food.user_id` 방식으로만 설계하면  
중복 데이터나 확장성 문제가 생길 수 있어, 관계의 성격에 따라 적절한 주인과 외래 키를 정하는 것이 핵심입니다.

### 2️⃣ 1 대 1 관계 (`@OneToOne`)

1:1 관계에서는 외래 키의 주인을 직접 정해야 하며,  
주인 쪽에서 `@JoinColumn`을 사용합니다.  
양방향 관계에서는 반대편 엔티티에 `mappedBy`를 설정해  
주인과 비주인을 명확히 구분합니다.

### 3️⃣ N 대 1 관계 (`@ManyToOne`)

여러 개의 음식이 한 명의 고객에 속하는 구조를 실습합니다.  
`Food` 엔티티가 N 쪽이므로 외래 키의 주인이 되며,  
`@ManyToOne` + `@JoinColumn(name = "user_id")` 형태로 매핑합니다.  
양방향으로 확장할 때는 `User` 엔티티에 `@OneToMany(mappedBy = "user")`를 둡니다.

### 4️⃣ 1 대 N 관계 (`@OneToMany`)

1:N 단방향 관계도 가능하지만, 실제 외래 키는 N 쪽 테이블이 가지기 때문에  
추가적인 `UPDATE`가 발생할 수 있다는 점을 학습합니다.  
그래서 실무에서는 보통 N:1 양방향 설계를 더 많이 사용합니다.

### 5️⃣ N 대 M 관계 (`@ManyToMany`)와 중간 테이블

N:M 관계는 직접 매핑할 수 있지만,  
중간 테이블을 JPA가 자동 생성하는 방식은 제어가 어렵습니다.  
그래서 실제 프로젝트에서는 `orders` 같은 중간 엔티티를 별도로 두는 방식이 더 유연합니다.  
이 프로젝트에도 `Order` 엔티티가 포함되어 있어,  
주문 정보를 명시적으로 관리하는 구조를 학습할 수 있습니다.

### 6️⃣ 지연 로딩과 즉시 로딩

JPA는 연관된 엔티티를 조회할 때  
즉시 가져올지(`EAGER`), 필요한 시점에 가져올지(`LAZY`)를 선택할 수 있습니다.  
기본적으로 `@ManyToOne`은 즉시 로딩, `@OneToMany`는 지연 로딩이 기본값입니다.  
프로젝트의 `FetchTypeTest`에서는 음식 조회 시 사용자 정보가 함께 조회되는 경우와,  
사용자 조회 후 연관된 음식 목록을 접근하는 시점에 추가 조회가 발생하는 경우를 확인할 수 있습니다.

### 7️⃣ 영속성 전이 (`CascadeType.PERSIST`)

부모 엔티티 저장 시 연관된 자식 엔티티도 함께 저장되도록  
`cascade = CascadeType.PERSIST`를 적용하는 내용을 실습합니다.  
이 프로젝트에서는 `User`가 `Food` 목록을 가질 때,  
사용자만 저장해도 음식 엔티티가 함께 저장되는 흐름을 테스트합니다.

### 8️⃣ 고아 엔티티 삭제 (`orphanRemoval = true`)

연관관계 컬렉션에서 엔티티를 제거했을 때  
DB에서도 자동 삭제되도록 `orphanRemoval = true`를 사용하는 예제입니다.  
즉, 부모와의 관계가 끊어진 자식 엔티티를 고아 객체로 보고 삭제합니다.  
프로젝트의 `OrphanTest`에서 컬렉션에서 특정 음식 엔티티를 제거하는 방식으로 확인할 수 있습니다.

---

## 🧱 도메인 모델

### `User`

- 고객 정보 저장
- `name`
- `foodList` 연관관계 관리
- 연관관계 편의 메서드 `addFoodList(Food food)` 제공

### `Food`

- 음식 정보 저장
- `name`
- `price`
- `user` 참조를 통해 주문한 고객과 연결

### `Order`

- 주문 중간 엔티티
- `user`
- `food`
- `orderDate`

---

##  테스트 클래스 요약

| 테스트 클래스 | 내용 |
|---|---|
| `OneToOneTest` | 1:1 단방향 / 양방향 관계 학습 |
| `ManyToOneTest` | N:1 단방향 / 양방향 관계 학습 |
| `OneToManyTest` | 1:N 관계와 추가 UPDATE 확인 |
| `ManyToManyTest` | N:M 관계 및 중간 테이블 개념 학습 |
| `OrderTest` | 중간 엔티티 `Order`를 사용한 주문 구조 실습 |
| `FetchTypeTest` | `LAZY` / `EAGER` 로딩 차이 확인 |
| `CascadeTest` | `CascadeType.PERSIST`, 삭제 전이 학습 |
| `OrphanTest` | `orphanRemoval = true` 동작 확인 |

---

##  핵심 정리

| 개념 | 설명 |
|---|---|
| `@OneToOne` | 1:1 관계 매핑 |
| `@ManyToOne` | N:1 관계 매핑, 보통 외래 키 주인 |
| `@OneToMany` | 1:N 관계 매핑, 컬렉션으로 관리 |
| `@ManyToMany` | N:M 관계 매핑, 실무에서는 중간 엔티티 분리 권장 |
| `mappedBy` | 양방향 관계에서 연관관계 주인이 아님을 표시 |
| `@JoinColumn` | 외래 키 컬럼 지정 |
| `FetchType.LAZY` | 실제 필요한 시점에 조회 |
| `FetchType.EAGER` | 엔티티 조회 시 즉시 함께 조회 |
| `CascadeType.PERSIST` | 부모 저장 시 자식도 함께 저장 |
| `orphanRemoval = true` | 부모와 관계가 끊긴 자식 엔티티 자동 삭제 |
