plugins {
    kotlin("jvm") version "1.9.22"
}

group = "io.drevezadur.scylla"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
    mavenLocal()
}


tasks.wrapper {
    gradleVersion = "8.6"
}

