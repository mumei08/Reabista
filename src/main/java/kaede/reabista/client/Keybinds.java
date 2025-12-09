package kaede.reabista.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "reabista", bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class Keybinds {

    public static final KeyMapping OPEN_ABILITY_KEY = new KeyMapping(
            "key.reabista.open_ability", // 翻訳用ID
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "key.categories.reabista"
    );

    public static final KeyMapping OPEN_STATUS_KEY = new KeyMapping(
            "key.reabista.open_status", // 翻訳用ID
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F4,
            "key.categories.reabista"
    );
    public static final KeyMapping ABILITY_CRYSTAL_KEY = new KeyMapping(
            "key.reabista.ability_crystal", // 翻訳用ID
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "key.categories.reabista"
    );
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_ABILITY_KEY);
        event.register(OPEN_STATUS_KEY);
        event.register(ABILITY_CRYSTAL_KEY);
    }
}