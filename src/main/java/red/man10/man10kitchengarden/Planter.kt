package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planterRecipe
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.titles

object Planter :MultiItem(){

    private const val planterID = 216

    const val planterName = "§aプランター"
    const val planterEXName = "§b改良型プランター"

    private val planterItem : ItemStack = ItemStack(Material.IRON_NUGGET)

    init {

        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta

        titles.add(planterName)
        titles.add(planterEXName)
    }

    fun getPlanter():ItemStack{
        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta

        meta.setDisplayName(planterName)
        meta.lore = mutableListOf("§f危険なハッパや、作物を育てる万能プランター","§f一度設置すると回収できない")

        planterItem.itemMeta = meta

        setString(planterItem,"name","planter")

        return planterItem
    }

    fun getPlanterEx():ItemStack{
        val meta = planterItem.itemMeta
        meta.setCustomModelData(planterID)
        planterItem.itemMeta = meta

        meta.setDisplayName(planterEXName)
        meta.lore = mutableListOf("§f回収することができる改良型プランター","§f肥料などが使えるようになってる")

        planterItem.itemMeta = meta

        setString(planterItem,"name","planterEX")

        return planterItem
    }

    fun isEx(planter: ItemStack):Boolean{
        if(getString(planter,"name") == "planterEX")return true
        return false
    }

    fun setRecipe(item: ItemStack, input: ItemStack, slot:Int):Boolean{

        val name = planterRecipe.getRecipe(input)?:return false

        val output = planterRecipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        super.set(item, output, slot)

        return true
    }

}