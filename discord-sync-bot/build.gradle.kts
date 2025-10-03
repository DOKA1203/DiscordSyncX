plugins {
    kotlin("jvm")
}

group = "kr.doka.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.17.0")
    implementation(project(":discord-sync-core"))
    implementation(project(":discord-sync-api"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
