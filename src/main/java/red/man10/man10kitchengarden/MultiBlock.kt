package red.man10.man10kitchengarden

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import red.man10.realestate.RealEstateAPI
import red.man10.realestate.region.User.Permission.*

object MultiBlock :Listener{


    fun getCube(centerBlock:Location):List<Location>{

        val list = mutableListOf<Location>()

        val fx = centerBlock.blockX-1
        val fy = centerBlock.blockY-1
        val fz = centerBlock.blockZ-1

        for (x in fx until fx+3){
            for (y in fy until fy+3){
                for (z in fz until fz+3){

                    list.add(Location(centerBlock.world,x.toDouble(),y.toDouble(),z.toDouble()))

                }
            }
        }

        return list

    }

    fun getArmorStand(location: Location):ArmorStand?{

        val fixedLoc = location.clone()
        fixedLoc.x += 0.5
        fixedLoc.z += 0.5

        val entities = location.world.getNearbyEntities(fixedLoc,1.5,1.5,1.5)

        for (entity in entities){
            if (entity.type != EntityType.ARMOR_STAND)continue
            if (entity !is ArmorStand)continue
            return entity
        }

        return null

    }

    fun setMultiBlock(center:Location, item:ItemStack):Boolean{

        val cube = getCube(center)

        cube.forEach {
            //if (!realEstateAPI.hasPermission(p,it,User.Companion.Permission.ALL))return false
            if (it.block.type != Material.AIR)return false
        }
        cube.forEach { it.block.type = Material.BARRIER }

        item.amount = 1

        val loc = center.clone()

        var yaw: Float = loc.yaw

        if (yaw < 0) {
            yaw += 360f
        }
        if (yaw >= 315 || yaw < 45) {
            loc.yaw = 0.0F
        } else if (yaw < 135) {
            loc.yaw = 90F
        } else if (yaw < 225) {
            loc.yaw = 180F
        } else if (yaw < 315) {
            loc.yaw = -90F
        }

        loc.x+=0.5
        loc.z+=0.5
        loc.y -= 1

        val armor = center.world.spawn(loc,ArmorStand :: class.java)
        armor.setGravity(false)
        armor.canPickupItems = false
        armor.isVisible = false
        armor.setItem(EquipmentSlot.HEAD,item)

        center.world.playSound(center,Sound.BLOCK_ANVIL_PLACE,1.0F,1.0F)

        return true
    }

    fun getMultiBlock(loc:Location):ItemStack?{

        val stand = getArmorStand(loc)?:return null

        val item = stand.getItem(EquipmentSlot.HEAD)

        if (isMultiBlock(item))return item

        return null

    }

    fun breakMultiBlock(location:Location):ItemStack?{

        val stand = getArmorStand(location)?:return null

        val item = stand.getItem(EquipmentSlot.HEAD)

        if (!isMultiBlock(item))return null

        val center = stand.location.clone()

        center.y ++

        getCube(center).forEach { it.block.type = Material.AIR }

        stand.remove()

        location.world.playSound(location,Sound.BLOCK_STONE_BREAK,1.0F,1.0F)

        return item
    }

    fun isMultiBlock(item:ItemStack):Boolean{
        if (Planter.getString(item,"name") !=null)return true
        return false
    }

    @EventHandler
    fun interactEvent(e:PlayerInteractEvent){

        val p = e.player

        if (p.world.name != "builder")return

        when(e.action){

            Action.RIGHT_CLICK_BLOCK->{

                //バリアブロックをクリックした場合
                if (e.clickedBlock!!.type == Material.BARRIER && !p.isSneaking){

                    if(e.hand != EquipmentSlot.HAND)return

                    val clickedLocation = e.clickedBlock!!.location.clone()

                    if (!RealEstateAPI.hasPermission(p,clickedLocation, INVENTORY))return

                    val item = getMultiBlock(clickedLocation)

                    if (item!=null){

                        val name = Planter.getString(item, "name")
                        if (name == "planter" || name == "planterEX"){
                            Inventory.openPlanter(item,p,clickedLocation)

                        }else if (name == Compressor.compressorID){
                            Inventory.openCompressor(item,p,clickedLocation)
                        }

                        e.isCancelled = true
                        return
                    }
                }else{

                    val item = e.item?:return

                    val clicked = e.clickedBlock?:return

                    if (e.blockFace != BlockFace.UP)return

                    if (e.hasItem() && isMultiBlock(item)){

                        val location = clicked.location.clone()

                        if (!RealEstateAPI.hasPermission(p,location, BLOCK))return

                        location.y +=2.0
                        location.yaw = p.location.yaw

                        if (setMultiBlock(location,item.clone())){
                            item.amount --
                        }
                        e.isCancelled = true

                        return
                    }
                    return
                }
            }

            Action.LEFT_CLICK_BLOCK->{

                if (e.clickedBlock!!.type == Material.BARRIER){

                    val location = e.clickedBlock!!.location

                    if (!RealEstateAPI.hasPermission(p,location, BLOCK))return

                    val wrench = p.inventory.itemInMainHand

                    if (!wrench.hasItemMeta()||wrench.itemMeta.displayName != "§lレンチ" ){
                        p.sendMessage("§c§lレンチを持ってください！")
                        return
                    }

                    val item = breakMultiBlock(location)?:return

                    val name = Planter.getString(item, "name")

                    if (name == "planter" || name == "planterEX"){

                        if (Planter.isEx(item)){
                            p.inventory.addItem(Planter.getPlanterEx())
                        }

                    }else if (name == Compressor.compressorID){
                        p.inventory.addItem(Compressor.compressorItem)
                    }


                    e.isCancelled = true
                }
            }

            else -> return
        }
    }

}