package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Planter :MultiItem(){

    private const val planterID = 216

    private val planterItem : ItemStack = ItemStack(Material.IRON_NUGGET)

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

    fun isEx(planter: ItemStack):Boolean{
        if(getString(planter,"name") == "planterEX")return true
        return false
    }
}