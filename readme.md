# Toast API Server
## 개발 작업 준비
### 소스 코드 다운로드
우선 다음 명령어를 사용하여 저장소를 클론합니다.
```
git clone https://github.com/teamtoast/toast-server.git
```

### 데이터베이스 설정
테스트용 데이터베이스에 다음 SQL문을 실행하여 테이블을 생성합니다. 데이터베이스는 MariaDB 혹은 MySQL을 사용해야 합니다.
(또한 데이터베이스의 내부 인코딩이 UTF-8으로 설정이 되어 있어야 합니다.)
```mysql
CREATE TABLE `categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent` int(11) DEFAULT NULL,
  `name` varchar(32) NOT NULL,
  `imagePath` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `categories_name_IDX` (`name`) USING BTREE,
  KEY `categories_categories_FK` (`parent`),
  CONSTRAINT `categories_categories_FK` FOREIGN KEY (`parent`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO `categories` VALUES (1, NULL, '경제', NULL);
INSERT INTO `categories` VALUES (2, NULL, '정치', NULL);
INSERT INTO `categories` VALUES (3, NULL, '기술', NULL);

```

데이터베이스를 서버 프로그램에서 사용 가능하도록 설정 파일을 작성합니다. Working Directory에 database.json 파일로 작성하며, IntelliJ IDEA의 경우 프로젝트 최상위 폴더에 작성하면 됩니다.
항목은 다음과 같습니다.

* host: DB 호스트
* port: DB 포트
* database: DB 이름
* user: DB 사용자
* password: DB 비밀번호

예
```json
{
  "host": "localhost",
  "port": 3306,
  "database": "toast",
  "user": "toast",
  "password": "1234"
}

```
### 기타 환경 설정
OAuth2를 서버 프로그램에서 사용 가능하도록 설정 파일을 작성합니다. Working Directory에서 src/main/resources/application.properties의 위치에 application.properties 파일로 작성합니다.

* 현재 기능적으로 Git Hub, Facebook, Google, Kakao Talk 연동이 가능합니다.
* 웹 애플리케이션을 실행하면 기본적으로 테스트 로그인 뷰가 나오도록 설정했습니다. (/login)

예
```
spring.security.oauth2.client.registration.github.client-id=<user client-id>
spring.security.oauth2.client.registration.github.client-secret=<user secret>
## spring.security.oauth2.client.registration.github.redirect-uri-template=https://toast-ser.run.goorm.io/login/oauth2/code/{registrationId}

spring.security.oauth2.client.registration.facebook.client-id=<user client-id>
spring.security.oauth2.client.registration.facebook.client-secret=<user secret>
## spring.security.oauth2.client.registration.facebook.redirect-uri-template=https://toast-ser.run.goorm.io/login/oauth2/code/{registrationId}

spring.security.oauth2.client.registration.google.client-id=<user client-id>
spring.security.oauth2.client.registration.google.client-secret=<user secret>
## spring.security.oauth2.client.registration.google.redirect-uri-template=https://toast-ser.run.goorm.io/login/oauth2/code/{registrationId}

spring.security.oauth2.client.registration.kakao.client-id=<user client-id>
spring.security.oauth2.client.registration.kakao.client-secret=<user secret>
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.redirect-uri-template={baseUrl}/login/oauth2/code/{registrationId}
## spring.security.oauth2.client.registration.kakao.redirect-uri-template=https://toast-ser.run.goorm.io/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.kakao.scope=profile
spring.security.oauth2.client.registration.kakao.client-name=Kakao
spring.security.oauth2.client.registration.kakao.client-authentication-method=POST
spring.security.oauth2.client.provider.kakao.token-uri=https://kauth.kakao.com/oauth/token
spring.security.oauth2.client.provider.kakao.authorization-uri=https://kauth.kakao.com/oauth/authorize
spring.security.oauth2.client.provider.kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
spring.security.oauth2.client.provider.kakao.user-name-attribute=id
```