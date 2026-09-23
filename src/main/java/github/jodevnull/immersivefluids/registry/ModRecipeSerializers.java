package github.jodevnull.immersivefluids.registry;

import github.jodevnull.immersivefluids.WaterPhysics;
import github.jodevnull.immersivefluids.recipe.FillRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, WaterPhysics.MODID);
    public static final Supplier<RecipeSerializer<?>> FILL = RECIPE_SERIALIZERS.register("fill", FillRecipe.Serializer::new);
}
