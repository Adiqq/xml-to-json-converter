import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.10"
    id("com.github.johnrengelman.shadow") version "4.0.2"
}
repositories {
    jcenter()
}
dependencies {
    compile(kotlin("stdlib"))
    testImplementation("junit:junit:4.12")
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.7")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.9.7")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.7")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.8.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    baseName = "XmlToJsonConverter"
    classifier = ""
    version = ""
}