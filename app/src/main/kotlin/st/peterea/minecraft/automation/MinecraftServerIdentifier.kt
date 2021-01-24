package st.peterea.minecraft.automation
interface MinecraftServerIdentifier {
    fun getIdentifier(): String
}

class RandomIdentifierProvider : MinecraftServerIdentifier {
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    fun charId(): Int = (Math.random() * alphabet.length).toInt()
    fun randomString(length: Int): String = String((0..length).map({ alphabet[charId()] }).toCharArray())
    override fun getIdentifier(): String = "lon1-mc-server-${randomString(5)}"
}
