package kaede.reabista.gamerules;

import kaede.reabista.events.geterAbility;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AbilityDistributor {

    public static void distributeOnJoin(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (getAbility(player) == -1) {

            boolean story = ModeHandler.isStoryMode(level);
            boolean multi = ModeHandler.isMultiMode(level);

            if (!story && !multi) return;

            if (story) {
                handleStoryMode(level, player);
            } else if (multi) {
                handleMultiMode(level, player);
            }
        }
    }

    /* -------------------- マルチモード -------------------- */

    private static void handleMultiMode(ServerLevel level, ServerPlayer player) {

        boolean abilityOneExists = level.players().stream()
                .anyMatch(p -> getAbility(p) == 0);

        if (!abilityOneExists) {
            setAbility(player, 0);
            return;
        }

        int[] pool = {0,3,4,5,6,7,8,9,10,11,13};
        int ability = pool[level.random.nextInt(pool.length)];

        setAbility(player, ability);
        geterAbility.getterAbility(level, player);

    }

    /* -------------------- ストーリーモード -------------------- */

    private static void handleStoryMode(ServerLevel level, ServerPlayer player) {
        setAbility(player, 0);
        geterAbility.getterAbility(level, player);
    }

    /* -------------------- 共通 -------------------- */

    private static int getAbility(ServerPlayer player) {

        if (player.getAttribute(ModAttributes.ABILITY.get()) == null) {
            return -2;
        }

        return (int) player.getAttribute(ModAttributes.ABILITY.get()).getValue();
    }

    private static void setAbility(ServerPlayer player, int value) {

        if (player.getAttribute(ModAttributes.ABILITY.get()) == null) {
            return;
        }

        player.getAttribute(ModAttributes.ABILITY.get()).setBaseValue(value);
    }
}
