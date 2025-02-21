// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("room_version", "2.5.0")
    }
}

plugins {
    alias(libs.plugins.androidApplication) version "8.8.0" apply false
    alias(libs.plugins.androidLibrary) version "8.8.0" apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) version "2.1.10" apply false
    alias(libs.plugins.ksp) version "2.1.10-1.0.29" apply false
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}