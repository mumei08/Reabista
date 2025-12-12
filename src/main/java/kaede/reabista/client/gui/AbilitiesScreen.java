package kaede.reabista.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Minecraft;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.SetAbilityPacket;
import kaede.reabista.registry.ModAttributes;

public class AbilitiesScreen extends Screen {

    public AbilitiesScreen() {
        super(Component.literal("Abilities"));
    }

    @Override
    protected void init() {
        int x = width / 2 - 80;
        int y = height / 6;
        Minecraft mc = Minecraft.getInstance();

        int ability = (int) mc.player.getAttributeValue(ModAttributes.ABILITY.get());

        switch (ability) {
            case 1 -> mc.setScreen(new EditGUI(mc.player));
            case 2 -> mc.setScreen(new CopyGUI(mc.player));
            case 5 -> mc.setScreen(new GluttonyGUI(mc.player));
            case 6 -> mc.setScreen(new GuardGUI(mc.player));
        }
        addRenderableWidget(Button.builder(
                Component.literal("Ability: " + ability),
                b -> NetworkHandler.CHANNEL.sendToServer(new SetAbilityPacket((ability + 1) % 7))
        ).bounds(x, y, 160, 20).build());
    }
}