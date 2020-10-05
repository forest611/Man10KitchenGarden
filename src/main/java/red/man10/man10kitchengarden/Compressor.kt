package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.titles

object Compressor : MultiItem(){

    private const val id = 214
    const val compressorName = "§6圧縮機"
    const val compressorID = "compressor"
    val compressorItem = ItemStack(Material.IRON_NUGGET)

    init {
        val meta = compressorItem.itemMeta

        meta.setDisplayName(compressorName)
        meta.lore = mutableListOf("§f素材を圧縮したりするときに使う","§fレンチで回収できる")

        meta.setCustomModelData(id)
        compressorItem.itemMeta = meta

        setString(compressorItem,"name", compressorID)

        titles.add(compressorName)
    }

    fun setRecipe(item: ItemStack, input: ItemStack, slot:Int):Boolean{

        val name = Man10KitchenGarden.compressorRecipe.getRecipe(input)?:return false

        val output = Man10KitchenGarden.compressorRecipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        super.set(item, output, slot)

        return true
    }

}