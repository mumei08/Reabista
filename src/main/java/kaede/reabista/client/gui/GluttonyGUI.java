package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class GluttonyGUI extends Screen {

    private final Player entity;
    private final HashMap<String, Object> guistate = new HashMap<>();
    private static final ResourceLocation TEXTURE = new ResourceLocation("reabista:textures/gui/ability_gui.png");


    public GluttonyGUI(Player entity) {
        super(Component.literal("大喰い能力"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        // ボタンなしなのでここでは何も追加しない
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);
        int left = this.width / 2 - 88; // 176/2
        int top = this.height / 2 - 83; // 166/2
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int left = this.width / 2 - 88;
        int top = this.height / 2 - 83;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, left, top, 0, 0, 176, 166);
        RenderSystem.disableBlend();

        guiGraphics.drawString(this.font, Component.literal("食べた数"), left + 15, top + 16, -12829636, false);
        int gluttonyFood = (int) entity.getAttribute(ModAttributes.GLUTTONY_FOOD.get()).getValue();
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(gluttonyFood)), left + 15, top + 34, -12829636, false);

        guiGraphics.drawString(this.font, Component.literal("喰った数"), left + 15, top + 88, -12829636, false);
        int gluttonyEntity = (int) entity.getAttribute(ModAttributes.GLUTTONY_ENTITY.get()).getValue();
        guiGraphics.drawString(this.font, Component.literal(String.valueOf(gluttonyEntity)), left + 15, top + 115, -12829636, false);
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
