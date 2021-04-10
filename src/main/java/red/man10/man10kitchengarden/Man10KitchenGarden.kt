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
import kotlin.jvm.Throws

class Man10KitchenGarden : JavaPlugin() {

    companion object{

        lateinit var plugin: Man10KitchenGarden
        lateinit var planterRecipe : Recipe
        lateinit var compressorRecipe : Recipe
        lateinit var EXPMakingMachineRecipe : Recipe

        val titles = mutableListOf<String>()

        var pluginEnable = true

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player)return false

        if (!sender.hasPermission("man10garden.op"))return true

        if (args.isEmpty()){

            sender.sendMessage("§a/mkg planter ... ２種類のプランターを取得")
            sender.sendMessage("§a/mkg compressor ... 圧縮機を取得")
            sender.sendMessage("§a/mkg expmakingmachine ... 経験値作製機を取得")
            sender.sendMessage("§a/mkg recipe <name> <planter/compressor/expmakingmachine>  ... レシピを作るメニューを表示")

            return true
        }

        if (args[0] == "planter"){

            sender.inventory.addItem(Planter.getPlanter())
            sender.inventory.addItem(Planter.getPlanterEx())

            return true
        }



        if (args[0] == "recipe"){

                Inventory.setRecipe(sender,args[1],args[2])

            return true
        }

        if (args[0] == "off"){
            pluginEnable = false
            return true
        }

        if (args[0] == "on"){
            pluginEnable = true
            return true
        }

        return false
    }

    override fun onEnable() {
        // Plugin startup logic

        plugin = this
        planterRecipe = Recipe("planter")

        server.pluginManager.registerEvents(MultiBlock,this)
        server.pluginManager.registerEvents(Inventory,this)

        saveDefaultConfig()
//        planterID = config.getInt("planter")

        planterRecipe.load()
        compressorRecipe.load()
        EXPMakingMachineRecipe.load()

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