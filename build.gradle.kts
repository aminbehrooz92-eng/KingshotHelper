buildscript {
    repositories { google(); mavenCentral() }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0")
        classpath(kotlin("gradle-plugin", version = "1.9.24"))
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
