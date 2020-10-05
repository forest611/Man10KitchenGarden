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
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.compressorRecipe
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.planterRecipe
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.titles
import red.man10.man10kitchengarden.Planter.getString
import red.man10.man10kitchengarden.Planter.planterName
import red.man10.man10kitchengarden.Planter.setString
import java.text.SimpleDateFormat

object Inventory:Listener{

    const val fuelSlot = 40
    val barrier = ItemStack(Material.BARRIER)

    val slots = listOf(10,13,16,37,43)

    init {
        val meta = barrier.itemMeta
        meta.setDisplayName("§c§l使用中")
        barrier.itemMeta = meta
    }

    fun createBaseMenu(title:String,p:Player,l: Location): Inventory {
        val inv = Bukkit.createInventory(p,54,title)

        val panel1 = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        for (i in 0..53){
            if (slots.contains(i))continue
            if (i == 40)continue
            inv.setItem(i,panel1)
        }

        val data = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        setLocation(l,data)
        inv.setItem(0,data)


        return inv
    }

    fun openPlanter(planter:ItemStack,p:Player,l: Location){

        val isEX = Planter.isEx(planter)

        val inv = createBaseMenu(if (isEX) Planter.planterEXName else planterName,p,l)

        val waterPanel = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        val wMeta = waterPanel.itemMeta
        wMeta.setDisplayName("§b水:${if (Planter.hasFuel(planter))"§aあり" else "§cなし" }")
        waterPanel.itemMeta = wMeta

        inv.setItem(fuelSlot-9,waterPanel)

        for (slot in slots){
            val panel2 = ItemStack(Material.LIME_STAINED_GLASS_PANE)
            val meta2 = panel2.itemMeta

            if (Planter.isUsed(planter,slot)){

                val output = Planter.isFinish(planter,slot)

                if (output !=null){
                    inv.setItem(slot,output)

                    meta2.setDisplayName("§a§l植える")
                    panel2.itemMeta = meta2

                    inv.setItem(slot-9,panel2)
                    continue
                }

                inv.setItem(slot,barrier)

                meta2.setDisplayName("§a§l完成予想時刻:${SimpleDateFormat("MM/dd kk:mm").format(Planter.getFinishTime(planter,slot))}")
                panel2.itemMeta = meta2
                inv.setItem(slot-9,panel2)
                continue
            }
            meta2.setDisplayName("§a§l植える")
            panel2.itemMeta = meta2

            inv.setItem(slot-9,panel2)

        }

        p.openInventory(inv)
    }

    fun openCompressor(compressor:ItemStack,p:Player,l:Location){
        val inv = createBaseMenu(Compressor.compressorName,p,l)

        val fuelPanel = ItemStack(Material.ORANGE_STAINED_GLASS_PANE)
        val fuelMeta = fuelPanel.itemMeta
        fuelMeta.setDisplayName("§6燃料:${if (Compressor.hasFuel(compressor))"§aあり" else "§cなし" }")
        fuelPanel.itemMeta = fuelMeta

        inv.setItem(fuelSlot-9,fuelPanel)


        for (slot in slots){
            val panel2 = ItemStack(Material.LIME_STAINED_GLASS_PANE)
            val meta2 = panel2.itemMeta

            if (Compressor.isUsed(compressor,slot)){

                val output = Compressor.isFinish(compressor,slot)

                if (output !=null){
                    inv.setItem(slot,output)

                    meta2.setDisplayName("§e投入")
                    panel2.itemMeta = meta2

                    inv.setItem(slot-9,panel2)
                    continue
                }

                inv.setItem(slot,barrier)

                meta2.setDisplayName("§a§l完成予想時刻:${SimpleDateFormat("MM/dd kk:mm").format(Compressor.getFinishTime(compressor,slot))}")
                panel2.itemMeta = meta2
                inv.setItem(slot-9,panel2)
                continue
            }
            meta2.setDisplayName("§e投入")
            panel2.itemMeta = meta2

            inv.setItem(slot-9,panel2)

        }

        p.openInventory(inv)
    }

    fun setRecipe(p:Player,name:String,item:String){

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
        setString(panel3,"name",name)
        setString(panel3,"item",item)


        inv.setItem(1,panel1)
        inv.setItem(2,panel1)
        inv.setItem(3,panel1)
        inv.setItem(4,panel3)
        inv.setItem(5,panel2)
        inv.setItem(6,panel2)
        inv.setItem(7,panel2)

        p.openInventory(inv)

    }

    fun clickPlanter(e:InventoryClickEvent,item: ItemStack,p:Player,location: Location){

        val inv = e.inventory

        if (e.slot == (fuelSlot-9)){
            val slot40 = inv.getItem(fuelSlot)?:return
            if (slot40.type == Material.WATER_BUCKET){
                Planter.setFuel(item)
                inv.removeItem(slot40)
                openPlanter(item,p,location)
            }
            return
        }

        if (!slots.contains(e.slot+9)) { return }

        val input = inv.getItem(e.slot+9)?:return

//        if (Recipe.getRecipe(input) ==null)return

        Planter.setRecipe(item,input.clone(),e.slot+9)

        openPlanter(item,p,location)

    }

    fun clickCompressor(e:InventoryClickEvent,item: ItemStack,p:Player,location: Location){
        val inv = e.inventory

        if (e.slot == (fuelSlot-9)){
            val slot40 = inv.getItem(fuelSlot)?:return
            if (slot40.type == Material.COAL){
                Compressor.setFuel(item)
                inv.removeItem(slot40)
                openCompressor(item,p,location)
            }
            return
        }

        if (!slots.contains(e.slot+9)) { return }

        val input = inv.getItem(e.slot+9)?:return

//        if (Recipe.getRecipe(input) ==null)return

        Compressor.setRecipe(item,input.clone(),e.slot+9)

        openCompressor(item,p,location)
    }

    @EventHandler
    fun click(e:InventoryClickEvent){

        if (!titles.contains(e.view.title)) return

        val p = e.whoClicked

        if (p !is Player)return

        if (e.clickedInventory == p.inventory)return

        if (slots.contains(e.slot) || e.slot == fuelSlot){
            val item = e.currentItem?:return
            if ((item.hasItemMeta()&&item.itemMeta.displayName == "§c§l使用中")){
                e.isCancelled = true
            }
            return
        }

        //アマスタのロケーションを読み込み
        val location = getLocation(e.inventory.getItem(0)!!)

        val item = MultiBlock.getMultiBlock(location)

        e.isCancelled = true

        if (item == null){
            p.closeInventory()
            return
        }

        val name = getString(item,"name")

        if (name ==null){
            p.closeInventory()
            return
        }

        if (name == "planter" || name == "planterEX"){
            clickPlanter(e,item,p,location)
            return
        }

        if (name == "compressor"){
            clickCompressor(e,item,p,location)
            return
        }


        return

    }

    @EventHandler
    fun close(e:InventoryCloseEvent){

        if (e.view.title == "SetRecipe"){

            val inv = e.inventory

            val input = inv.getItem(0)?:return
            val output = inv.getItem(8)?:return

            val name = getString(inv.getItem(4)!!,"name")!!

            when(getString(inv.getItem(4)!!,"item")!!){
                "planter" -> planterRecipe.setRecipe(name,input,output)
                "compressor" -> compressorRecipe.setRecipe(name,input,output)
            }

            e.player.sendMessage("§a§l設定完了！")
            return
        }
    }

    fun setLocation(loc:Location,item:ItemStack){

        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin,"world"), PersistentDataType.STRING,loc.world.name)
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin,"locX"), PersistentDataType.DOUBLE,loc.x)
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin,"locY"), PersistentDataType.DOUBLE,loc.y)
        meta.persistentDataContainer.set(NamespacedKey(Man10KitchenGarden.plugin,"locZ"), PersistentDataType.DOUBLE,loc.z)

        item.itemMeta = meta

    }

    fun getLocation(item:ItemStack):Location{

        val meta = item.itemMeta
        val world = meta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin,"world"), PersistentDataType.STRING]!!
        val x = meta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin,"locX"), PersistentDataType.DOUBLE]?:0.0
        val y = meta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin,"locY"), PersistentDataType.DOUBLE]?:0.0
        val z = meta.persistentDataContainer[NamespacedKey(Man10KitchenGarden.plugin,"locZ"), PersistentDataType.DOUBLE]?:0.0

        return Location(Bukkit.getWorld(world),x,y,z)
    }
}