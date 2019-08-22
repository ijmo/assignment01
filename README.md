# 사전과제1. 지자체 협약 지원 API
국내 각 지자체의 중소기업은행에서 각 기업에 지원하는 금융 정보 제공을 위한 API 서비스 입니다.
<br><br>

## 개발 환경
- Scala 2.12.1
- Spring Boot 2.1.7
- H2 with JPA
<br>

## 문제해결 방법

### JPA Modeling

- AssistanceInfo: 지원 정보. 생성 시 추천 기능에 필요한 지역에 대한 정보(지역명, 코드, 위/경도)를 찾아내 함께 저장합니다.

- Organization: 지자체(기관)
<br>

### 애매한 정보에 대한 처리

#### ***지원한도*** - ***추천 금액 이내***의 경우

추천 금액이 정해있지 않아 MAX(Long)로 저장합니다.

#### ***이차보전*** - ***대출이자 전액***의 경우

이자의 제한이 없다는 뜻으로 받아드려, 100%로 저장합니다.
<br>

### 기본 기능의 경우

검색 조건에 들어가는 필드는 필요한 데이터를 파싱하여 다른 컬럼에 따로 저장하였습니다.

- 지자체명 엔티티(Entity)를 따로 관리
- `지원대상` 기준으로 위치를 검색하여 `지역명`, `코드`, `위/경도`를 저장
- 용도에 대한 정보는 없어서 Regex를 하드코딩 했습니다.
- 지원한도는 숫자로 바꿔서 별개의 컬럼에 저장
- 이차보전은 보전율을 2개로 나눠서 저장. 보전율이 없을 경우엔 둘 다 100%로 취급
- 추천기관은 첫번째 컬럼인 지자체명을 Many to Many로 연결
- 관리점, 취급점은 문자열 그대로 저장
<br>

### 추천 기능의 경우

1. 행정구역 사전을 준비 (위도, 경도는 카카오API를 이용해 관청 기준으로 가져와서 저장해 둠. DistrictRepository)
2. 입력 받은 문자열을 형태소 분석기로 토크나이징
3. 토큰화 된 목록에서 명사를 뽑아 지역명 사전과 비교하며 찾기. 결과가 1개 이상일 경우에는 부모-자식 관계의 지역을 우선순위로 선택. (e.g. 충남 대천은 대천이 충남에 속해있으므로 부모자식 관계임) 부모-자식이 없을 경우에는 가장 큰 단위의 행정구역을 선택.
4. 입력 받은 문자열에서 나머지 조건(금액, 이차보전율 등)은 정규식으로 찾기
5. 찾은 요소들을 SQL을 이용해 검색. JPQL에서는 From절에 Nested Query를 사용하지 못해 Native Query를 사용.
<br>

## Requirements

[sbt](https://www.scala-sbt.org)가 필요합니다.
<br>

## 빌드 및 실행 방법
실행

```
$ sbt run
```
테스트
```
$ sbt test
```
<br>

## Basic API

회원가입(signup)은 요청 바디가 `Json`, 로그인과 토큰재발급은 `form-data` 형태로 보내시면 됩니다. 

| Method | Path | Description |
|---|---|---|
| POST | /api/signup | 회원가입 (필드: 'username', 'password') <br> e.g. {"username": "newuser", "password": "secret"}<br>성공시 - 201 |
| POST | /oauth/token | 로그인<br>Basic Auth(Username: financialClientId / Password: 1111)<br>grant_type=password<br>username=newuser<br>password=secret |
| POST | /oauth/token | 토큰 재발급<br>Basic Auth(Username: financialClientId / Password: 1111)<br>grant_type=refresh_token<br>refresh_token=TOKEN_HERE |
<br>

## Resource API

모든 요청에는 Authorization Header에 'Bearer TOKEN' 형식으로 토큰을 포함해야 합니다.

요청 시 데이터는 `Json`형태로 보내야 합니다.

| Method | Path | Description |
|---|---|---|
| GET | /api/assistanceinfo | 지원 정보 목록을 조회합니다. |
| POST | /api/assistanceinfo | 지원 정보를 1건 등록합니다. |
| PUT | /api/assistanceinfo/:id | 지원 정보를 1건 수정합니다. |
| GET | /api/assistanceinfo/find | 출력 개수를 'limit' 필드로 받아 지자체명 목록을 조회합니다. (지원금액, 이차보전 평균으로 내림차순 정렬) |
| GET | /api/assistanceinfo/minimumRate | 이차보전이 가장 작은 지원 정보의 추천 기관명을 1건 리턴합니다. |
| POST | /api/assistanceinfo/match | 'region' 필드를 받아서 해당 지자체의 지원 정보를 1건 리턴합니다. |
| POST | /api/assistanceinfo/search | 'input' 필드를 받아서 가장 가깝고 적절한 지원 정보를 1건 추천합니다. |
