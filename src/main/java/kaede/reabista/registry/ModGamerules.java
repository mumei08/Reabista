package kaede.reabista.registry;

import net.minecraft.world.level.GameRules;

public class ModGamerules {

    public static GameRules.Key<GameRules.BooleanValue> MULTI_MODE;
    public static GameRules.Key<GameRules.BooleanValue> STORY_MODE;

    public static void register() {

        MULTI_MODE = GameRules.register(
                "multiMode",
                GameRules.Category.PLAYER,
                GameRules.BooleanValue.create(false)
        );

        STORY_MODE = GameRules.register(
                "storyMode",
                GameRules.Category.PLAYER,
                GameRules.BooleanValue.create(false)
        );
    }
}