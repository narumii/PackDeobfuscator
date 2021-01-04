import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "pl.alpheratzteam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("http://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("org.ow2.asm:asm:9.0")
    implementation("org.ow2.asm:asm-tree:9.0")
    implementation("org.ow2.asm:asm-commons:9.0")
    implementation("org.ow2.asm:asm-util:9.0")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.google.guava:guava:30.1-jre")
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("pl.alpheratzteam.deobfuscator.MainKt")
}