package kaede.reabista.registry;

import kaede.reabista.Reabista;
import kaede.reabista.block.HardenedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Reabista.MODID);

    public static final RegistryObject<Block> HARDENED_BLOCK =
            BLOCKS.register("hardened_block", HardenedBlock::new);
}
