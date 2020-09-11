package red.man10.man10kitchengarden

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.plugin
import java.util.concurrent.ConcurrentHashMap


class Recipe {

    val recipes = ConcurrentHashMap<String,RecipeData>()

    fun setRecipe(name:String,input:ItemStack,output:ItemStack){

        val recipe = RecipeData()

        input.amount = 1
        output.amount = 1
        recipe.input = input
        recipe.output = output

        recipes[name] =  recipe

        GlobalScope.launch {

            plugin.reloadConfig()

            plugin.config.set("$name.input", plugin.itemToBase64(input))
            plugin.config.set("$name.output", plugin.itemToBase64(output))

            plugin.saveConfig()

        }
    }

    fun getRecipe(name:String):RecipeData?{
        return recipes[name]
    }

    fun getRecipe(input:ItemStack):String?{

        val clone = input.clone()
        clone.amount = 1

        for (recipe in recipes){
            if (recipe.value.input.toString() == clone.toString()){
                return recipe.key
            }
        }

        return null
    }

    fun load(){

        val keys = plugin.config.getKeys(false)

        for (key in keys){

            val recipe = RecipeData()

            recipe.input = plugin.itemFromBase64(plugin.config.getString("$key.input")?:continue)?:continue
            recipe.output = plugin.itemFromBase64(plugin.config.getString("$key.output")?:continue)?:continue

            recipes[key] = recipe
        }

    }


    class RecipeData{

        lateinit var input : ItemStack
        lateinit var output : ItemStack

    }
}