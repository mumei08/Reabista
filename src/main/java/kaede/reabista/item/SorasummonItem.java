package kaede.reabista.item;

import kaede.reabista.entity.TokinosoraEntity;
import kaede.reabista.registry.ModEntities;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.MobSpawnType;

import java.util.List;

public class SorasummonItem extends Item {

    public SorasummonItem() {
        super(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) world;

            // 召喚位置
            BlockPos spawnPos = player.blockPosition().above();

            // エンティティ生成
            TokinosoraEntity entity = ModEntities.TOKINOSORA.get().create(serverWorld);
            if (entity != null) {
                entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                entity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
                serverWorld.addFreshEntity(entity);
            }

            // 最初の1回だけメッセージ＆周囲弱体化
            if (entity instanceof LivingEntity) {
                Vec3 center = new Vec3(player.getX(), player.getY(), player.getZ());
                CommandSourceStack src = new CommandSourceStack(
                        CommandSource.NULL,
                        player.position(),
                        player.getRotationVector(),
                        serverWorld,
                        4,              // 権限
                        "",             // name
                        Component.literal(""),
                        serverWorld.getServer(),
                        null
                ).withSuppressedOutput(); // チャット出力なし

                // タイトル表示
                serverWorld.getServer().getCommands().performPrefixedCommand(
                        src,
                        "title @a times 20 20 20"
                );
                serverWorld.getServer().getCommands().performPrefixedCommand(
                        src,
                        "title @a title {\"text\":\"反逆者なの？\",\"color\":\"blue\"}"
                );
                serverWorld.getServer().getCommands().performPrefixedCommand(
                        src,
                        "title @a subtitle {\"text\":\"じゃあ敵だね？\",\"color\":\"dark_red\",\"bold\":true}"
                );

                // 弱体化付与
                AABB box = new AABB(center.x - 10, center.y - 10, center.z - 10,
                        center.x + 10, center.y + 10, center.z + 10);
                List<? extends Entity> entities = world.getEntitiesOfClass(Entity.class, box, e -> e != entity);
                for (Entity e : entities) {
                    if (e instanceof LivingEntity le && !le.level().isClientSide()) {
                        le.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 255, false, false));
                        le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 255, false, false));
                        le.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 3, false, false));
                    }
                }

                // エンドポータル召喚音
                SoundEvent sound = net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS.getValue(
                        new net.minecraft.resources.ResourceLocation("block.end_portal.spawn"));
                if (sound != null) {
                    serverWorld.playSound(
                            null,
                            new BlockPos((int)center.x, (int)center.y, (int)center.z),
                            sound,
                            SoundSource.MASTER,
                            1.0F,
                            1.0F
                    );
                }
            }
        }

        return InteractionResultHolder.success(stack);
    }
}