package kaede.reabista;

import net.minecraftforge.common.ForgeConfigSpec;

public class ReabistaConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    public static class Common {
        public final ForgeConfigSpec.DoubleValue MULTIPLIER;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Abista Config").push("theusfall");

            MULTIPLIER = builder
                    .comment("崩天のダメージ倍率(default = 1.2)")
                    .defineInRange("damageMultiplier", 1.2, 0.0, 10000.0);

            builder.pop();
        }
    }
}