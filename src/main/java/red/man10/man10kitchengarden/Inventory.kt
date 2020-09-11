package red.man10.man10kitchengarden

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.multiBlock
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planter
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.recipe
import java.text.SimpleDateFormat

class Inventory:Listener{

    val slots = listOf(10,13,16,37,43)
    val waterSlot = 40

    fun openPlanter(planter:ItemStack,p:Player){

        val isEX = Man10KitchenGarden.planter.isEx(planter)

        val inv = if (isEX){
            Bukkit.createInventory(null,54,"§b改良型プランター")
        }else{
            Bukkit.createInventory(null,54,"§aプランター")
        }

        val panel1 = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        for (i in 0..53){
            if (slots.contains(i))continue
            if (i == 40)continue
            inv.setItem(i,panel1)
        }

        val barrier = ItemStack(Material.BARRIER)
        val meta = barrier.itemMeta
        meta.setDisplayName("§c§l使用中")
        barrier.itemMeta = meta

        slots.forEach {

            val panel2 = ItemStack(Material.LIME_STAINED_GLASS_PANE)
            val meta2 = panel2.itemMeta

            if (Man10KitchenGarden.planter.isUsed(planter,it)){

                val output = Man10KitchenGarden.planter.isFinish(planter,it)

                if (output !=null){
                    inv.setItem(it,output)
                    return@forEach
                }

                inv.setItem(it,barrier)

                meta2.setDisplayName("§a§l完成予想時刻:${SimpleDateFormat("MM/dd kk:mm").format(Man10KitchenGarden.planter.getFinishTime(planter,it))}")
                panel2.itemMeta = meta2
            }
            inv.setItem(it-9,panel2)
        }

        p.openInventory(inv)
    }

    @EventHandler
    fun click(e:InventoryClickEvent){

//        if (e.view.title != "§b改良型プランター" && e.view.title != "§aプランター") return

        val p = e.whoClicked

        if (p !is Player)return

        if (e.clickedInventory == p.inventory)return

        if (!slots.contains(e.slot)){
            e.isCancelled = true
            return
        }

        val input = e.cursor?:return

        if (recipe.getRecipe(input) !=null){

            val planterItem = multiBlock.getMultiBlock(multiBlock.openLocation[p]?:return)

            if (planterItem == null){
                p.closeInventory()
                return
            }

            if (!planter.set(planterItem,input,e.slot))e.isCancelled = true

            openPlanter(planterItem,p)

        }


    }

    @EventHandler
    fun close(e:InventoryCloseEvent){

        multiBlock.openLocation.remove(e.player)

    }

}