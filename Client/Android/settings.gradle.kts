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
    }
}

rootProject.name = "Metroid Store"
include(":MetroidStore")
include(":BackendModel")
include(":BackendClient")
include(":CustomerClientModel")
include(":EmbeddedBackend")
