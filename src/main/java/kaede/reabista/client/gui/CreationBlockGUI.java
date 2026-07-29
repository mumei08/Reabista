package kaede.reabista.client.gui;

import kaede.reabista.client.util.NameResolver;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.CreationPlacePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * 創造能力(結晶化, CreationCrystal所持時に発動): ブロックのja/en名を入力すると、
 * GUIを開いた瞬間に照準していたブロックの上にそのブロックを生成する。
 */
public class CreationBlockGUI extends Screen {
    private final Player entity;
    private final BlockPos targetPos; // GUIを開いた時点で照準していたブロック(null=対象なし)
    private EditBox nameInput;
    private String feedback = "";

    public CreationBlockGUI(Player entity, BlockHitResult hit) {
        super(Component.literal("創造(結晶化) - ブロック生成"));
        this.entity = entity;
        this.targetPos = hit != null ? hit.getBlockPos() : null;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int top = this.height / 2 - 20;

        this.nameInput = new EditBox(this.font, centerX - 120, top, 240, 20, Component.literal("name"));
        this.nameInput.setMaxLength(128);
        this.addRenderableWidget(this.nameInput);
        this.setInitialFocus(this.nameInput);

        this.addRenderableWidget(
                Button.builder(Component.literal("生成"), b -> tryCreate())
                        .bounds(centerX - 50, top + 30, 100, 20).build()
        );

        if (targetPos == null) {
            this.feedback = "§cブロックに照準を合わせた状態で発動してください";
        }
    }

    private void tryCreate() {
        if (targetPos == null) return;
        String query = this.nameInput.getValue();
        if (query.isBlank()) return;

        Optional<ResourceLocation> resolved = NameResolver.resolveBlock(query);
        if (resolved.isEmpty()) {
            this.feedback = "§c「" + query + "」に一致するブロックが見つかりません";
            return;
        }

        NetworkHandler.sendToServer(new CreationPlacePacket(resolved.get().toString(),
                targetPos.getX(), targetPos.getY(), targetPos.getZ()));
        this.minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int centerX = this.width / 2;
        int top = this.height / 2 - 20;

        guiGraphics.drawCenteredString(this.font, "ブロック名(日本語/英語)を入力", centerX, top - 15, 0xFFFFFF);
        if (!feedback.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, feedback, centerX, top + 55, 0xFF5555);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { this.minecraft.setScreen(null); return true; }
        if (key == 257 || key == 335) { tryCreate(); return true; } // Enter
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
