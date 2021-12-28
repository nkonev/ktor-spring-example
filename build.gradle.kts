import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "com.example.ktor"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.ktor:ktor-server-cio:1.6.4")
    implementation("io.ktor:ktor-server-core:1.6.4")
    implementation("io.ktor:ktor-server:1.6.4")
    implementation("io.ktor:ktor-utils:1.6.4")
    implementation("io.ktor:ktor:1.6.4")
    implementation("io.ktor:ktor-jackson:1.6.4")
    implementation("io.ktor:ktor-server-sessions:1.6.4")
    implementation("redis.clients:jedis:3.6.3")
    implementation("org.litote.kmongo:kmongo:4.3.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
