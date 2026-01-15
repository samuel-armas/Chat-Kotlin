// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    dependencies{
        //El ultimo cambio fue pasar de 4.4.0 a 4.4.4
        classpath("com.google.gms:google-services:4.4.4")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false

}

