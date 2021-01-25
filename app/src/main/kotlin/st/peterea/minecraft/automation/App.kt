/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package st.peterea.minecraft.automation

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main(args: Array<String>) {
    val rip = RandomIdentifierProvider()
    val digitalOcean = DigitalOcean()

    println("Starting")
    val mc = MinecraftServer(digitalOcean, rip)
    mc.createNew()
    println("Done")
}
