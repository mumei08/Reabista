package kaede.reabista.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class AbilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<IAbilityData> ABILITY =
            CapabilityManager.get(new CapabilityToken<>(){});

    private final IAbilityData inst = new AbilityData();

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ABILITY ? LazyOptional.of(() -> inst).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("GluttonyEnabled", inst.isGluttonyEnabled());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inst.setGluttonyEnabled(nbt.getBoolean("GluttonyEnabled"));
    }
}
