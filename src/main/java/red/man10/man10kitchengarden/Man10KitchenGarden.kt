package red.man10.man10kitchengarden

import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Man10KitchenGarden : JavaPlugin() {

    companion object{

        lateinit var plugin: JavaPlugin

        lateinit var multiBlock: MultiBlock

        fun setData(item:ItemStack,key:String,value:String){
            val meta = item.itemMeta
            meta.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.STRING,value)
            item.itemMeta = meta
        }

        fun getData(item: ItemStack,key: String):String?{
            if (!item.hasItemMeta())return null
            return item.itemMeta.persistentDataContainer[NamespacedKey(plugin, key), PersistentDataType.STRING]?:return null
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        return false
    }

    override fun onEnable() {
        // Plugin startup logic

        multiBlock = MultiBlock()

        server.pluginManager.registerEvents(multiBlock,this)

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}