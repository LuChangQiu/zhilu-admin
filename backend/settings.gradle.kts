rootProject.name = "backend"

pluginManagement {
    repositories {
//        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
//        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin/") }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
//        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
//        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/apache-snapshots/") }
        mavenCentral()
    }
}
