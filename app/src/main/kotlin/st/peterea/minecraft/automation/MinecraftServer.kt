package st.peterea.minecraft.automation

// Represents a SshKey that can be associated with a virtual server
class SshKey constructor (id: String) {
    val id: String = id
}

// Something that will generate a runnable script that will be run at startup.
interface StartupScript {
    fun genereateScript(): String
}

interface FloatingIp

interface VirtualServer {
    fun start(): Unit
    fun stop(): Unit
    fun getIp(): String
    fun assignIp(ip: FloatingIp): Unit
}

interface CloudProvider {
    // Creates a new virtual server
    fun createNewServer(identifier: String, sshKey: SshKey, startingScript: StartupScript?): VirtualServer

    // TODO: Gets an existing virtual server
    // fun getExistingServer(identifier: String): VirtualServer

    // Gets a list of the pre-installed SSH keys
    fun getSshKeys(): List<SshKey>

    // TODO: Gets a list of assignable ip addresses
    // fun getFloatingIps(): List<FloatingIp>
}

class DemoStartupScript : StartupScript {
    override fun genereateScript(): String {
        return """
        #!/usr/bin/env bash

        touch /root/startup.txt
        echo "this has worked!" > /root/startup.txt
        """
    }
}

class MinecraftServer constructor(provider: CloudProvider) {
    val cloudProvider: CloudProvider = provider
    fun createNew() {

        // Somehow choose an SSH key
        val sshKey = this.cloudProvider.getSshKeys().first()

        val server = this.cloudProvider.createNewServer("testing-one", sshKey, DemoStartupScript())
        server.start()

        // Create a fresh volume eventually

        // Create a new DO droplet
    }
}
