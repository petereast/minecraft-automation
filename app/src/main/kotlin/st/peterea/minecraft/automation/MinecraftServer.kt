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
    fun createNewServer(identifier: String, sshKeys: List<SshKey>, startingScript: StartupScript?): VirtualServer

    // TODO: Gets an existing virtual server
    // fun getExistingServer(identifier: String): VirtualServer

    // Gets a list of the pre-installed SSH keys
    fun getSshKeys(): List<SshKey>

    // TODO: Gets a list of assignable ip addresses
    // fun getFloatingIps(): List<FloatingIp>
}

// TODO: Ideally we should use a custom image that will save having to install java & minecraft.
class DemoStartupScript : StartupScript {
    override fun genereateScript(): String {
        return """#!/bin/bash
touch /root/startup.txt
echo "this has worked" > /root/startup.txt

apt install -y openjdk-11-jre-headless 
wget https://launcher.mojang.com/v1/objects/1b557e7b033b583cd9f66746b7a9ab1ec1673ced/server.jar
echo "eula=true" > eula.txt
java -Xmx1024M -Xms1024M -jar server.jar nogui""".also(::println)
    }
}

class MinecraftServer constructor(provider: CloudProvider, identifierProvider: MinecraftServerIdentifier) {
    val cloudProvider: CloudProvider = provider
    val identifierProvider: MinecraftServerIdentifier = identifierProvider

    fun createNew() {
        val server = this.cloudProvider.createNewServer(
            this.identifierProvider.getIdentifier(),
            this.cloudProvider.getSshKeys(),
            DemoStartupScript()
        )

        server.start()

        // Create a fresh volume eventually

        // Create a new DO droplet
    }
}
