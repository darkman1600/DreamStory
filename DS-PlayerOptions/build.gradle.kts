plugins {
    id("java")
}

group = "kr.dreamstory.player_options"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":DS-Library"))
    implementation(project(":DS-Community"))
    implementation(project(":DS-Ability"))
}
