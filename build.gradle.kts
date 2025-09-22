plugins {
    kotlin("jvm") version "2.2.10"
    java

    id("com.gradleup.shadow") version "8.3.0"
    id("com.diffplug.spotless") version "6.25.0" apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

        implementation("org.jetbrains.exposed:exposed-core:0.52.0")
        implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")
        implementation("org.jetbrains.exposed:exposed-java-time:0.52.0")

        implementation("com.zaxxer:HikariCP:5.1.0")
        implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")
        implementation("io.javalin:javalin:6.7.0")
        implementation("org.slf4j:slf4j-simple:2.0.16")
        implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    }
}

// /build.gradle.kts (루트 프로젝트)

subprojects {
    // 각 서브모듈에 spotless 플러그인을 적용합니다.
    apply(plugin = "com.diffplug.spotless")

    // spotless 설정을 구성합니다.
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        // 모든 Kotlin 소스 파일(*.kt)에 적용될 규칙
        kotlin {
            target("src/**/*.kt") // 적용할 파일 경로 지정 (기본값이라 생략 가능)

            // ktlint 최신 버전을 사용하도록 설정합니다.
            // 버전을 명시하면 특정 버전으로 고정할 수 있습니다.
            ktlint()
                .setEditorConfigPath("$rootDir/.editorconfig") // 루트의 .editorconfig 파일을 사용하도록 지정

            // 라이선스 헤더 등을 추가할 수도 있습니다.
            // licenseHeaderFile("$rootDir/spotless/copyright.kt")
        }

        // 추가: Gradle Kotlin DSL 파일(*.gradle.kts)에도 적용
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }
}

group = "kr.doka.lab"
version = "1.0-SNAPSHOT"

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

tasks.named<Jar>("jar") {
    dependsOn(":discord-sync-core:jar", ":discord-sync-api:jar", ":discord-sync-plugin:jar", ":discord-sync-auth:jar")
    from(project(":discord-sync-core").extensions.getByType<SourceSetContainer>()["main"].output)
    from(project(":discord-sync-api").extensions.getByType<SourceSetContainer>()["main"].output)
    from(project(":discord-sync-plugin").extensions.getByType<SourceSetContainer>()["main"].output)
    from(project(":discord-sync-auth").extensions.getByType<SourceSetContainer>()["main"].output)
    destinationDirectory.set(file("D:\\Servers\\abdlcraft\\plugins"))
}
