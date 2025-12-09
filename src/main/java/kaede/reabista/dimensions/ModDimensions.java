package kaede.reabista.dimensions;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensions {

    public static final ResourceKey<Level> YGGDRASIL_LEVEL =
            ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation("reabista", "yggdrasill"));

    public static final ResourceKey<DimensionType> YGGDRASIL_DIMENSION_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    new ResourceLocation("reabista", "yggdrasill"));

    public static void register() {
        System.out.println("ModDimensions registered.");
    }
}