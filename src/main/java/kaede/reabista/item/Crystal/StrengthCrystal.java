package kaede.reabista.item.Crystal;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 筋力増強能力の結晶アイテム。
 * 発動は能力キー直押しで即時(GUI無し) → StrengthClutchパケットを送る。
 */
public class StrengthCrystal extends Item {

    public StrengthCrystal() {
        super(new Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
