package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.HealAbilityPacket;

public class HealerGUI extends Screen {
    private final Player entity;

    private static final ResourceLocation texture = new ResourceLocation("reabista:textures/gui/ability_gui.png");

    public HealerGUI(Player entity) {
        super(Component.literal(entity.getName().getString() + " - Healer Ability"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.width / 2 - 88;
        int top = this.height / 2 - 83;

        // 上ボタン：自己回復
        this.addRenderableWidget(
                Button.builder(Component.literal("自己回復"), b -> {
                    NetworkHandler.sendToServer(new HealAbilityPacket(HealAbilityPacket.Type.SELF_HEAL));
                    Minecraft.getInstance().setScreen(null);
                }).bounds(left + 60, top + 34, 56, 20).build()
        );

        // 下ボタン：範囲回復
        this.addRenderableWidget(
                Button.builder(Component.literal("範囲回復"), b -> {
                    NetworkHandler.sendToServer(new HealAbilityPacket(HealAbilityPacket.Type.AREA_HEAL));
                    Minecraft.getInstance().setScreen(null);
                }).bounds(left + 60, top + 89, 56, 20).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int left = this.width / 2 - 88;
        int top = this.height / 2 - 83;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, left, top, 0, 0, 176, 166);
        RenderSystem.disableBlend();

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
