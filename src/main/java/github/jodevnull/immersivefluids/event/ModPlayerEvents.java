package github.jodevnull.immersivefluids.event;

import github.jodevnull.immersivefluids.features.CachedWater;
import github.jodevnull.immersivefluids.recipe.PickupWaterHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
        if (event.getLevel().isClientSide())
            return;

        final var player = event.getEntity();
        final var hand = event.getHand();
        final var pos = event.getPos().relative(event.getHitVec().getDirection());
        final var state = event.getLevel().getBlockState(pos);

        if (!CachedWater.isWater(state) || CachedWater.isNatural(state))
            return;

        if (CachedWater.world == null)
            return;

        if (PickupWaterHandler.handle((ServerLevel) event.getLevel(), player, hand, pos)) {
            event.setCanceled(true);
        }
    }
}
