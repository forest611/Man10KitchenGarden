package red.man10.man10kitchengarden

import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class Recipe {

    val recipes = ConcurrentHashMap<String,RecipeData>()

    fun setRecipe(name:String,input:ItemStack,output:ItemStack,time:Int){

        val recipe = RecipeData()

        input.amount = 1
        output.amount = 1
        recipe.input = input
        recipe.output = output
//        recipe.time = time

        recipes[name] =  recipe
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

    }


    class RecipeData{

        lateinit var input : ItemStack
        lateinit var output : ItemStack

//        var time = 0//一つ作るのにかかる時間(min)


    }
}