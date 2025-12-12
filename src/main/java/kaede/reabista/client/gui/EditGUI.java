package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.events.GluttonyAbilitiesEvent;
import kaede.reabista.events.YggdrasillTeleportEvent;
import kaede.reabista.network.EditCommandPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.network.OpenAbilityGuiPacket;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditGUI extends Screen {

    private final Player entity;
    private final HashMap<String, Object> guistate = new HashMap<>();
    private static final ResourceLocation TEXTURE = new ResourceLocation("reabista:textures/gui/ability_gui.png");

    private AutocompleteEditBox command;
    private Button button_e;

    public EditGUI(Player entity) {
        super(Component.literal("編集能力"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();

        int cx = this.width / 2;
        int cy = this.height / 2;

        int by = cy - 35 + 45;
        int ey = cy - 60 + 45;

        this.command = new AutocompleteEditBox(this.font, cx - 59, ey, 118, 18,
                Component.literal("ここにコマンド入力"));
        this.command.setMaxLength(32767);
        guistate.put("text:command", this.command);
        this.addRenderableWidget(this.command);


        this.button_e = Button.builder(Component.literal("実行"), b -> executeCommand())
                .bounds(cx - 20, by, 40, 20)
                .build();
        guistate.put("button:button_e", this.button_e);
        this.addRenderableWidget(this.button_e);
    }

    private void executeCommand() {
        Minecraft mc = Minecraft.getInstance();
        double ap = mc.player.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();
        switch (command.getValue()) {
            case "copy" -> {
                if (ap < 100) return;
                mc.setScreen(new kaede.reabista.client.gui.CopyGUI(mc.player));
            }
            case "gluttony" -> {
                if (ap < 75) return;
                mc.setScreen(new kaede.reabista.client.gui.GluttonyGUI(mc.player));
            }
            case "guard" -> {
                if (ap < 50) return;
                mc.setScreen(new kaede.reabista.client.gui.GuardGUI(mc.player));
            }
            default -> {
                NetworkHandler.sendToServer(new EditCommandPacket(command.getValue()));
                mc.setScreen(null);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int cx = this.width / 2;
        int cy = this.height / 2;

        int ky = cy - 120 + 45;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE, cx - 88, cy - 83, 0, 0, 176, 166);
        RenderSystem.disableBlend();

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.command.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawString(this.font, Component.literal("コマンドを入力してください"),
                cx - 57 + 30, cy - 83 + 11, -16777216, false);

        // Autocomplete 描画（最大6件表示）
        this.command.renderSuggestions(guiGraphics, cx - 57, ky, 6);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC
            this.minecraft.setScreen(null);
            return true;
        } else if (key == 257 || key == 335) { // Enter
            if (!command.applySuggestion()) {
                executeCommand();
            }
            return true;
        } else if (key == 258) { // Tab
            return command.applySuggestion(); // Tabで候補確定
        } else if (key == 265) { // UP
            command.moveSelectionUp();
            return true;
        } else if (key == 264) { // DOWN
            command.moveSelectionDown();
            return true;
        }

        return this.command.keyPressed(key, b, c) || super.keyPressed(key, b, c);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (command.scrollSuggestions(delta)) return true; // 反転
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ============================
    //      Autocomplete EditBox
    // ============================
    private static class AutocompleteEditBox extends EditBox {

        private List<String> suggestions = new ArrayList<>();
        private int selectedIndex = -1;
        private int scrollOffset = 0;
        private final net.minecraft.client.gui.Font myFont;

        public AutocompleteEditBox(net.minecraft.client.gui.Font font, int x, int y, int width, int height, Component msg) {
            super(font, x, y, width, height, msg);
            this.myFont = font;
            this.setResponder(this::updateSuggestions);
        }

        private void updateSuggestions(String text) {
            this.suggestions.clear();
            this.selectedIndex = -1;
            this.scrollOffset = 0;

            if (text.startsWith("create:")) {
                String prefix = text.substring("create:".length()).toLowerCase();

                ForgeRegistries.ITEMS.getKeys().stream()
                        .map(ResourceLocation::toString)
                        .filter(id -> id.startsWith(prefix))
                        .forEach(suggestions::add);
            }
        }

        public void renderSuggestions(GuiGraphics gui, int x, int y, int maxVisible) {
            int yOffset = 0;
            int visibleCount = Math.min(maxVisible, suggestions.size() - scrollOffset);

            double mouseX = Minecraft.getInstance().mouseHandler.xpos();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos();

            for (int i = 0; i < visibleCount; i++) {
                int index = i + scrollOffset;
                String text = suggestions.get(index);

                int suggestionX = x - 2;
                int suggestionWidth = this.getWidth() + 4;
                int suggestionHeight = 10;
                int yTop = y + yOffset;
                int yBottom = yTop + suggestionHeight;

                double guiMouseX = Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getGuiScale();
                double guiMouseY = Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getGuiScale();

                // カーソルが乗ると選択状態
                if (guiMouseX >= suggestionX && guiMouseX <= suggestionX + suggestionWidth &&
                        guiMouseY >= yTop && guiMouseY <= yTop + suggestionHeight) {
                    selectedIndex = index;
                }

                // 背景
                gui.fill(suggestionX, yTop, suggestionX + suggestionWidth, yTop + suggestionHeight, 0xBF000000);

                // 選択中文字色を見やすく黄色に
                int color = (index == selectedIndex) ? 0xBFFFFF00 : 0xBFFFFFFF;
                gui.drawString(myFont, Component.literal(text), x, yTop, color, false);

                yOffset += suggestionHeight;
            }
        }

        public void moveSelectionUp() {
            if (suggestions.isEmpty()) return;
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = suggestions.size() - 1;
            adjustScroll();
        }

        public void moveSelectionDown() {
            if (suggestions.isEmpty()) return;
            selectedIndex++;
            if (selectedIndex >= suggestions.size()) selectedIndex = 0;
            adjustScroll();
        }

        private void adjustScroll() {
            int maxVisible = 6;
            if (selectedIndex < scrollOffset) scrollOffset = selectedIndex;
            if (selectedIndex >= scrollOffset + maxVisible) scrollOffset = selectedIndex - maxVisible + 1;
        }

        public boolean scrollSuggestions(double delta) {
            if (suggestions.isEmpty()) return false;
            int maxVisible = 5;
            scrollOffset -= delta;
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > suggestions.size() - maxVisible)
                scrollOffset = Math.max(suggestions.size() - maxVisible, 0);
            return true;
        }

        public boolean applySuggestion() {
            if (selectedIndex >= 0 && selectedIndex < suggestions.size()) {
                this.setValue("create:" + suggestions.get(selectedIndex));
                this.setCursorPosition(this.getValue().length());
                suggestions.clear();
                selectedIndex = -1;
                scrollOffset = 0;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && selectedIndex >= 0) {
                return applySuggestion();
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}