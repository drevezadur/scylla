import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
}

group = "io.drevezadur.scylla"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
    mavenLocal()
}


tasks.wrapper {
    gradleVersion = "8.4"
}

