plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencies {
    implementation(project(":battleship-scenario-dsl"))
    implementation(project(":battleship-lang"))

    implementation(libs.kotlin.reflect)

    implementation(libs.okhttp)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.datetime)

    implementation(libs.commons.io)
    implementation(libs.slf4j.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)

    testRuntimeOnly(libs.junit.platform)
}


repositories {
    mavenCentral()
    mavenLocal()
}


tasks.test {
    useJUnitPlatform()
}
