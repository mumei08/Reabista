package kaede.reabista.client.gui;

import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.ability.BinaryWritePacket;
import kaede.reabista.registry.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;

/**
 * 「0と1」能力: BinaryWriterGUI。
 * 0/1のみで構成されたビット列を入力し、UTF-8として8bit毎に復号した文字列を
 * アイテムID/エンティティIDとして解釈してサーバーに書き込み申請を送る。
 *
 * ID入力欄に文字列(例: "minecraft:diamond")を打つと、下のビット列欄へ
 * UTF-8バイト列→2進数を自動変換して反映する(自分で手計算しなくてよい)。
 * ビット列欄は直接編集も可能。
 */
public class BinaryWriterGUI extends Screen {
    private final Player entity;
    private EditBox idInput;
    private EditBox bitsInput;

    public BinaryWriterGUI(Player entity) {
        super(Component.literal("0と1 - ビット列書き込み"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int top = this.height / 2 - 55;

        // ID入力欄(例: minecraft:diamond) → 自動でビット列に変換
        this.idInput = new EditBox(this.font, centerX - 120, top, 240, 20, Component.literal("id"));
        this.idInput.setMaxLength(256);
        this.idInput.setResponder(this::onIdChanged);
        this.addRenderableWidget(this.idInput);

        // ビット列欄(自動入力される。直接編集も可能)
        this.bitsInput = new EditBox(this.font, centerX - 120, top + 45, 240, 20, Component.literal("bits"));
        this.bitsInput.setMaxLength(4096);
        this.bitsInput.setFilter(s -> s.chars().allMatch(c -> c == '0' || c == '1'));
        this.addRenderableWidget(this.bitsInput);

        this.setInitialFocus(this.idInput);

        this.addRenderableWidget(
                Button.builder(Component.literal("書き込む"), b -> {
                    String bits = this.bitsInput.getValue();
                    if (!bits.isEmpty()) {
                        NetworkHandler.sendToServer(new BinaryWritePacket(bits));
                    }
                }).bounds(centerX - 50, top + 75, 100, 20).build()
        );
    }

    /**
     * ID入力欄が変更される度に、UTF-8バイト列→2進数文字列へ変換してビット列欄へ反映する。
     */
    private void onIdChanged(String idText) {
        if (idText.isEmpty()) {
            this.bitsInput.setValue("");
            return;
        }
        byte[] bytes = idText.getBytes(StandardCharsets.UTF_8);
        StringBuilder bits = new StringBuilder(bytes.length * 8);
        for (byte b : bytes) {
            String bin = Integer.toBinaryString(b & 0xFF);
            bits.append("0".repeat(8 - bin.length())).append(bin); // 8bit固定長でゼロ埋め
        }
        this.bitsInput.setValue(bits.toString());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int top = this.height / 2 - 55;

        guiGraphics.drawCenteredString(this.font, "アイテムID/エンティティIDを入力(自動でビット化)", centerX, top - 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "↓ 自動変換されたビット列(直接編集も可)", centerX, top + 33, 0xAAAAAA);

        String bits = this.bitsInput.getValue();
        int zerosNeeded = (int) bits.chars().filter(c -> c == '0').count();
        int onesNeeded = (int) bits.chars().filter(c -> c == '1').count();
        int zeroHave = entity.getInventory().countItem(ModItems.ZERO_FRAGMENT.get());
        int oneHave = entity.getInventory().countItem(ModItems.ONE_FRAGMENT.get());

        boolean enough = zeroHave >= zerosNeeded && oneHave >= onesNeeded;
        int color = enough ? 0x55FF55 : 0xFF5555;
        guiGraphics.drawCenteredString(this.font,
                "必要: 0×" + zerosNeeded + " 1×" + onesNeeded
                        + "  (所持: 0×" + zeroHave + " 1×" + oneHave + ")",
                centerX, top + 100, color);

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
