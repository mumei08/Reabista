package kaede.reabista.item.Crystal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Comparator;
import java.util.List;

public class GuardCrystal extends Item {

    TagKey<EntityType<?>> ENTITY_PROJECTILE_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:impact_projectiles"));

    public GuardCrystal() {
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {
        super.inventoryTick(stack, level, holder, slot, selected);

        if (level.isClientSide()) return;

        // サーバー側としてキャスト
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 center = holder.position();
        double radius = 4.0;
        AABB box = new AABB(center, center).inflate(radius);

        List<Entity> found = level.getEntitiesOfClass(Entity.class, box, e -> true)
                .stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();

        for (Entity e : found) {
            if (e.getType().is(ENTITY_PROJECTILE_TAG)) {
                // Entity を消す
                e.discard();

                // Particle をサーバーに送る
                serverLevel.sendParticles(
                        ParticleTypes.SPIT,
                        e.getX(), e.getY(), e.getZ(),
                        1, 0, 1, 0, 0
                );
            }
        }
    }
}