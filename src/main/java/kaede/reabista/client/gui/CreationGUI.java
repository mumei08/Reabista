package kaede.reabista.client.gui;

import kaede.reabista.client.util.NameResolver;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.CreationGetPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * 創造能力(基本, 結晶を持たずに発動): アイテムのja/en名を入力するとそのアイテムを1個入手できる。
 */
public class CreationGUI extends Screen {
    private final Player entity;
    private EditBox nameInput;
    private String feedback = "";

    public CreationGUI(Player entity) {
        super(Component.literal("創造 - アイテム生成"));
        this.entity = entity;
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
    }

    private void tryCreate() {
        String query = this.nameInput.getValue();
        if (query.isBlank()) return;

        Optional<ResourceLocation> resolved = NameResolver.resolveItem(query);
        if (resolved.isEmpty()) {
            this.feedback = "§c「" + query + "」に一致するアイテムが見つかりません";
            return;
        }

        NetworkHandler.sendToServer(new CreationGetPacket(resolved.get().toString()));
        this.minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int centerX = this.width / 2;
        int top = this.height / 2 - 20;

        guiGraphics.drawCenteredString(this.font, "アイテム名(日本語/英語)を入力", centerX, top - 15, 0xFFFFFF);
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
