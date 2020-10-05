package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Compressor : MultiItem(){

    private const val id = 210
    private val compressorItem = ItemStack(Material.IRON_NUGGET)

    init {
        val meta = compressorItem.itemMeta

        meta.setDisplayName("§6圧縮機")
        meta.lore = mutableListOf("§f素材を圧縮したりするときに使う","§fレンチで回収できる")

        meta.setCustomModelData(id)
        compressorItem.itemMeta = meta
    }



}