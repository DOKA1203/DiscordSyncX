plugins {
    kotlin("kapt")
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}
