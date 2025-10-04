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
    implementation("net.dv8tion:JDA:5.6.1")
    implementation(project(":discord-sync-core"))
    implementation(project(":discord-sync-api"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
