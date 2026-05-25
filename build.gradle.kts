plugins {
	java
	id("org.springframework.boot") version "3.5.11"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.metahrms"
version = "0.0.1-SNAPSHOT"
description = " Employee Management System"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.flywaydb:flyway-core")
	implementation ("org.flywaydb:flyway-mysql")
		// Cloudinary
	implementation("com.cloudinary:cloudinary-http44:1.36.0")

	// export excel
	implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
	

    // Apache HttpClient
    implementation("org.apache.httpcomponents.client5:httpclient5")
	// WebClient (reactive) - thay RestTemplate
    implementation("org.springframework.boot:spring-boot-starter-webflux")
	// Image processing
    implementation("org.imgscalr:imgscalr-lib:4.2")
    // Apache Commons IO
    implementation("commons-io:commons-io:2.16.1")
    // Resilience4j (Circuit Breaker)
    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.1.0")
    // AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

configurations.configureEach {
    resolutionStrategy {
        force("commons-io:commons-io:2.16.1")
    }
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
