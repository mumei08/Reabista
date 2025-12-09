package kaede.reabista.item.Crystal;

import kaede.reabista.registry.ModAttributes;
import kaede.reabista.registry.ModItems;
import kaede.reabista.util.AttributeUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GluttonyCrystal extends Item {

    public GluttonyCrystal() {
        super(new Item.Properties()
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
    public InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        Player player = ctx.getPlayer();
        Vec3 pos = Vec3.atCenterOf(ctx.getClickedPos());

        if (world.isClientSide || player == null)
            return InteractionResult.SUCCESS;

        // ==========
        // 範囲の全エンティティ取得
        // ==========
        double radius = 12.5;
        AABB area = new AABB(pos, pos).inflate(radius);

        List<Entity> list = world.getEntities(null, area);

        int absorbedCount = 0;

        for (Entity e : list) {
            if (e == player) continue; // 自分は吸わない

            absorbedCount++;

            e.discard(); // エンティティ消去

            world.addParticle(
                    ParticleTypes.SPIT,
                    e.getX(), e.getY(), e.getZ(),
                    0, 1, 0
            );
        }

        // ==========
        // GLUTTONY 属性に「吸収数」だけ加算
        // ==========
        if (absorbedCount > 0 && player.getAttributes().hasAttribute(ModAttributes.GLUTTONY_ENTITY.get())) {
            var att = player.getAttribute(ModAttributes.GLUTTONY_ENTITY.get());
            if (att != null) {
                att.setBaseValue(att.getBaseValue() + absorbedCount);
                AttributeUtils.updateAttributeModifier(player, "GENTITY");
            }
        }

        // ==========
        // アイテムを 1 つ消費
        // ==========
        player.getInventory().clearOrCountMatchingItems(
                p -> p.getItem() == ModItems.GLUTTONY_CRYSTAL.get(),
                1,
                player.getInventory()
        );

        return InteractionResult.SUCCESS;
    }
}