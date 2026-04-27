pluginManagement {
    includeBuild("plugins")
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

rootProject.name = "DocVault"
include(":app")
include(":core:common")
include(":core:designsystem")
include(":feature:home:home_presentation")
include(":feature:document:document_data")
include(":feature:document:document_domain")
include(":feature:detail:detail_presentation")
