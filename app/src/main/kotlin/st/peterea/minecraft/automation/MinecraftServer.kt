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
    fun getId(): String
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

    // Destroy an instance
    fun destroyInstance(server: VirtualServer)
}

// TODO: Ideally we should use a custom image that will save having to install java & minecraft.
class DemoStartupScript : StartupScript {
    override fun genereateScript(): String {
        return """#!/bin/bash
apt install -y openjdk-11-jre-headless

mkdir /root/mc
cd /root/mc
wget https://launcher.mojang.com/v1/objects/1b557e7b033b583cd9f66746b7a9ab1ec1673ced/server.jar
echo "eula=true" > eula.txt
java -Xmx1024M -Xms1024M -jar server.jar nogui"""
    }
}

class VolumeStartupScript(val volumeName: String) : StartupScript {
    override fun genereateScript(): String {
        return """#!/bin/bash
apt install -y openjdk-11-jre-headless
// Mount the volume in here
mkdir -p /mnt/$volumeName

$ mount -o discard,defaults,noatime /dev/disk/by-id/scsi-0DO_Volume_$volumeName /mnt/$volumeName

cd /mnt/$volumeName
wget https://launcher.mojang.com/v1/objects/1b557e7b033b583cd9f66746b7a9ab1ec1673ced/server.jar
echo "eula=true" > eula.txt
java -Xmx1024M -Xms1024M -jar server.jar nogui"""
    }
}

class MinecraftServer constructor(provider: CloudProvider, identifierProvider: MinecraftServerIdentifier) {
    val cloudProvider: CloudProvider = provider
    val identifierProvider: MinecraftServerIdentifier = identifierProvider

    var virtualServer: VirtualServer? = null

    // This will eventually:
    // Create a new volume
    // Create a new server
    // Pair the two
    // Update the startup script to run the minecraft server inside the volume
    fun createNew() {
        if (this.virtualServer == null) {
            val server = this.cloudProvider.createNewServer(
                this.identifierProvider.getIdentifier(),
                this.cloudProvider.getSshKeys(),
                DemoStartupScript()
            )

            server.start()
            this.virtualServer = server

            println(server.getId())
        } else {
            throw Exception("Already got a server here")
        }
    }

    fun destroyServer() {
        // Perform cleanup routine
        this.cloudProvider.destroyInstance(this.virtualServer ?: throw Exception("Create a server first!"))
    }

    // Create a fresh volume eventually

    // Create a new DO droplet
}
