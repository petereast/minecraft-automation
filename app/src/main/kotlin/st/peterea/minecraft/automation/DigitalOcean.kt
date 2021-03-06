package st.peterea.minecraft.automation

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DigitalOceanVirtualServer constructor(id: String) : VirtualServer {
    val droplet_id: String = id
    override fun start() { }
    override fun stop() { }
    override fun getIp() = ""

    override fun getId() = this.droplet_id

    override fun assignIp(ip: FloatingIp) { }
}

class DigitalOcean : CloudProvider {
    val client = HttpClient.newBuilder().build()
    val klaxon = Klaxon()

    val authToken = System.getenv("DO_TOKEN") ?: ""

    class NewDropletRequest constructor(name: String, size: String, ssh_keys: List<SshKey>, user_data: String?) {
        val name: String = name
        val region: String = "lon1"
        val size: String = size
        val image: String = "ubuntu-20-04-x64"
        val ssh_keys: List<String> = ssh_keys.map({ key -> key.id })

        @Json(name = "user_data")
        val userData: String? = user_data
        val backups: Boolean = false
        val monitoring: Boolean = true
        val tags: List<String> = listOf("minecraft-server", "auto-create")
    }

    class NewDropletResponse(
        val droplet: DropletResponse
    )
    class DropletResponse(
        val id: Int,
        val name: String,
        val status: String,
        @Json(name = "size_slug")
        val sizeSlug: String
    )

    override fun createNewServer(identifier: String, sshKey: List<SshKey>, startingScript: StartupScript?): VirtualServer {
        // Create a droplet

        val droplet = NewDropletRequest(identifier, "s-1vcpu-2gb", sshKey, startingScript?.genereateScript())

        val request = HttpRequest.newBuilder(URI.create("https://api.digitalocean.com/v2/droplets"))
            .header("Authorization", "Bearer ${this.authToken}")
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(this.klaxon.toJsonString(droplet))
            ).build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.statusCode())
        if (response.statusCode() == 202) {
            val responseJson = this.klaxon.parse<NewDropletResponse>(response.body())
            if (responseJson?.droplet?.id != null) {
                // If we can't deserialise this, throw an error!
                return DigitalOceanVirtualServer(responseJson.droplet.id.toString())
            } else {
                throw Exception("Response did not contain an ID")
            }
        } else {
            throw Exception("aaaaaa")
        }
    }

    class DigitalOceanSshKey(
        val id: Int,
        val fingerprint: String,
        @Json(name = "public_key")
        val publicKey: String,
        val name: String
    ) {
        fun toSshKey(): SshKey = SshKey(this.id.toString())
    }

    class GetSshKeysResponse(
        @Json(name = "ssh_keys")
        val sshKeys: List<DigitalOceanSshKey>
    )

    override fun getSshKeys(): List<SshKey> {
        val request = HttpRequest.newBuilder(URI.create("https://api.digitalocean.com/v2/account/keys"))
            .header("Authorization", "Bearer ${this.authToken}")
            .header("Content-Type", "application/json")
            .GET().build()

        val rawResponse = this.client.send(request, HttpResponse.BodyHandlers.ofString())

        if (rawResponse.statusCode() != 200) {
            throw InternalError(rawResponse.body())
        }

        val response = this.klaxon.parse<GetSshKeysResponse>(rawResponse.body())

        return (response?.sshKeys ?: listOf()).map({ key -> key.toSshKey() })
    }

    override fun destroyInstance(server: VirtualServer) {
        val id = server.getId()
        val request = HttpRequest.newBuilder(URI.create("https://api.digitalocean.com/v2/droplets/$id"))
            .header("Authorization", "Bearer ${this.authToken}")
            .header("Content-Type", "application/json")
            .DELETE().build()

        val response = this.client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 204) {
            throw InternalError(response.body())
        }
    }
}
