plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.14.0"
}

group = "xyz.xenondevs"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("org.ow2.asm:asm:9.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.7.10")
}

gradlePlugin {
    plugins {
        create("string-remapper-gradle-plugin") {
            id = "xyz.xenondevs.string-remapper-gradle-plugin"
            description = "String remapper plugin for Gradle"
            implementationClass = "xyz.xenondevs.stringremapper.StringRemapperGradlePlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}