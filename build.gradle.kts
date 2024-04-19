import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.21"
	kotlin("plugin.spring") version "1.8.21"
}

group = "com.sudoSoo"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter", "junit-jupiter", "5.6.2")
	testImplementation("io.mockk:mockk:1.12.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.data:spring-data-envers")
	implementation ("io.github.microutils:kotlin-logging:3.0.5")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	//redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.redisson:redisson-spring-boot-starter:3.16.8")

	//h2
	runtimeOnly("com.h2database:h2")
	implementation("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	builder.set("paketobuildpacks/builder-jammy-base:latest")
}
