package kaede.reabista.registry;

import kaede.reabista.Reabista;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureHelper {
    public static void placeStructure(Level level, BlockPos pos, String structureName) {
        if (!(level instanceof ServerLevel server))
            return;

        ResourceLocation rl = new ResourceLocation(Reabista.MODID, structureName);

        StructureTemplate template = server.getStructureManager().getOrCreate(rl);

        template.placeInWorld(
                server,
                pos,
                pos,
                new StructurePlaceSettings(),
                server.random,
                2
        );
    }

    public static void clearArea(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server))
            return;

        BlockState air = Blocks.AIR.defaultBlockState();

        server.setBlock(pos, air, 1);
    }
}