# 사전과제1. 지자체 협약 지원 API
국내 각 지자체의 중소기업은행에서 각 기업에 지원하는 금융 정보 제공을 위한 API 서비스 입니다.

## 개발 환경
- Scala 2.12.1
- Spring Boot 2.1.7
- H2 with JPA

## 문제해결 방법

### 기본 기능의 경우

검색 조건에 들어가는 필드는 필요한 데이터를 파싱하여 다른 컬럼에 따로 저장하였습니다.

### 추천 기능의 경우

1. 행정구역 사전을 준비했습니다. (위도, 경도 포함)
2. 형태소 분석기로 토크나이징
3. 토큰화 된 목록에서 지역명 사전과 비교하며 찾았습니다.
4. 나머지 조건(금액, 이차보전율 등)은 정규식으로 찾았습니다.
5. 찾은 요소들을 SQL을 이용해 검색

## Requirements

[sbt](https://www.scala-sbt.org)가 필요합니다.

## 빌드 및 실행 방법
실행

```
$ sbt run
```
테스트
```
$ sbt test
```


## HTTP API
| Method | Path | Description |
|---|---|---|
| POST | /api/signup | 회원가입 (필드: 'username', 'password') <br> e.g. {"username": "newuser", "password": "secret"} |
| POST | /api/signin | 로그인 (필드: 'username', 'password') <br>기본 계정: {"username": "testuser", "password": "1234"} |
| POST | /api/refresh | 토큰 재발급. Authorization 헤더에 토큰이 필요합니다. |
| GET | /api/assistanceinfo | 지원 정보 목록을 조회합니다. |
| POST | /api/assistanceinfo | 지원 정보를 1건 등록합니다. |
| PUT | /api/assistanceinfo/:id | 지원 정보를 1건 수정합니다. |
| GET | /api/assistanceinfo/find | 출력 개수를 'limit' 필드로 받아 지자체명 목록을 조회합니다. (지원금액, 이차보전 평균으로 내림차순 정렬) |
| GET | /api/assistanceinfo/minimumRate | 이차보전이 가장 작은 지원 정보의 추천 기관명을 1건 리턴합니다. |
| POST | /api/assistanceinfo/match | 'region' 필드를 받아서 해당 지자체의 지원 정보를 1건 리턴합니다. |
| POST | /api/assistanceinfo/search | 'input' 필드를 받아서 가장 가깝고 적절한 지원 정보를 1건 추천합니다. |
