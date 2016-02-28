# gs-batch-processing-complete
Spring Batch processing HelloWorld application generated from Spring STS

# Appendix

## Example when writting in Oracle database

**Maven dependency**  
- Download Oracle driver [ojdbc6 11.2.0.4](http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html)
- Install the library in your local repository

```
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar -Dfile=path-to-ojdbc6.jar -DgeneratePom=true
```

Modify `pom.xml`

```xml
<dependency>
	<groupId>com.oracle</groupId>
	<artifactId>ojdbc6</artifactId>
	<version>11.2.0.4</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```


**Oracle**  
- Install locally Oracle Database Express Edition
- Install Oracle SQL Developer

**Initialize**  
- Create the table people
- Create the sequence for auto increment

```
CREATE TABLE people  (
    person_id INTEGER NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE SEQUENCE s_people;
```

**Spring configuration**  
Example of application.yml configuration.  
- `batch.initializer.enabled` tells to Spring to create Spring batch monitoring tables.
- `spring.datasource.initialize` tells to Spring to load schema-{platform}.sql script.
- `spring.datasource.platform` tells the platform.

```
oracle:
  url: jdbc:oracle:thin:@//localhost:1521/xe
  username: xxx
  password: yyy
  #driver-class-name: oracle.jdbc.OracleDriver

spring:
  #batch.initializer.enabled: false
  datasource:
    initialize: false
    platform: oracle
  jpa.database-platform: org.hibernate.dialect.OracleDialect
```

**Datasource**  

```java
package hello.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@ConfigurationProperties
public class OracleConfiguration {

	@Value("${oracle.username}")
	private String username;

	@Value("${oracle.password}")
	private String password;

	@Value("${oracle.url}")
	private String url;

	@Bean
	DataSource dataSource() throws SQLException {

		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setURL(url);
		dataSource.setImplicitCachingEnabled(true);
		dataSource.setFastConnectionFailoverEnabled(true);
		return dataSource;
	}
}
```

**Insert**  

In the writer

```java
writer.setSql("INSERT INTO people (person_id, first_name, last_name) VALUES (s_people.nextval, :firstName, :lastName)"); // oracle with s_people sequence
```
