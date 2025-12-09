package kaede.reabista.item;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;

public class EditorEyeItem extends Item {
    public EditorEyeItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        super.useOn(context);

        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Entity entity = context.getPlayer();

        if (entity instanceof LivingEntity _entity)
            _entity.swing(InteractionHand.MAIN_HAND, true);

        // サーバー側で構造物設置
        if (world instanceof ServerLevel _serverworld) {
            StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("reabista", "kaede"));
            if (template != null) {
                template.placeInWorld(_serverworld,
                        BlockPos.containing(x, y + 1, z),
                        BlockPos.containing(x, y + 1, z),
                        new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false),
                        _serverworld.random,
                        3);
            }

            // 空気で消去
            _serverworld.getServer().getCommands().performPrefixedCommand(
                    new CommandSourceStack(CommandSource.NULL, new Vec3(x, y + 1, z), Vec2.ZERO, _serverworld, 4, "", Component.literal(""), _serverworld.getServer(), null).withSuppressedOutput(),
                    "fill ~ ~ ~ ~ ~ ~ air destroy");
        }
        return InteractionResult.SUCCESS;
    }
}