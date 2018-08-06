# Toast API Server
## 개발 작업 준비
### 소스 코드 다운로드
우선 다음 명령어를 사용하여 저장소를 클론합니다. 
```
git clone https://github.com/teamtoast/toast-server.git
```

### 데이터베이스 설정
테스트용 데이터베이스에 다음 SQL문을 실행하여 테이블을 생성합니다. 데이터베이스는 MariaDB 혹은 MySQL을 사용해야 합니다.
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
)
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