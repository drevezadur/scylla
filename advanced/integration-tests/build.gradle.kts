plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencies {
    implementation(project(":battleship-lang"))
    implementation(project(":battleship-scenario-dsl"))

    implementation(libs.okhttp)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.commons.io)
    implementation(libs.slf4j.api)
    implementation(libs.logback)


    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.kotlin.test)
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