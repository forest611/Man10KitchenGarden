package red.man10.man10kitchengarden

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.getData
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.multiBlock
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planter
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.recipe
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.setData
import java.text.SimpleDateFormat

class Inventory:Listener{

    val slots = listOf(10,13,16,37,43)
    val waterSlot = 40

    fun openPlanter(planter:ItemStack,p:Player,l: Location){

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

        val data = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        setData(data,"location","${l.x};${l.y};${l.z}")

        inv.setItem(0,data)

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

    fun setRecipe(p:Player,name:String){

        val inv = Bukkit.createInventory(null,9,"SetRecipe")

        val panel1 = ItemStack(Material.RED_STAINED_GLASS_PANE)

        val meta1 = panel1.itemMeta
        meta1.setDisplayName("§3§l←材料")
        panel1.itemMeta = meta1

        val panel2 = ItemStack(Material.RED_STAINED_GLASS_PANE)

        val meta2 = panel2.itemMeta
        meta2.setDisplayName("§3§l完成品→")
        panel2.itemMeta = meta2

        val panel3 = ItemStack(Material.LIME_STAINED_GLASS_PANE)
        val meta3 = panel3.itemMeta
        meta3.setDisplayName("§a§l閉じてセーブ")
        panel3.itemMeta = meta3
        setData(panel3,"name",name)


        inv.setItem(1,panel1)
        inv.setItem(2,panel1)
        inv.setItem(3,panel1)
        inv.setItem(4,panel3)
        inv.setItem(5,panel2)
        inv.setItem(6,panel2)
        inv.setItem(7,panel2)

        p.openInventory(inv)

    }

    @EventHandler
    fun click(e:InventoryClickEvent){

        if (e.view.title != "§b改良型プランター" && e.view.title != "§aプランター") return

        val p = e.whoClicked

        if (p !is Player)return

        if (e.clickedInventory == p.inventory)return

        if (!slots.contains(e.slot)){
            e.isCancelled = true
            return
        }

        val input = e.cursor?:return

        if (recipe.getRecipe(input) !=null){

            val pos = getData(e.inventory.getItem(0)!!,"location")!!.split(";")
            val location = Location(p.world,pos[0].toDouble(),pos[1].toDouble(),pos[2].toDouble())

            val planterItem = multiBlock.getMultiBlock(location)

            if (planterItem == null){
                p.closeInventory()
                return
            }

            if (!planter.set(planterItem,input,e.slot))e.isCancelled = true

            openPlanter(planterItem,p,location)

            return

        }

    }

    @EventHandler
    fun close(e:InventoryCloseEvent){

        if (e.view.title == "SetRecipe"){

            val inv = e.inventory

            val input = inv.getItem(0)?:return
            val output = inv.getItem(8)?:return

            val name = getData(inv.getItem(4)!!,"name")!!

            recipe.setRecipe(name,input,output)

            e.player.sendMessage("§a§l設定完了！")
            return
        }
    }

}