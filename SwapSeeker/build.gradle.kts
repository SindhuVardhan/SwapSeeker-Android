buildscript {
    val kotlin_version by extra("2.0.0-Beta3")
    dependencies {
//        classpath("com.google.gms:google-services:4.4.0")
        classpath ("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.gms:google-services:4.4.0")
    }
    repositories {
        mavenCentral()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
}
dependencies {

}
