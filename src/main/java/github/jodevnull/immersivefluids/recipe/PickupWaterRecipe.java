package github.jodevnull.immersivefluids.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.jodevnull.immersivefluids.registry.ModRecipeSerializers;
import github.jodevnull.immersivefluids.registry.ModRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PickupWaterRecipe implements Recipe<PickupWaterRecipeInput>
{
    private final String group;
    private final int amount;
    private final Ingredient input;
    private final ItemStack output;

    public PickupWaterRecipe(String group, int amount, Ingredient input, ItemStack output) {
        this.group = group;
        this.amount = amount;
        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() {
        return this.input;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input);
        return nonnulllist;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public ItemStack assemble(PickupWaterRecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public boolean matches(PickupWaterRecipeInput recipeInput, Level level) {
        if (recipeInput.isEmpty())
            return false;

        return input.test(recipeInput.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PICKUP_WATER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PICKUP_WATER.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PickupWaterRecipe that = (PickupWaterRecipe) o;

        if (!getGroup().equals(that.getGroup())) return false;
        if (!input.equals(that.input)) return false;
        if (amount != that.getAmount()) return false;

        return this.output.equals(that.getOutput());
    }

    @Override
    public int hashCode() {
        int result = getGroup().hashCode();
        result = 31 * result + input.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + ((Object) amount).hashCode();
        return result;
    }

    @ParametersAreNonnullByDefault
    public static class Serializer implements RecipeSerializer<PickupWaterRecipe>
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, PickupWaterRecipe> STREAM_CODEC =
            StreamCodec.of(PickupWaterRecipe.Serializer::toNetwork, PickupWaterRecipe.Serializer::fromNetwork);

        public static final MapCodec<PickupWaterRecipe> CODEC =
            RecordCodecBuilder.mapCodec(
                inst -> inst.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(PickupWaterRecipe::getGroup),
                    Codec.INT.fieldOf("amount").forGetter(PickupWaterRecipe::getAmount),
                    Ingredient.MAP_CODEC_NONEMPTY.fieldOf("container").forGetter(PickupWaterRecipe::getInput),
                    ItemStack.CODEC.fieldOf("result").forGetter(PickupWaterRecipe::getOutput)
                ).apply(inst, PickupWaterRecipe::new)
            );

        public static PickupWaterRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            final var group = buffer.readUtf(32767);
            final var amount = buffer.readInt();
            final var input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            final var output = ItemStack.STREAM_CODEC.decode(buffer);

            return new PickupWaterRecipe(group, amount, input, output);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, PickupWaterRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeInt(recipe.amount);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
        }

        @Override
        public MapCodec<PickupWaterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PickupWaterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}