package kaede.reabista.gamerules;

import kaede.reabista.registry.ModGamerules;
import net.minecraft.server.level.ServerLevel;

public class ModeHandler {

    public static boolean isStoryMode(ServerLevel level) {
        return level.getGameRules().getBoolean(ModGamerules.STORY_MODE);
    }

    public static boolean isMultiMode(ServerLevel level) {
        return level.getGameRules().getBoolean(ModGamerules.MULTI_MODE);
    }

    public static boolean isAnyModeEnabled(ServerLevel level) {
        return isStoryMode(level) || isMultiMode(level);
    }
}
