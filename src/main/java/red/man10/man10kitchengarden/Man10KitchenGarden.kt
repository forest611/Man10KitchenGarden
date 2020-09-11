package red.man10.man10kitchengarden

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

import org.bukkit.persistence.PersistentDataType.STRING
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class Man10KitchenGarden : JavaPlugin() {

    companion object{

        lateinit var plugin: Man10KitchenGarden

        lateinit var multiBlock: MultiBlock
        lateinit var recipe : Recipe

        lateinit var planter: Planter
        lateinit var inventory: Inventory

        var planterID = 216

        fun setData(item:ItemStack,key:String,value:String){
            val meta = item.itemMeta
            meta.persistentDataContainer.set(NamespacedKey(plugin, key), STRING,value)
            item.itemMeta = meta
        }

        fun getData(item: ItemStack,key: String):String?{
            if (!item.hasItemMeta())return null
            return item.itemMeta.persistentDataContainer[NamespacedKey(plugin, key), STRING]?:return null
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player)return false

        if (!sender.hasPermission("man10garden.op"))return true

        if (args.isEmpty()){

            sender.sendMessage("§a/mkg planter ... ２種類のプランターを取得")
            sender.sendMessage("§a/mkg recipe <name>  ... レシピを作るメニューを表示")

            return true
        }

        if (args[0] == "planter"){

            sender.inventory.addItem(planter.getPlanter())
            sender.inventory.addItem(planter.getPlanterEx())

            return true
        }

        if (args[0] == "recipe"){

                inventory.setRecipe(sender,args[1])

            return true
        }

        return false
    }

    override fun onEnable() {
        // Plugin startup logic

        plugin = this
        multiBlock = MultiBlock()
        recipe = Recipe()
        planter = Planter()
        inventory = Inventory()

        server.pluginManager.registerEvents(multiBlock,this)
        server.pluginManager.registerEvents(inventory,this)

        saveDefaultConfig()
//        planterID = config.getInt("planter")

        recipe.load()

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