import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation( project(":battleship-domain"))
    implementation( project(":battleship-persistance"))
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.data.jpa)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.web)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reflect)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.h2)

    providedRuntime(libs.spring.boot.tomcat)

    testImplementation(libs.spring.boot.test)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.mockk)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
