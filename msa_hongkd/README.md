# MSA 기반 주문관리 시스템 (1차 - 서비스 분리)

동일한 기능의 주문관리 시스템을 **모놀리식**과 **MSA** 두 가지 구조로 각각 구현한 프로젝트입니다.

## 개발 환경

| 항목 | 내용 |
|---|---|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.1 |
| 프론트엔드 | React 18 |
| 데이터베이스 | MariaDB (3307) |
| 토큰 저장소 | Redis (6379) |
| 메시지 브로커 | Apache Kafka (9092) |
| 인증 | JWT |
| 빌드 | Gradle |

---

## 프로젝트 구성

### 모놀리식
```
msa_mono_hongkd          백엔드 (단일 애플리케이션, :8081)
msa_mono_hongkd_front    프론트엔드 (:3000)
```

### MSA
```
eureka              서비스 레지스트리          :8761
configserver        중앙 설정 관리             :8888
apigateway          단일 진입점, JWT 검증       :8081
member              회원 · 인증                랜덤 포트
product             물품 · 재고                랜덤 포트
ordering            주문 · 취소 · 환불          랜덤 포트
msa_hongkd_config   설정 저장소 (yml 파일)
msa_hongkd_front    프론트엔드                 :3000
```

---

## 실행 방법

### 1. 사전 준비

**데이터베이스 생성**
```sql
CREATE DATABASE hongkd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Redis 실행**
```bash
docker run --name redis -p 6379:6379 -d redis
```

**Kafka 실행** (ordering 폴더에서)
```bash
cd ordering
docker compose up -d
```

**Config Server 설정 저장소**

`msa_hongkd_config` 폴더의 yml 파일들을 본인의 GitHub 저장소에 올린 뒤,
`configserver/src/main/resources/application.yaml`의 `uri`를 해당 주소로 변경합니다.

### 2. 백엔드 실행 (순서 중요)

```
eureka → configserver → member → product → ordering → apigateway
```

Eureka와 Config Server가 먼저 떠 있어야 나머지 서비스가 설정을 받아올 수 있습니다.

### 3. 프론트엔드 실행

```bash
cd msa_hongkd_front
npm install
npm start
```

---

## 기본 계정

애플리케이션 최초 실행 시 관리자 계정이 자동 생성됩니다.

| 구분 | 이메일 | 비밀번호 |
|---|---|---|
| 관리자 | admin@naver.com | 12341234 |

일반 회원은 회원가입 화면에서 직접 생성합니다.

---

## 주요 기능

### 사용자
- 회원가입 / 로그인 (JWT 발급, Refresh Token으로 자동 재발급)
- 물품 목록 조회 및 상품명 검색
- 물품 상세 조회 후 수량 지정 주문
- 주문 내역 조회, 주문 취소, 환불 신청

### 관리자
- 사용자의 모든 기능
- 물품 등록 및 수정 (ADMIN 권한 전용)

---

## MSA 구조의 핵심

### 서비스 간 통신

| 방식 | 사용처 | 이유 |
|---|---|---|
| OpenFeign (동기) | 주문 생성 시 재고 확인 | 결과가 있어야 다음 단계를 판단할 수 있음 |
| Kafka (비동기) | 재고 차감 / 복원 | 조금 늦게 처리돼도 무방, 주문 응답이 빨라짐 |

**Kafka 토픽**
- `update-stock-topic` : 주문 생성 시 재고 차감
- `restore-stock-topic` : 취소 / 환불 시 재고 복원

### 인증 처리

모놀리식에서는 각 애플리케이션이 직접 JWT를 검증했지만,
MSA에서는 **API Gateway가 한 번만 검증**하고 결과를 헤더로 전달합니다.

```
요청 → Gateway (JWT 검증) → X-User-Id, X-User-Role 헤더 주입 → 각 서비스
```

각 서비스의 Controller는 `@RequestHeader("X-User-Id")`로 회원 ID를 받습니다.

### 데이터 참조

서비스마다 DB가 분리되어 있어 다른 서비스의 엔티티를 직접 참조할 수 없습니다.

| | 모놀리식 | MSA |
|---|---|---|
| 참조 방식 | `@ManyToOne Product product` | `Long productId` |
| 조회 | JPA 연관관계로 즉시 접근 | 필요 시 OpenFeign으로 API 호출 |
| 외래키 | 물리적 FK 제약 | 없음 (논리적 참조) |

---

## API 경로

MSA에서는 Gateway가 서비스별 프리픽스로 라우팅합니다.

| 기능 | 경로 |
|---|---|
| 회원가입 | `POST /member-service/member/create` |
| 로그인 | `POST /member-service/member/doLogin` |
| 토큰 재발급 | `POST /member-service/member/refresh-token` |
| 물품 목록 | `GET /product-service/product/list?keyword=` |
| 물품 상세 | `GET /product-service/product/{id}` |
| 물품 등록 | `POST /product-service/product/create` |
| 물품 수정 | `PUT /product-service/product/{id}` |
| 주문 생성 | `POST /ordering-service/ordering/create` |
| 주문 내역 | `GET /ordering-service/ordering/my` |
| 주문 취소 | `PUT /ordering-service/ordering/{id}/cancel` |
| 환불 신청 | `PUT /ordering-service/ordering/{id}/refund` |

---

## 참고 사항

- `ddl-auto: update` 설정이라 최초 실행 시 테이블이 자동 생성됩니다.
- 각 서비스는 `server.port: 0`으로 랜덤 포트에서 실행되며, Eureka를 통해 서로를 찾습니다.
- 재고 차감이 Kafka 비동기 처리라, 주문 직후 재고 반영까지 약간의 시차가 있습니다.
