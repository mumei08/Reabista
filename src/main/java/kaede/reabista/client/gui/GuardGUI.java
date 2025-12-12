package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.GuardAbilityPacket;

public class GuardGUI extends Screen {
    private final Player entity;
    private final HashMap<String, Object> guistate = new HashMap<>();

    private Button buttonResistance;
    private Button buttonAttackBoost;

    private static final ResourceLocation texture = new ResourceLocation("reabista:textures/gui/ability_gui.png");

    public GuardGUI(Player entity) {
        super(Component.literal(entity.getName().getString() + " - Guard Ability"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.width / 2 - 88;
        int top = this.height / 2 - 83;


        // 上ボタン：耐性LV4
        buttonResistance = Button.builder(Component.literal("硬化"), b -> {
            NetworkHandler.sendToServer(new GuardAbilityPacket(GuardAbilityPacket.Type.RESISTANCE_LV4));
            Minecraft.getInstance().setScreen(null);
        }).bounds(left + 69, top + 34, 35, 20).build();
        guistate.put("button:resistance", buttonResistance);
        this.addRenderableWidget(buttonResistance);

        // 下ボタン：攻撃力上昇LV3
        buttonAttackBoost = Button.builder(Component.literal("拳を硬化"), b -> {
            NetworkHandler.sendToServer(new GuardAbilityPacket(GuardAbilityPacket.Type.ATTACK_LV3));
            Minecraft.getInstance().setScreen(null);
        }).bounds(left + 58, top + 89, 56, 20).build();
        guistate.put("button:attackBoost", buttonAttackBoost);
        this.addRenderableWidget(buttonAttackBoost);
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