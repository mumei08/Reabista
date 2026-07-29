package kaede.reabista.item.Crystal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class CopyCrystal extends Item {

    public CopyCrystal() {
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

    // ==========================
    //      ブロック右クリック
    // ==========================
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity tile = world.getBlockEntity(pos);

        if (player.isShiftKeyDown()) {
            CompoundTag tileNBT = null;
            if (tile != null) tileNBT = tile.saveWithoutMetadata();

            // ブロックドロップ
            List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, tile, player, player.getMainHandItem());
            for (ItemStack drop : drops) {
                Block.popResource((ServerLevel) world, pos, drop);
            }

            // ブロック破壊（パーティクルなし）
            world.removeBlock(pos, false);

            // ブロックを置き直す
            world.setBlock(pos, state, 3);

            // TileEntity 復元
            if (tileNBT != null) {
                BlockEntity newTile = world.getBlockEntity(pos);
                if (newTile != null) newTile.load(tileNBT);
            }

        } else {
            // 通常右クリック → NBT付きブロックアイテム作成
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            if (tile != null) {
                CompoundTag tileNBT = tile.saveWithoutMetadata();
                stack.getOrCreateTag().put("BlockEntityTag", tileNBT);
            }

            if (player instanceof ServerPlayer sp) {
                sp.getInventory().add(stack);
            }
        }

        return InteractionResult.SUCCESS;
    }
}