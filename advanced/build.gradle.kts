plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("com.autonomousapps.dependency-analysis") version "1.31.0"
}

group = "io.drevezerezh.scylla.advanced"

repositories {
    mavenCentral()
    mavenLocal()
}


tasks.wrapper {
    gradleVersion = "8.7"
}

