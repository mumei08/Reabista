package kaede.reabista.capabilities;

import net.minecraft.world.entity.player.Player;

public class AbilityDataAPI {

    public static IAbilityData get(Player player) {
        return player.getCapability(AbilityProvider.ABILITY)
                .orElseThrow(() -> new IllegalStateException("Ability Capability not found!"));
    }
}