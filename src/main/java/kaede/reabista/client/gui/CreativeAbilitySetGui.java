package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.network.ability.gui.AbilityChangePacket;
import kaede.reabista.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CreativeAbilitySetGui extends Screen {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("reabista:textures/gui/abilitysetitemgui.png");

    private final Player player;
    private int leftPos, topPos;
    private final int imageWidth = 320;
    private final int imageHeight = 220;

    public CreativeAbilitySetGui(Player player) {
        super(Component.literal("Ability Selector"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = this.width / 2 - imageWidth / 2;
        this.topPos = this.height / 2 - imageHeight / 2;

        int x = leftPos;
        int y = topPos;

        // ===============================
        //   ボタン登録一覧(1〜15、4列グリッド)
        // ===============================
        String[] labels = {
                "編集", "複製", "飛行", "テレポート",
                "大喰い", "硬化", "ヒーラー", "雷",
                "筋力増強", "分身", "煙幕", "0と1",
                "異消", "創造", "破壊"
        };

        int colW = 70, rowH = 24;
        int startX = x + 20, startY = y + 20;

        for (int i = 0; i < labels.length; i++) {
            int abilityId = i + 1; // 1〜15
            int col = i % 4;
            int row = i / 4;
            addAbilityButton(abilityId, startX + col * colW, startY + row * rowH, 64, 20, labels[i]);
        }
    }

    private void addAbilityButton(
            int abilityId,
            int x, int y,
            int w, int h,
            String label
    ) {
        this.addRenderableWidget(
                Button.builder(Component.literal(label), b -> {
                    NetworkHandler.sendToServer(new AbilityChangePacket(abilityId));
                    Minecraft.getInstance().setScreen(null);
                }).bounds(x, y, w, h).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int keyCode, int scan, int modifiers) {
        if (keyCode == 256) { // ESC
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scan, modifiers);
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }
}