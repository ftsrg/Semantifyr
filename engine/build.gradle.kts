import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download

plugins {
    id("hu.bme.mit.gamma.gradle.conventions.application")
    kotlin("jvm") version "1.9.10"
}

kotlin {
    jvmToolchain(17)
}

val gammaVersion = libs.versions.gamma.get()
val gammaJarsZip = "v$gammaVersion/gamma-tool.zip"

val downloadGammaJars by tasks.registering(Download::class) {
    src("https://github.com/ftsrg/gamma/releases/download/$gammaJarsZip")
    overwrite(false)
    dest("gamma.$gammaVersion.zip")
}

val unzipGammaJars by tasks.registering(Sync::class) {
    dependsOn(downloadGammaJars)
    from(zipTree("gamma.$gammaVersion.zip"))
    into("gamma-libs")
}

tasks.compileKotlin {
    dependsOn(unzipGammaJars)
}

tasks.test {
    inputs.dir("Test Models")
}

dependencies {
    implementation(project(":hu.bme.mit.gamma.oxsts.lang"))
    implementation(project(":hu.bme.mit.gamma.oxsts.model"))

    implementation(libs.kotlinx.cli)

    testImplementation("commons-io:commons-io:2.14.0")
    testImplementation(project(":hu.bme.mit.gamma.oxsts.lang"))
    testImplementation(testFixtures(project(":hu.bme.mit.gamma.oxsts.lang")))

//    implementation(fileTree("gamma-libs"))
}
