package github.jodevnull.immersivefluids.event;

import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.recipe.PickupWaterHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ModPlayerEvents
{
    public static void register() {}

    @SubscribeEvent
    public static void waterContainerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide().isClient())
            return;

        final var player = event.getEntity();
        final var hand = event.getHand();
        final var pos = event.getPos().relative(event.getHitVec().getDirection());
        final var state = event.getLevel().getBlockState(pos);

        if (player.getItemInHand(hand).isEmpty())
            return;

        if (CachedWater.world == null)
            return;

        if (player.getMainHandItem().is(Items.STICK)) {
            player.sendSystemMessage(Component.literal(state.toString()));
            player.sendSystemMessage(Component.literal(state.getFluidState().toString()));
            player.sendSystemMessage(Component.literal("is natural: %b".formatted(CachedWater.isNatural(pos))));
            player.sendSystemMessage(Component.literal("water level: %d".formatted(CachedWater.getWaterLevel(pos))));
        }

        if (!CachedWater.isWater(state) || CachedWater.isNatural(state))
            return;

        if (PickupWaterHandler.handle((ServerLevel) event.getLevel(), player, hand, pos)) {
            event.setCanceled(true);
        }
    }
}
