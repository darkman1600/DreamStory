group = "kr.dreamstory.ability"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation(project(":DS-Library"))
    compileOnly("com.comphenix.protocol","ProtocolLib","4.8.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.0.3")
}
