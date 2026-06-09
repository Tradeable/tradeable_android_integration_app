pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    val tradeableWrapperDir = file("../tradeable_android_wrapper")
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
            url = uri(tradeableWrapperDir.resolve(".flutter_sdk/build/host/outputs/repo"))
        }
        // Fallback local repo copied by tradeable_android_wrapper/build.sh
        maven {
            url = uri(tradeableWrapperDir.resolve("tradeable-sdk/libs"))
        }
    }
}

rootProject.name = "TradeableDemo"
include(":app")
