package kaede.reabista.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 硬化の結晶化: 爆発では絶対に破壊できないブロック。
 * 通常の採掘(ツール)では硬度相応に破壊できるが、爆発耐性だけ桁違いに高くしてある。
 */
public class HardenedBlock extends Block {
    public HardenedBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(50.0F, 3600000.0F) // 硬度50(採掘可能), 爆発耐性は岩盤級
                .sound(net.minecraft.world.level.block.SoundType.NETHERITE_BLOCK)
        );
    }
}
