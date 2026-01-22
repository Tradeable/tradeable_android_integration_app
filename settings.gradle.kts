pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://storage.googleapis.com/download.flutter.io") }
        // Local AAR repository
        flatDir {
            dirs("libs")
        }
        // Flutter module repository (built by wrapper build script)
        maven {
            url = uri("../tradeable-android-wrapper/.flutter_sdk/build/host/outputs/repo")
        }
    }
}

rootProject.name = "TradeableDemo"
include(":app")
