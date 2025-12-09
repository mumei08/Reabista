package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.network.AbilityChangePacket;
import kaede.reabista.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class AbilitySetGUI extends Screen {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("reabista:textures/gui/abilitysetitemgui.png");

    private final Player player;
    private int leftPos, topPos;
    private final int imageWidth = 270;
    private final int imageHeight = 185;

    public AbilitySetGUI(Player player) {
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
        //       ボタン登録一覧
        // ===============================

        addAbilityButton(1, x + 25, y + 20,  46, 20, "編集");
        addAbilityButton(2, x + 25, y + 55,  46, 20, "複製");
        addAbilityButton(3, x + 25, y + 90,  46, 20, "飛行");
        addAbilityButton(4, x + 25, y + 125, 67, 20, "テレポート");
        addAbilityButton(5, x + 88, y + 20,  67, 20, "大喰い");
        addAbilityButton(6, x + 88, y + 55,  51, 20, "硬化");

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