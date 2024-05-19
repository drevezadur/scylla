import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
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
    implementation(project(":battleship-lang"))
    implementation(libs.slf4j.api)
    implementation(libs.jackson.kotlin)
    implementation(libs.spring.context)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(project(":battleship-scenario-dsl-domain-driver"))

    testRuntimeOnly(libs.junit.platform)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}
