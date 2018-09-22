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

CREATE TABLE `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nickname` varchar(20) NOT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `gender` enum('male','female') DEFAULT NULL,
  `age` int(11) NOT NULL,
  `level` int(11) NOT NULL DEFAULT 0,
  `picture` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `expired_at` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_nickname` (`nickname`),
  KEY `users_level_IDX` (`level`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

CREATE TABLE `kakao_accounts` (
  `user` bigint(20) unsigned NOT NULL,
  `kakao_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user`),
  UNIQUE KEY `kakao_accounts_UN` (`kakao_id`),
  CONSTRAINT `kakao_accounts_users_FK` FOREIGN KEY (`user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

```

서버 프로그램의 설정 파일을 작성합니다. Working Directory에 config.json 파일로 작성하며, IntelliJ IDEA의 경우 프로젝트 최상위 폴더에 작성하면 됩니다.
항목은 다음과 같습니다.

* host: DB 호스트
* port: DB 포트
* database: DB 이름
* user: DB 사용자
* password: DB 비밀번호

예
```json
{
  "database": {
    "host": "localhost",
    "port": 3306,
    "name": "toast",
    "user": "toast",
    "password": "1234"
  },
  "token_secret": "jwt-token-secret"
}