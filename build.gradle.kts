import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    idea
    java
    id("org.openapi.generator") version "7.5.0"
    id("org.springframework.boot") version "3.3.0" apply true
    id("io.spring.dependency-management") version "1.1.5"
    id("io.freefair.lombok") version "8.6"
}

val jacksonDataVersion = "2.17.1"

idea {
    project {
        languageLevel = IdeaLanguageLevel(21)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.google.com/")
    }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

val generatedProjectDir = "$projectDir/build/generated"
val controllerDir = "ru.bstrdn.controller"
val modelDir = "ru.bstrdn.data.dto"
val openapiJson = "$projectDir/src/main/resources/openapi.json"

tasks.named<GenerateTask>("openApiGenerate") {

    generatorName.set("spring")
    inputSpec.set(openapiJson)
    outputDir.set(generatedProjectDir)
    apiPackage.set(controllerDir)
    modelPackage.set(modelDir)

    configOptions.putAll(
        mapOf(
            Pair("dateLibrary", "java8-localdatetime"),
            Pair("useSpringBoot3", "true"),
            Pair("documentationProvider", "springdoc"),
            Pair("interfaceOnly", "true"),
            Pair("useTags", "true"),
            Pair("hideGenerationTimestamp", "true"),
//            Pair("additionalModelTypeAnnotations", "@lombok.NoArgsConstructor @lombok.AllArgsConstructor")
//            Pair("additionalModelTypeAnnotations", "@lombok.Builder")
            Pair("additionalModelTypeAnnotations",
                "@lombok.Data @lombok.NoArgsConstructor @lombok.AllArgsConstructor @lombok.experimental.SuperBuilder"
            )
        )
    )

    doFirst {
        delete(generatedProjectDir)
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.0")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

//    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.data:spring-data-commons")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonDataVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonDataVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDataVersion")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("org.openapitools:jackson-databind-nullable:0.2.6")


    //db
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.liquibase:liquibase-core:4.28.0")
    implementation("net.lbruun.springboot:preliquibase-spring-boot-starter:1.5.0")

}

afterEvaluate {
    extensions.configure<SourceSetContainer>("sourceSets") {
        named("main") {
            java {
                setSrcDirs(listOf("src/main/java", "build/generated/src/main/java"))
            }
            resources {
                setSrcDirs(listOf("src/main/resources", "build/generated/src/main/resources"))
            }
        }
    }
}