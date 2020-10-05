package red.man10.man10kitchengarden

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import red.man10.man10kitchengarden.Man10KitchenGarden.Companion.plugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap


class Recipe(private val itemName:String) {

    val recipes = ConcurrentHashMap<String,RecipeData>()

    fun setRecipe(name:String,input:ItemStack,output:ItemStack){

        val recipe = RecipeData()

        val file = File(plugin.dataFolder.path+"/$itemName.yml")

        val config = YamlConfiguration.loadConfiguration(file)

        input.amount = 1
        output.amount = 1
        recipe.input = input
        recipe.output = output

        recipes[name] =  recipe

        Thread{

            config.set("$name.input", plugin.itemToBase64(input))
            config.set("$name.output", plugin.itemToBase64(output))

            config.save(file)

        }.start()
    }

    fun getRecipe(name:String):RecipeData?{
        return recipes[name]
    }

    fun getRecipe(input:ItemStack):String?{

//        val clone = input.clone()
//        clone.amount = 1
//
//        for (recipe in recipes){
//            if (recipe.value.input.isSimilar(input)){
//                return recipe.key
//            }
//        }


        return recipes.filter { it.value.input.isSimilar(input) }.keys.toString()
    }

    fun load(){

        val file = File(plugin.dataFolder.path+"/$itemName.yml")

        val config = YamlConfiguration.loadConfiguration(file)

        val keys = config.getKeys(false)

        for (key in keys){

            val recipe = RecipeData()

            recipe.input = plugin.itemFromBase64(config.getString("$key.input")?:continue)?:continue
            recipe.output = plugin.itemFromBase64(config.getString("$key.output")?:continue)?:continue

            recipes[key] = recipe
        }

    }


    class RecipeData{

        lateinit var input : ItemStack
        lateinit var output : ItemStack

    }
}