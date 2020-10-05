package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.titles

object Compressor : MultiItem(){

    private const val id = 210
    const val compressorName = "§6圧縮機"
    private val compressorItem = ItemStack(Material.IRON_NUGGET)

    init {
        val meta = compressorItem.itemMeta

        meta.setDisplayName(compressorName)
        meta.lore = mutableListOf("§f素材を圧縮したりするときに使う","§fレンチで回収できる")

        meta.setCustomModelData(id)
        compressorItem.itemMeta = meta

        setString(compressorItem,"name","compressor")

        titles.add(compressorName)
    }



}