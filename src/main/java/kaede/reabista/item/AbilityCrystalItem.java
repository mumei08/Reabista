package kaede.reabista.item;

import net.minecraft.world.item.Item;

/**
 * 能力結晶アイテムの基底クラス
 * 世界転送時に必ず消滅するアイテムはこのクラスを継承する
 */
public class AbilityCrystalItem extends Item {

    public AbilityCrystalItem(Properties properties) {
        super(properties);
    }

    /**
     * これが能力結晶かどうかの判定に使用
     * instanceofで判定可能
     */
    public boolean isAbilityCrystal() {
        return true;
    }
}
