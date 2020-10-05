package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType.LONG
import org.bukkit.persistence.PersistentDataType.STRING
import red.man10.man10kitchengarden.Inventory.slots
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planterID
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.plugin
import java.util.*

object Planter {

    val planterItem : ItemStack = ItemStack(Material.IRON_NUGGET)
    val air = ItemStack(Material.AIR)

    init {

        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta
    }

    fun getPlanter():ItemStack{
        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta

        meta.setDisplayName("§aプランター")
        meta.lore = mutableListOf("§f危険なハッパや、作物を育てる万能プランター","§f一度設置すると回収できない")

        planterItem.itemMeta = meta

        setString(planterItem,"name","planter")

        return planterItem
    }

    fun getPlanterEx():ItemStack{
        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta

        meta.setDisplayName("§b改良型プランター")
        meta.lore = mutableListOf("§f回収することができる改良型プランター","§f肥料などが使えるようになってる")

        planterItem.itemMeta = meta

        setString(planterItem,"name","planterEX")

        return planterItem
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

        val name = Recipe.getRecipe(input)?:return false

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,3)
//        time.add(Calendar.MINUTE,1)

        val output = Recipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        setString(planter,"$slot.output", plugin.itemToBase64(output))
        setLong(planter,"$slot.time",time.time.time)

        return true
    }

    fun isFinish(planter: ItemStack,slot: Int):ItemStack?{

        val time = getLong(planter,"$slot.time")

        if (time>Date().time)return null
        if (time>getLong(planter,"water")){
            delete(planter,"$slot.output")
            delete(planter,"$slot.time")
            delete(planter,"$slot.fertilizer")

            return air
        }

        val output = plugin.itemFromBase64(getString(planter,"$slot.output")!!)?:return null

        delete(planter,"$slot.output")
        delete(planter,"$slot.time")
        delete(planter,"$slot.fertilizer")

        return output

    }

    fun getFinishTime(planter: ItemStack,slot: Int): Long {
        return getLong(planter,"$slot.time")
    }

    fun setFertilizer(planter: ItemStack,time:Int){

        for (slot in slots){

            //既に使ってたら使えない
            if (getString(planter,"$slot.fertilizer")!=null)continue

            val date = Date()
            date.time = getLong(planter,"$slot.time")
            if (time == 0)continue

            val calender = Calendar.getInstance()
            calender.time =date
            calender.add(Calendar.MINUTE,30 )
            setString(planter,"$slot.fertilizer","used")

        }

    }

    //水を入れる
    fun setWater(planter:ItemStack){

        val time = Calendar.getInstance()
        time.add(Calendar.HOUR_OF_DAY,6)

        setLong(planter,"water",time.time.time)
    }

    //水分があるか
    fun isWater(planter: ItemStack):Boolean{
        val time = getLong(planter,"water")
        if (time<Date().time)return false
        return true
    }

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

    fun setLong(item:ItemStack,key:String,value:Long){
        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(plugin, key), LONG,value)
        item.itemMeta = meta
    }

    fun getLong(item: ItemStack,key: String):Long{
        if (!item.hasItemMeta())return 0
        return item.itemMeta.persistentDataContainer[NamespacedKey(plugin, key), LONG]?:0
    }

    fun delete(item:ItemStack,key: String){
        val meta = item.itemMeta
        meta.persistentDataContainer.remove(NamespacedKey(plugin,key))
        item.itemMeta = meta
    }

}