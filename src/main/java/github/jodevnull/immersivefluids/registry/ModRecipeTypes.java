package github.jodevnull.immersivefluids.registry;

import github.jodevnull.immersivefluids.WaterPhysics;
import github.jodevnull.immersivefluids.recipe.FillRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, WaterPhysics.MODID);

    public static final Supplier<RecipeType<FillRecipe>> FILL = RECIPE_TYPES.register("pickup_water", () -> new RecipeType<>()
    {
        public String toString() {
            return WaterPhysics.MODID + ":" + "fill";
        }
    });
}
