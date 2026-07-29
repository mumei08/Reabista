package kaede.reabista.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.NonNullConsumer;

public class AbilityDataAPI {

    public static IAbilityData get(Player player) {
        return player.getCapability(AbilityProvider.ABILITY)
                .orElse(null);
    }

    public static void ifPresent(Player player, NonNullConsumer<IAbilityData> consumer) {
        player.getCapability(AbilityProvider.ABILITY)
                .ifPresent(consumer);
    }
}