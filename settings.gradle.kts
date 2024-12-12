pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google() // Google's Maven repository
        mavenCentral() // Maven Central repository
        maven { url = uri("https://storage.googleapis.com/download.tensorflow.org/maven/") } // Custom TensorFlow repo
    }

}


rootProject.name = "FruLens"
include(":app")
