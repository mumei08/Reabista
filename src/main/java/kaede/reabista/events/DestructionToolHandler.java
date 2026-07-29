package kaede.reabista.events;

import kaede.reabista.item.Crystal.DestructionCrystal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 破壊能力(結晶化)。
 * DestructionCrystalを手に持ってる間は、あらゆるブロックを瞬時に破壊できる
 * マルチツールとして機能する。岩盤・コマンドブロック・バリアブロックなど
 * 通常サバイバルでは破壊不可能(destroySpeed < 0)なブロックも対象。
 *
 * 通常のマイニング処理(採掘時間の計算)はdestroySpeed<0のブロックに対して
 * そもそも成立しない(バニラ側でブロック破壊自体が起きない)ため、
 * PlayerInteractEvent.LeftClickBlockの時点で強制的にブロックを除去する形で実装している。
 */
@Mod.EventBusSubscriber
public class DestructionToolHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.getMainHandItem().getItem() instanceof DestructionCrystal)) return;

        Level level = player.level();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        if (state.isAir()) return;

        level.removeBlock(pos, false);
        event.setCanceled(true);

        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2, 0.3, 0.3, 0.3, 0.0);
        }
    }
}
