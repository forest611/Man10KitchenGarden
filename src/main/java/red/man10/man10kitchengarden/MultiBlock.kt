package red.man10.man10kitchengarden

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.getData

class MultiBlock :Listener{

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

        val entities = location.world.getNearbyEntities(location,1.5,1.5,1.5)

        for (entity in entities){
            if (entity.type != EntityType.ARMOR_STAND)continue
            if (entity !is ArmorStand)continue
            return entity
        }

        return null

    }

    fun setMultiBlock(center:Location, item:ItemStack):Boolean{

        val cube = getCube(center)

        cube.forEach { if (it.block.type != Material.AIR)return false }
        cube.forEach { it.block.type = Material.AIR }

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

        val armor = center.world.spawn(loc,ArmorStand :: class.java)
        armor.setGravity(false)
        armor.canPickupItems = false
        armor.isVisible = false
        armor.setItem(EquipmentSlot.HEAD,item)

        return true
    }

    fun breakMultiBlock(location:Location):ItemStack?{

        val stand = getArmorStand(location)?:return null

        val item = stand.getItem(EquipmentSlot.HEAD)

        if (!isMultiBlock(item))return null

        getCube(stand.location.block.location).forEach { it.block.type = Material.AIR }

        stand.remove()

        return item
    }

    fun isMultiBlock(item:ItemStack):Boolean{
        if (getData(item,"name") !=null)return true
        return false
    }

    @EventHandler
    fun interactEvent(e:PlayerInteractEvent){

        if (e.action != Action.RIGHT_CLICK_BLOCK)return

        val p = e.player

        //バリアブロックをクリックした場合
        if (e.clickedBlock!!.type == Material.BARRIER){

        }


    }

}