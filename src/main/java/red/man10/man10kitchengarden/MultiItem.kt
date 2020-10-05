package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

open class MultiItem{

    private val air = ItemStack(Material.AIR)
    val output = "output"
    val time = "time"
    private val fertilizer = "fertilizer"
    private val fuel = "fuel"

    /**
     * スロットが使用中かどうか
     */
    fun isUsed(item: ItemStack, slot: Int):Boolean{
        if (getString(item,"$slot.$output") == null)return false
        return true
    }


    fun isFinish(item: ItemStack, slot: Int): ItemStack?{

        if (hasFuel(item))return null
        if (getFinishTime(item, slot)>Date().time){
            delete(item,"$slot.$output")
            delete(item,"$slot.$time")
            delete(item,"$slot.$fertilizer")

            return air
        }

        val output = Man10KitchenGarden.plugin.itemFromBase64(getString(item,"$slot.$output")!!)?:return null

        delete(item,"$slot.${this.output}")
        delete(item,"$slot.$time")
        delete(item,"$slot.$fertilizer")

        return output

    }

    fun getFinishTime(item: ItemStack, slot: Int): Long {
        return getLong(item,"$slot.$time")
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

    open fun set(item: ItemStack, output: ItemStack, slot:Int):Boolean{

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,3)

        setString(item,"$slot.${output}", Man10KitchenGarden.plugin.itemToBase64(output))
        setLong(item,"$slot.${time}",time.time.time)

        return true
    }

    //燃料を入れる
    fun setFuel(item: ItemStack){

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,6)

        setLong(item,fuel,time.time.time)
    }

    //燃料があるか
    fun hasFuel(item: ItemStack):Boolean{
        val time = getLong(item,fuel)
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