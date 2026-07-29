package kaede.reabista.network.ability;

import kaede.reabista.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 「0と1」能力: エンティティ還元パケット(GUI無し即時発動)。
 *
 * 【仕様】
 * 照準先のエンティティのNBTを保存し、そのシリアライズ文字列の長さ(文字数)を「NBT量」として扱う。
 * このNBT量を二進数に変換し、桁の0の数だけZeroFragment、1の数だけOneFragmentを付与する。
 * 得られた0/1はBinaryWriterGUI(照準先に何もいない状態で能力発動)で
 * アイテムID/エンティティIDのビット列を書いて新たなアイテム/エンティティを生み出すのに使う。
 *
 * 照準先にエンティティがいない場合はBinaryWriterGUIを開く(ClientEvents側で分岐)。
 */
public record BinaryClutch() {

    private static final double RANGE = 20.0;

    public static void encode(BinaryClutch msg, FriendlyByteBuf buf) {}

    public static BinaryClutch decode(FriendlyByteBuf buf) {
        return new BinaryClutch();
    }

    public static void handle(BinaryClutch msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getViewVector(1.0F);
            Vec3 reachEnd = eyePos.add(viewVec.x * RANGE, viewVec.y * RANGE, viewVec.z * RANGE);

            Predicate<Entity> filter = e -> e != player && e.isAlive() && e.isPickable() && e instanceof LivingEntity;
            EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                    level, player, eyePos, reachEnd,
                    player.getBoundingBox().expandTowards(viewVec.scale(RANGE)).inflate(1.0D),
                    filter
            );

            if (hit == null || !(hit.getEntity() instanceof LivingEntity target)) {
                player.sendSystemMessage(Component.literal("§7対象が見つかりません(何もない方向を向いて発動するとビット列書き込みGUIが開きます)"));
                return;
            }

            CompoundTag tag = new CompoundTag();
            target.saveWithoutId(tag);
            int nbtAmount = tag.toString().length();
            String binary = Integer.toBinaryString(nbtAmount);

            int zeros = 0, ones = 0;
            for (char c : binary.toCharArray()) {
                if (c == '0') zeros++;
                else ones++;
            }

            if (zeros > 0) {
                player.getInventory().add(new ItemStack(ModItems.ZERO_FRAGMENT.get(), zeros));
            }
            if (ones > 0) {
                player.getInventory().add(new ItemStack(ModItems.ONE_FRAGMENT.get(), ones));
            }

            player.sendSystemMessage(Component.literal(
                    "§b" + target.getName().getString() + " §7を還元: NBT量=" + nbtAmount
                            + " (2進数: " + binary + ") §7→ §f0×" + zeros + " §71×" + ones + " §7を獲得"));

            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    15, 0.3, 0.3, 0.3, 0.03);
        });
        ctx.get().setPacketHandled(true);
    }
}
