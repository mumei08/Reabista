package kaede.reabista.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * 「0と1」能力で、エンティティのNBTを二進数に還元した際に得られる断片(1側)。
 * BinaryWriterGUIでアイテムID/エンティティIDのビット列を書く材料として使う。
 */
public class OneFragment extends Item {
    public OneFragment() {
        super(new Properties().stacksTo(6400).rarity(Rarity.RARE));
    }
}
