package me.xa5.smoothbedrock;

import blue.endless.jankson.Comment;
import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;

public class Config {
    @Comment("Set to false to make the dimension whitelist act as a blacklist.")
    public boolean isWhitelist = true;

    @Comment("A list of dimension ids that this mod should filter based on the 'Act as whitelist' setting." +
            "\nModded dimensionFilter will need to be added here to be affected.")
    public List<String> dimensionFilter = getDefaultFilter();

    private List<String> getDefaultFilter() {
        return Lists.newArrayList(getDimId(DimensionType.OVERWORLD).toString(), getDimId(DimensionType.THE_NETHER).toString());
    }

    private static Identifier getDimId(DimensionType type) {
        return Registry.DIMENSION.getId(type);
    }
}