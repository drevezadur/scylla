plugins {
    kotlin("jvm") version "1.9.22"
}

group = "io.drevezerezh.scylla.advanced"

repositories {
    mavenCentral()
    mavenLocal()
}


tasks.wrapper {
    gradleVersion = "8.6"
}

