package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

open class MultiItem{

    private val air = ItemStack(Material.AIR)
    private val output = "output"
    private val time = "time"
    private val fertilizer = "fertilizer"
    private val fuel = "fuel"

    fun isUsed(planter: ItemStack, slot: Int):Boolean{
        if (getString(planter,"$slot.$output") == null)return false
        return true
    }



    fun set(planter: ItemStack, input: ItemStack, slot:Int):Boolean{

        val name = Recipe.getRecipe(input)?:return false

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,3)
//        time.add(Calendar.MINUTE,1)

        val output = Recipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        setString(planter,"$slot.${this.output}", Man10KitchenGarden.plugin.itemToBase64(output))
        setLong(planter,"$slot.${this.time}",time.time.time)

        return true
    }

    fun isFinish(planter: ItemStack, slot: Int): ItemStack?{

        if (hasFuel(planter))return null
        if (getFinishTime(planter, slot)>Date().time){
            delete(planter,"$slot.$output")
            delete(planter,"$slot.$time")
            delete(planter,"$slot.$fertilizer")

            return air
        }

        val output = Man10KitchenGarden.plugin.itemFromBase64(getString(planter,"$slot.$output")!!)?:return null

        delete(planter,"$slot.${this.output}")
        delete(planter,"$slot.$time")
        delete(planter,"$slot.$fertilizer")

        return output

    }

    fun getFinishTime(planter: ItemStack, slot: Int): Long {
        return getLong(planter,"$slot.$time")
    }

//    fun setFertilizer(planter: ItemStack,time:Int){
//
//        for (slot in slots){
//
//            //既に使ってたら使えない
//            if (getString(planter,"$slot.fertilizer")!=null)continue
//
//            val date = Date()
//            date.time = getLong(planter,"$slot.time")
//            if (time == 0)continue
//
//            val calender = Calendar.getInstance()
//            calender.time =date
//            calender.add(Calendar.MINUTE,30 )
//            setString(planter,"$slot.fertilizer","used")
//
//        }
//
//    }

    //水を入れる
    fun setFuel(planter: ItemStack){

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,6)

        setLong(planter,fuel,time.time.time)
    }

    //水分があるか
    fun hasFuel(planter: ItemStack):Boolean{
        val time = getLong(planter,fuel)
        if (time< Date().time)return false
        return true
    }

    ///////////////////////////////////////////////////////////////////

    fun setString(item: ItemStack, key:String, value:String){
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin, key), PersistentDataType.STRING,value)
        item.itemMeta = meta
    }

    fun getString(item: ItemStack, key: String):String?{
        if (!item.hasItemMeta())return null
        return item.itemMeta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin, key), PersistentDataType.STRING]
    }

    fun setLong(item: ItemStack, key:String, value:Long){
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin, key), PersistentDataType.LONG,value)
        item.itemMeta = meta
    }

    fun getLong(item: ItemStack, key: String):Long{
        if (!item.hasItemMeta())return 0
        return item.itemMeta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin, key), PersistentDataType.LONG]?:0
    }

    fun delete(item: ItemStack, key: String){
        val meta = item.itemMeta
        meta.persistentDataContainer.remove(NamespacedKey(Man10KitchenGarden.plugin,key))
        item.itemMeta = meta
    }


}