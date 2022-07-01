import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.6.21" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "kr.dreamstory.main"
version = "1.0.0"

allprojects {

    apply {
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.purpurmc.org/snapshots")
    }

    dependencies {
        implementation("org.purpurmc.purpur:purpur-api:1.18.2-R0.1-SNAPSHOT")
        implementation("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    }
}