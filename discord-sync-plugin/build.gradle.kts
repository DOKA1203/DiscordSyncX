plugins {
    kotlin("jvm")
    java
}

group = "kr.doka.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":discord-sync-api"))
    implementation(project(":discord-sync-core"))
    implementation(project(":discord-sync-auth"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
