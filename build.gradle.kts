plugins {
    java
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.hoangvo"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

//    // https://mvnrepository.com/artifact/org.hibernate.javax.persistence/hibernate-jpa-2.0-api
//    implementation("org.hibernate.javax.persistence:hibernate-jpa-2.0-api:1.0.0.Final")

//    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
//    implementation("javax.persistence:javax.persistence-api:2.2")
//
//
//    // https://mvnrepository.com/artifact/com.h2database/h2
//    testImplementation("com.h2database:h2:2.2.224")
//
//    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.1")

    // https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

    // https://mvnrepository.com/artifact/com.google.firebase/firebase-admin
    implementation("com.google.firebase:firebase-admin:9.2.0")



    // https://mvnrepository.com/artifact/org.hibernate.common/hibernate-commons-annotations
    implementation("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:8.2.0")

    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // https://mvnrepository.com/artifact/org.jboss.logging/jboss-logging
    implementation("org.jboss.logging:jboss-logging:3.5.3.Final")

    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")


    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
