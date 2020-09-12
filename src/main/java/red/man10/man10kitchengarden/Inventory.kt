package red.man10.man10kitchengarden

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.getData
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.multiBlock
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planter
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.plugin
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.recipe
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.setData
import java.text.SimpleDateFormat

class Inventory:Listener{

    companion object{
        val slots = listOf(10,13,16,37,43)
    }
    val waterSlot = 40

    val barrier = ItemStack(Material.BARRIER)

    init {
        val meta = barrier.itemMeta
        meta.setDisplayName("§c§l使用中")
        barrier.itemMeta = meta
    }

    fun openPlanter(planter:ItemStack,p:Player,l: Location,inventory: Inventory?){

        val isEX = Man10KitchenGarden.planter.isEx(planter)

        val isWater = Man10KitchenGarden.planter.isWater(planter)

        val inv = inventory
                ?: if(isEX){
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

        val data = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        setData(data,"location","${l.x};${l.y};${l.z}")

        inv.setItem(0,data)

        val waterPanel = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        val wMeta = waterPanel.itemMeta
        wMeta.setDisplayName("§b水:${if (isWater)"§bあり" else "§cなし" }")
        waterPanel.itemMeta = wMeta

        inv.setItem(waterSlot-9,waterPanel)

        for (slot in slots){
            val panel2 = ItemStack(Material.LIME_STAINED_GLASS_PANE)
            val meta2 = panel2.itemMeta

            if (Man10KitchenGarden.planter.isUsed(planter,slot)){

                val output = Man10KitchenGarden.planter.isFinish(planter,slot)

                if (output !=null){
                    inv.setItem(slot,output)

                    meta2.setDisplayName("§a§l植える")
                    panel2.itemMeta = meta2

                    inv.setItem(slot-9,panel2)
                    continue
                }

                inv.setItem(slot,barrier)

                meta2.setDisplayName("§a§l完成予想時刻:${SimpleDateFormat("MM/dd kk:mm").format(Man10KitchenGarden.planter.getFinishTime(planter,slot))}")
                panel2.itemMeta = meta2
                inv.setItem(slot-9,panel2)
                continue
            }
            meta2.setDisplayName("§a§l植える")
            panel2.itemMeta = meta2

            inv.setItem(slot-9,panel2)

        }

        if (inventory==null){
            p.openInventory(inv)
        }
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

        if (slots.contains(e.slot) || e.slot == waterSlot)return

        //アマスタのロケーションを読み込み
        val pos = getData(e.inventory.getItem(0)!!,"location")!!.split(";")
        val location = Location(p.world,pos[0].toDouble(),pos[1].toDouble(),pos[2].toDouble())

        val planterItem = multiBlock.getMultiBlock(location)

        val inv = e.inventory

        e.isCancelled = true

        if (planterItem == null){
            p.closeInventory()
            return
        }

        if (e.slot == (waterSlot-9)){
            val slot40 = inv.getItem(waterSlot)?:return
            if (slot40.type == Material.WATER_BUCKET){
                planter.setWater(planterItem)
                inv.removeItem(slot40)
                openPlanter(planterItem,p,location,inv)
            }
            return
        }

        if (!slots.contains(e.slot+9)) { return }

        val input = inv.getItem(e.slot+9)?:return

        if (recipe.getRecipe(input) ==null)return

        planter.set(planterItem,input.clone(),e.slot+9)

        openPlanter(planterItem,p,location,inv)

        return

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