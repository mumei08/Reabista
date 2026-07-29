package kaede.reabista.events;

import kaede.reabista.Reabista;
import kaede.reabista.capabilities.AbilityDataAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reabista.MODID)
public class FlyAbilitiesEvent {

    public static boolean isFlyEnabled(LivingEntity living) {
        if (living instanceof Player player) {
            return AbilityDataAPI.get(player).isFlyEnabled();
        }
        return false;
    }

    public static void clutchFly(LivingEntity living) {
        if (!(living instanceof Player player)) return;

        var data = AbilityDataAPI.get(player);
        data.setFlyEnabled(!data.isFlyEnabled());
        if (data.isFlyEnabled()){
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }else if (!data.isFlyEnabled()){
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
