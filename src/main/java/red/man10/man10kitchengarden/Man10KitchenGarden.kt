package red.man10.man10kitchengarden

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Man10KitchenGarden : JavaPlugin() {

    companion object{

        lateinit var plugin: JavaPlugin

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

    override fun onEnable() {
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}