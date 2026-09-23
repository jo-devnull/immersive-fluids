package github.jodevnull.immersivefluids.recipe;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.jodevnull.immersivefluids.WaterPhysics;
import github.jodevnull.immersivefluids.registry.ModRecipeSerializers;
import github.jodevnull.immersivefluids.registry.ModRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
public class FillRecipe implements Recipe<SimpleContainer>
{
    private final ResourceLocation id;
    private final String group;
    private final int amount;
    private final Ingredient input;
    private final ItemStack output;

    public FillRecipe(ResourceLocation id, String group, int amount, Ingredient input, ItemStack output) {
        this.id = id;
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
    public ResourceLocation getId() {
        return this.id;
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
        return this.output.copy();
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public ItemStack assemble(@NotNull SimpleContainer inv, @NotNull RegistryAccess access) {
        return this.output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess access) {
        return output;
    }

    @Override
    public boolean matches(SimpleContainer inv, @NotNull Level level) {
        if (inv.isEmpty())
            return false;

        return input.test(inv.getItem(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FILL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FILL.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillRecipe that = (FillRecipe) o;

        if (!getId().equals(that.getId())) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (!input.equals(that.input)) return false;
        if (amount != that.getAmount()) return false;

        return this.output.equals(that.getOutput());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getGroup().hashCode();
        result = 31 * result + input.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + ((Object) amount).hashCode();
        return result;
    }

    @ParametersAreNonnullByDefault
    public static class Serializer implements RecipeSerializer<FillRecipe>
    {
        public Serializer() {}

        @Override
        public FillRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final var group = GsonHelper.getAsString(json, "group", "");
            final var amount = GsonHelper.getAsInt(json, "amount");
            final var container = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "container"));
            final var result = readOutput(GsonHelper.getAsJsonObject(json, "result"));

            if (container.isEmpty())
                throw new JsonParseException("No ingredients for pickup water recipe");

            if (result.isEmpty())
                throw new JsonParseException("No output for pickup water recipe");

            return new FillRecipe(recipeId, group, amount, container, result);
        }

        @Nullable
        @Override
        public FillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf(32767);
            int amount = buffer.readInt();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new FillRecipe(recipeId, group, amount, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FillRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeInt(recipe.amount);
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }

        private static ItemStack readOutput(JsonElement element) {
            if (!element.isJsonObject())
                throw new JsonSyntaxException("Must be a json object");

            final var json = element.getAsJsonObject();
            final var itemId = GsonHelper.getAsString(json, "item");

            final ItemStack itemstack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId))), 1);

            if (json.has("nbt")) {
                try {
                    final var nbt = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(
                        nbt.isJsonObject() ? WaterPhysics.GSON.toJson(nbt) : GsonHelper.convertToString(nbt, "nbt")));
                }
                catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }

            return itemstack;
        }
    }
}