package red.man10.man10kitchengarden

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType.*
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planterID
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.plugin
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.recipe
import java.util.*

class Planter {


    val planter0 : ItemStack = ItemStack(Material.IRON_NUGGET)

    init {

        val meta = planter0.itemMeta
        meta.setCustomModelData(planterID)
        planter0.itemMeta = meta
    }

    fun getPlanter():ItemStack{
        val meta = planter0.itemMeta
        meta.setCustomModelData(planterID)
        planter0.itemMeta = meta

        meta.setDisplayName("§aプランター")
        meta.lore = mutableListOf("§f危険なハッパや、作物を育てる万能プランター","§f一度設置すると回収できない")

        planter0.itemMeta = meta

        setString(planter0,"name","planter")

        return planter0
    }

    fun getPlanterEx():ItemStack{
        val meta = planter0.itemMeta
        meta.setCustomModelData(planterID)
        planter0.itemMeta = meta

        meta.setDisplayName("§b改良型プランター")
        meta.lore = mutableListOf("回収することができる改良型プランター","保持できる水の量も増えている")

        meta.addEnchant(Enchantment.LUCK,0,false)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        planter0.itemMeta = meta

        setString(planter0,"name","planterEX")

        return planter0
    }

    fun isUsed(planter: ItemStack,slot: Int):Boolean{
        if (getString(planter,"$slot.output") == null)return false
        return true
    }

    fun isEx(planter: ItemStack):Boolean{
        if(getString(planter,"name") == "planterEX")return true
        return false
    }

    fun set(planter:ItemStack,input:ItemStack,slot:Int):Boolean{

        val name = recipe.getRecipe(input)?:return false

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,3)

        val output = recipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        setString(planter,"$slot.output", plugin.itemToBase64(output))
        setString(planter,"$slot.time",time.time.time.toString())

        return true
    }

    fun isFinish(planter: ItemStack,slot: Int):ItemStack?{

        val time = getString(planter,"$slot.time")?:return null

        if (time.toLong()>Date().time)return null

        val output = plugin.itemFromBase64(getString(planter,"$slot.output")!!)?:return null

        delete(planter,"$slot.output")
        delete(planter,"$slot.time")

        return output

    }

    fun getFinishTime(planter: ItemStack,slot: Int): Long {

        return getString(planter,"$slot.time")!!.toLong()

    }




//    fun add(input: ItemStack,planter: ItemStack):Boolean{
//        val name = recipe.getRecipe(input)?:return false
//
//        if ((getString(planter,"recipe")?:return false) !=name)return false
//
//        var amount = getInt(planter,"amount")
//
//        if (amount>=64)return false
//
//        if (!useWater(planter))return false
//
//        amount ++
//
//        setInt(planter,"amount", amount)
//        setString(planter,"recipe",name)
//
//        return true
//    }
//
//    fun take(planter: ItemStack):ItemStack?{
//
//        val meta = planter.itemMeta
//        val now = meta.persistentDataContainer[NamespacedKey(plugin,"amount"), INTEGER]?:0
//
//        if (now == 0){
//            return null
//        }
//
//        meta.persistentDataContainer.set(NamespacedKey(plugin,"amount"), INTEGER,(now - 1))
//
//        return recipe.getRecipe(meta.persistentDataContainer[NamespacedKey(plugin,"recipe"), STRING]!!)!!.output
//    }
//
//    //6時間でなくなる(麻薬一つ2時間で作る) test
//    fun addWater(planter: ItemStack):Boolean{
//        var water = getInt(planter,"water")
//
//        if (water >= 3){
//            return false
//        }
//        water += 3
//
//        setInt(planter,"water",water)
//        return true
//    }
//
//    fun useWater(planter: ItemStack):Boolean{
//        var water = getInt(planter,"water")
//
//        if (water <1)return false
//
//        water --
//
//        setInt(planter,"water",water)
//        return true
//    }




    ///////////////////////////////////////////////////////////////////

    fun setString(item:ItemStack,key:String,value:String){
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin, key), STRING,value)
        item.itemMeta = meta
    }

    fun getString(item: ItemStack,key: String):String?{
        if (!item.hasItemMeta())return null
        return item.itemMeta.persistentDataContainer[NamespacedKey(plugin, key), STRING]?:return null
    }

    fun setInt(item:ItemStack,key:String,value:Int){
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin, key), INTEGER,value)
        item.itemMeta = meta
    }

    fun getInt(item: ItemStack,key: String):Int{
        if (!item.hasItemMeta())return 0
        return item.itemMeta.persistentDataContainer[NamespacedKey(plugin, key), INTEGER]?:0
    }

    fun delete(item:ItemStack,key: String){
        val meta = item.itemMeta
        meta.persistentDataContainer.remove(NamespacedKey(plugin,key))
        item.itemMeta = meta
    }

}