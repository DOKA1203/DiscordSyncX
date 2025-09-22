plugins {
    kotlin("jvm")
}

group = "kr.doka.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":discord-sync-api"))
    implementation(project(":discord-sync-core"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
