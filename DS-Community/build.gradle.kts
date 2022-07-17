group = "kr.dreamstory.community"
version = "1.0.0"

repositories {
    mavenCentral()
    maven ("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(project(":DS-Library"))
    implementation(project(":DS-Ability"))
    compileOnly("world.bentobox:bentobox:1.20.1-SNAPSHOT")
}