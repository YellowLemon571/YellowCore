package net.yelw.yellowcore;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue SCALE_MIN = BUILDER
            .comment("The minimum pokemon scale modifier")
            .defineInRange("minScale", 0.2, 0.01, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SCALE_MAX = BUILDER
            .comment("The maximum pokemon scale modifier")
            .defineInRange("maxScale", 2.0, 0.01, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SCALE_CHANGE = BUILDER
            .comment("The amount to change a pokemon's scale with each resizer use")
            .defineInRange("scaleChange", 0.2, 0.01, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

}
