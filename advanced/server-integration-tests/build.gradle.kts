import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)

    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.spring") version libs.versions.kotlin
}



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencies {
    implementation(project(":battleship-lang"))
    implementation( project(":battleship-domain"))
    implementation(project(":battleship-server"))
    implementation(project(":battleship-scenario-dsl"))
    implementation(project(":battleship-scenario-dsl-rest-driver"))
    implementation(libs.spring.boot.web)

    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.datetime)
    implementation(libs.kotlin.reflect)
    implementation(libs.commons.io)
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)
    implementation(libs.logback.core)

    providedRuntime(libs.spring.boot.tomcat)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.mockk)

    testRuntimeOnly(libs.junit.platform)
}


repositories {
    mavenCentral()
    mavenLocal()
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
