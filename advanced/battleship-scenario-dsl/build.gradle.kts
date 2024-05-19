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
    implementation(project(":battleship-scenario-dsl-grammar"))
    implementation(project(":battleship-lang"))
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
}


tasks.test {
    useJUnitPlatform()
}