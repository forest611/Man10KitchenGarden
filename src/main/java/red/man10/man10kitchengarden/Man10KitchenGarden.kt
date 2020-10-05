package red.man10.man10kitchengarden

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class Man10KitchenGarden : JavaPlugin() {

    companion object{

        lateinit var plugin: Man10KitchenGarden
        lateinit var planterRecipe : Recipe
        lateinit var compressorRecipe : Recipe

        val titles = mutableListOf<String>()

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player)return false

        if (!sender.hasPermission("man10garden.op"))return true

        if (args.isEmpty()){

            sender.sendMessage("§a/mkg planter ... ２種類のプランターを取得")
            sender.sendMessage("§a/mkg compressor ... 圧縮機を取得")
            sender.sendMessage("§a/mkg recipe <name> <planter/compressor>  ... レシピを作るメニューを表示")

            return true
        }

        if (args[0] == "planter"){

            sender.inventory.addItem(Planter.getPlanter())
            sender.inventory.addItem(Planter.getPlanterEx())

            return true
        }

        if (args[0] == "compressor"){
            sender.inventory.addItem(Compressor.compressorItem)
        }

        if (args[0] == "recipe"){

                Inventory.setRecipe(sender,args[1],args[2])

            return true
        }

        return false
    }

    override fun onEnable() {
        // Plugin startup logic

        plugin = this
        planterRecipe = Recipe("planter")
        compressorRecipe = Recipe("compressor")

        server.pluginManager.registerEvents(MultiBlock,this)
        server.pluginManager.registerEvents(Inventory,this)

        saveDefaultConfig()
//        planterID = config.getInt("planter")

        planterRecipe.load()
        compressorRecipe.load()

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }


    fun itemFromBase64(data: String): ItemStack? {
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())

            // Read the serialized inventory
            for (i in items.indices) {
                items[i] = dataInput.readObject() as ItemStack
            }

            dataInput.close()
            return items[0]
        } catch (e: Exception) {
            return null
        }

    }

    @Throws(IllegalStateException::class)
    fun itemToBase64(item: ItemStack): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val items = arrayOfNulls<ItemStack>(1)
            items[0] = item
            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            dataOutput.close()
            val base64: String = Base64Coder.encodeLines(outputStream.toByteArray())

            return base64

        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }
}