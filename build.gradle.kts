plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.walletservice"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Spring Data JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// For Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3");

	// Liquibase
	implementation("org.liquibase:liquibase-core")

	// PostgreSQL (runtime)
	runtimeOnly("org.postgresql:postgresql")

	// Spring Boot Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Lombok
	implementation("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Cache
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
