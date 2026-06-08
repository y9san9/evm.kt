import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.maven.publish)
}

group = "me.y9san9.evm"
version = libs.versions.evm.get()

kotlin {
    explicitApi()

    compilerOptions {
        extraWarnings = true
        allWarningsAsErrors = true
        progressiveMode = true
        freeCompilerArgs.addAll(
            "-Xcontext-sensitive-resolution",
            "-Xexpect-actual-classes",
            "-Xnested-type-aliases",
        )
        optIn.add("kotlin.ExperimentalUnsignedTypes")
    }

    jvm()
}

dependencies {
    commonMainImplementation(libs.kotlinx.serialization.json)
    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.ktor.client.cio)
    commonMainImplementation(libs.ktor.client.content.negotiation)
    commonMainImplementation(libs.ktor.client.serialization.json)
    commonMainImplementation(libs.slf4j.simple)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    pom {
        name = "evm"
        description = "Simple and idiomatic Kotlin library to access web3 APIs"
        url = "https://github.com/y9san9/evm.kt"

        licenses {
            license {
                name = "MIT"
                distribution = "repo"
                url = "https://github.com/y9san9/evm.kt/blob/main/LICENSE.md"
            }
        }

        developers {
            developer {
                id = "y9san9"
                name = "Alex Sokol"
                email = "y9san9@gmail.com"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/y9san9/evm.kt.git"
            developerConnection = "scm:git:ssh://github.com/y9san9/evm.kt.git"
            url = "https://github.com/y9san9/evm.kt"
        }
    }

    signAllPublications()
}
