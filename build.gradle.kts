tasks {
    val printVersion by registering {
        group = "CI"

        doFirst {
            println(libs.versions.evm.get())
        }
    }
}
