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
    antlr(libs.antlr)
}


repositories {
    mavenCentral()
}

tasks.generateGrammarSource {
    mustRunAfter(tasks.findByPath("explodeCodeSourceMain"))
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
}
