# franc-standard-api
스프링부트 기반 API에 대한 나만의 샘플 어플리케이션.  
API를 구현하는데 있어 필요한 설정과 기본적인 기능들을 구현.  
앞으로 API를 새로 만들어야 할 때 참고 할 목적으로 만들었다.

## 🖥️ 프로젝트 소개
계좌를 등록하고 등록한 계좌로 입.출금/이체를 지원하는 서비스.  
기존 어떤 서비스의 회원 정보를 공유한다는 설정이기 때문에, `회원가입` 등의 처리는 없음  
(서버실행 시 임의의 회원 데이터 INSERT)

### ⚙️ 개발 환경

- Java 11 / JDK 11
- Spring Boot 2.7.9
- Gradle 7.5.1
- H2-db
- Mybatis 2.2.2
- JUnit 5.5.2
- lombok

## 📌 서비스 설명

테이블은 `schema.sql` 참고


### 회원(MEMBER), 은행(BANK)

- 해당 서비스의 기준정보들로, 서비실행 시 INSERT 스크립트 실행 (`data.sql` 참고)

### 계좌(ACCOUNT)

- 회원은 시중은행의 계좌들을 등록할 수 있으며, 등록된 계좌를 해지할 수 있다.
  - API = `계좌등록`, `계좌해지`, `내 계좌목록조회`, `계좌상세조회`

### 거래(TRANS)

- 회원은 등록된 계좌를 통해 '입.출금' 또는 타회원계좌로 '이체'를 할 수 있다.
  - API = `입.출금/이체`, `거래내역조회`, `거래상세조회`
