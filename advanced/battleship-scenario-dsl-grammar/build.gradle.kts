plugins {
    antlr
    `java-library`
    `maven-publish`
}



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencies {
    antlr("org.antlr:antlr4:4.5")
}


repositories {
    mavenCentral()
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
}
