package github.jodevnull.immersivefluids.registry;

import github.jodevnull.immersivefluids.WaterPhysics;
import github.jodevnull.immersivefluids.recipe.PickupWaterRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, WaterPhysics.MODID);
    public static final Supplier<RecipeSerializer<?>> PICKUP_WATER = RECIPE_SERIALIZERS.register("pickup_water", PickupWaterRecipe.Serializer::new);
}
