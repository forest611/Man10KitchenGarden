package red.man10.man10kitchengarden

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.titles

object EXPMakingMachine : MultiItem(){

    private const val id = 214
    const val expmakingmachinename = "§a§l経験値作製機"
    const val expmakingmachineid = "expmakingmachine"
    val expmakingmachineitem = ItemStack(Material.IRON_NUGGET)

    init {
        val meta = expmakingmachineitem.itemMeta

        meta.setDisplayName(expmakingmachinename)
        meta.lore = mutableListOf("§a経験値を作成するときに使う","§aレンチで回収できる")

        meta.setCustomModelData(id)
        expmakingmachineitem.itemMeta = meta

        setString(expmakingmachineitem,"name", "expmakingmachine")

        titles.add(expmakingmachinename)
    }

    fun setRecipe(item: ItemStack, input: ItemStack, slot:Int):Boolean{

        val name = Man10KitchenGarden.EXPMakingMachineRecipe.getRecipe(input)?:return false

        val output = Man10KitchenGarden.EXPMakingMachineRecipe.getRecipe(name)!!.output.clone()

        output.amount = input.amount

        super.set(item, output, slot)

        return true
    }

}