package kaede.reabista.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import kaede.reabista.config.AbilityAction;
import kaede.reabista.network.ability.EditAbility;
import kaede.reabista.network.ability.EditCommandPacket;
import kaede.reabista.network.NetworkHandler;
import kaede.reabista.registry.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditGUI extends Screen {

    private int page = 0;
    private int lastPage = -1;
    private final Player entity;
    private final HashMap<String, Button> buttons = new HashMap<>();
    private final HashMap<String, AutocompleteEditBox> editBoxes = new HashMap<>();
    private static final ResourceLocation TEXTURE = new ResourceLocation("reabista:textures/gui/ability_gui.png");

    public EditGUI(Player entity) {
        super(Component.literal("編集能力"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        super.init();
        renderPage();
    }

    private void renderPage() {
        buttons.values().forEach(this::removeWidget);
        buttons.clear();
        editBoxes.values().forEach(this::removeWidget);
        editBoxes.clear();

        int cx = this.width / 2;
        int cy = this.height / 2;
        int by = cy - 25 + 45;
        int ey = cy - 80 + 45;

        switch (page) {
            case 0 -> {
                addEditBox("command", cx - 59, ey, 118, 18, "ここにコマンド入力", getAvailableEditCommands());
                addButton("e", "実行", cx - 20, by, 40, 20, () -> executeCommand(getEditBoxValue("command")));
                if (entity.getAttributeBaseValue(ModAttributes.ABILITY_POINT.get()) >= 1500){
                    addButton("f", "能力付与", cx - 70, by + 30, 50, 20, () -> {
                        page = 1;
                        renderPage();
                    });
                    addButton("g", "能力削除", cx + 20, by + 30, 50, 20, () -> {
                        page = 2;
                        renderPage();
                    });
                }
            }
            case 1 -> {
                List<String> playerNames = Minecraft.getInstance().level.players().stream()
                        .map(p -> p.getName().getString())
                        .toList();
                addEditBox("player1", cx - 59, ey, 118, 18, "プレイヤー", playerNames);
                List<String> abilities = List.of("edit","copy","fly","tp","gluttony","guard");
                addEditBox("ability", cx - 59, ey + 30, 118, 18, "能力名(英語)", abilities);
                addButton("h","実行",cx - 20, by, 40, 20, () -> AddAbility(getEditBoxValue("player1"), getEditBoxValue("ability")));
            }
            case 2 -> {
                List<String> playerNames2 = Minecraft.getInstance().level.players().stream()
                        .map(p -> p.getName().getString()).toList();
                addEditBox("player2", cx - 59, ey, 118, 18, "プレイヤー", playerNames2);
                addButton("i","実行",cx - 20, by, 40, 20, () -> DelAbility(getEditBoxValue("player2")));
            }
        }
    }

    private List<String> getAvailableEditCommands() {
        double ap = entity.getAttribute(ModAttributes.ABILITY_POINT.get()).getBaseValue();
        List<String> available = new ArrayList<>();

        // EDITタイプのコマンドをAP判定で追加
        for (AbilityAction act : AbilityAction.getByType(AbilityAction.ActionType.EDIT)) {
            if (ap >= act.requiredAP()) {
                available.add(act.key());
            }
        }

        // create: はAP >= 1500 の場合のみ追加
        if (ap >= 1500) {
            available.add("create:");
        }

        return available;
    }

    private void addButton(String key, String text, int x, int y, int w, int h, Runnable onClick) {
        Button b = Button.builder(Component.literal(text), btn -> onClick.run()).bounds(x, y, w, h).build();
        addRenderableWidget(b);
        buttons.put(key, b);
    }

    private void addEditBox(String key, int x, int y, int w, int h, String placeholder, List<String> suggestions) {
        AutocompleteEditBox box = new AutocompleteEditBox(font, x, y, w, h, Component.literal(placeholder), suggestions);
        box.setHint(Component.literal(placeholder));
        addRenderableWidget(box);
        editBoxes.put(key, box);
    }

    private String getEditBoxValue(String key) {
        AutocompleteEditBox box = editBoxes.get(key);
        if (box == null) return null;
        String txt = box.getValue().trim();
        return txt.isEmpty() ? null : txt;
    }

    private void executeCommand(String co) {
        if (co == null || co.isEmpty()) return;
        NetworkHandler.sendToServer(new EditCommandPacket(co));
        Minecraft.getInstance().setScreen(null);
    }

    private void AddAbility(String player, String ability) {
        if (player == null || ability == null) return;
        NetworkHandler.sendToServer(new EditAbility(player, 1, ability));
        Minecraft.getInstance().setScreen(null);
    }

    private void DelAbility(String player) {
        if (player == null) return;
        NetworkHandler.sendToServer(new EditAbility(player, 0, "0"));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        boolean handled = false;
        for (AutocompleteEditBox box : editBoxes.values()) {
            if (box.isFocused()) {
                switch (key) {
                    case 258 -> { box.moveSelectionDown(); handled = true; } // TAB
                    case 257, 335 -> { box.applySuggestion(); handled = true; } // ENTER
                    case 265 -> { box.moveSelectionUp(); handled = true; } // UP
                    case 264 -> { box.moveSelectionDown(); handled = true; } // DOWN
                }
            }
            handled |= box.keyPressed(key, b, c);
        }
        return handled || super.keyPressed(key, b, c);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        boolean handled = false;
        for (AutocompleteEditBox box : editBoxes.values()) {
            handled |= box.scrollSuggestions(delta);
        }
        return handled || super.mouseScrolled(mx, my, delta);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui);
        int left = width / 2 - 88;
        int top = height / 2 - 83;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        gui.blit(TEXTURE, left, top, 0, 0, 176, 166);
        RenderSystem.disableBlend();

        if (page != lastPage) {
            lastPage = page;
            renderPage();
        }

        super.render(gui, mouseX, mouseY, partialTicks);

        for (AutocompleteEditBox box : editBoxes.values()) {
            box.render(gui, mouseX, mouseY, partialTicks);
            box.renderSuggestions(gui, box.getX(), box.getY() + box.getHeight(), 5);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // ============================
    //      Autocomplete EditBox
    // ============================

    private static class AutocompleteEditBox extends EditBox {
        private List<String> baseSuggestions = new ArrayList<>();
        private List<String> filtered = new ArrayList<>();
        private int selectedIndex = -1;
        private int scrollOffset = 0;
        private final net.minecraft.client.gui.Font myFont;

        public AutocompleteEditBox(net.minecraft.client.gui.Font font, int x, int y, int width, int height,
                                   Component msg, List<String> suggestions) {
            super(font, x, y, width, height, msg);
            this.myFont = font;
            setBaseSuggestions(suggestions);
            this.setResponder(this::updateSuggestions);
            updateSuggestions(getValue());
        }

        public void setBaseSuggestions(List<String> suggestions) {
            this.baseSuggestions = (suggestions != null) ? new ArrayList<>(suggestions) : new ArrayList<>();
        }

        public void updateSuggestions(String text) {
            filtered.clear();
            selectedIndex = -1;
            scrollOffset = 0;

            if (text == null) text = "";
            String lower = text.toLowerCase();

            if (text.startsWith("create:")) {
                String prefix = text.substring("create:".length()).toLowerCase();
                ForgeRegistries.ITEMS.getKeys().stream()
                        .map(ResourceLocation::toString)
                        .filter(id -> id.startsWith(prefix))
                        .forEach(filtered::add);
            } else {
                for (String s : baseSuggestions) {
                    if (s.toLowerCase().startsWith(lower)) filtered.add(s);
                }
            }
        }

        public void renderSuggestions(GuiGraphics gui, int x, int y, int maxVisible) {
            if (!this.isFocused() || filtered.isEmpty()) return;

            int yOffset = 0;
            int visibleCount = Math.min(maxVisible, filtered.size() - scrollOffset);
            double guiMouseX = Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getGuiScale();
            double guiMouseY = Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getGuiScale();

            for (int i = 0; i < visibleCount; i++) {
                int index = i + scrollOffset;
                String text = filtered.get(index);

                int suggestionX = x - 2;
                int suggestionWidth = this.getWidth() + 4;
                int suggestionHeight = 10;
                int yTop = y + yOffset;

                if (guiMouseX >= suggestionX && guiMouseX <= suggestionX + suggestionWidth &&
                        guiMouseY >= yTop && guiMouseY <= yTop + suggestionHeight) {
                    selectedIndex = index;
                }

                gui.fill(suggestionX, yTop, suggestionX + suggestionWidth, yTop + suggestionHeight, 0xBF000000);
                int color = (index == selectedIndex) ? 0xBFFFFF00 : 0xBFFFFFFF;
                gui.drawString(myFont, Component.literal(text), x, yTop, color, false);

                yOffset += suggestionHeight;
            }
        }

        public void moveSelectionUp() {
            if (filtered.isEmpty()) return;
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = filtered.size() - 1;
            adjustScroll();
        }

        public void moveSelectionDown() {
            if (filtered.isEmpty()) return;
            selectedIndex++;
            if (selectedIndex >= filtered.size()) selectedIndex = 0;
            adjustScroll();
        }

        private void adjustScroll() {
            int maxVisible = 6;
            if (selectedIndex < scrollOffset) scrollOffset = selectedIndex;
            if (selectedIndex >= scrollOffset + maxVisible) scrollOffset = selectedIndex - maxVisible + 1;
        }

        public boolean scrollSuggestions(double delta) {
            if (!this.isFocused() || filtered.isEmpty()) return false;
            int maxVisible = 5;
            scrollOffset -= delta;
            if (scrollOffset < 0) scrollOffset = 0;
            if (scrollOffset > filtered.size() - maxVisible)
                scrollOffset = Math.max(filtered.size() - maxVisible, 0);
            return true;
        }

        public boolean applySuggestion() {
            if (!this.isFocused() || selectedIndex < 0 || selectedIndex >= filtered.size()) return false;
            String text = filtered.get(selectedIndex);

            if (getValue().startsWith("create:")) this.setValue("create:" + text);
            else this.setValue(text);

            this.setCursorPosition(this.getValue().length());
            filtered.clear();
            selectedIndex = -1;
            scrollOffset = 0;
            return true;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && selectedIndex >= 0) return applySuggestion();
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
