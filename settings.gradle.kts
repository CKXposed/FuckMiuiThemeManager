pluginManagement {
    repositories {
        maven("https://api.xposed.info/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://api.xposed.info/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "FuckMiuiThemeManager"
include(":app")