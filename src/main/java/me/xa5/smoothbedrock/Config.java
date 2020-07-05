package me.xa5.smoothbedrock;

import blue.endless.jankson.Comment;
import net.minecraft.world.dimension.DimensionType;

public class Config {
    @Comment("true: Dimensions in filter will have flat bedrock. false: Dimensions in list will not have flat bedrock")
    public boolean isWhitelist = false;

    @Comment("A list of dimension ids that this mod should filter based on the 'Act as whitelist' setting.")
    public String[] dimensionFilter = getDefaultFilter();

    private String[] getDefaultFilter() {
        return new String[]{DimensionType.THE_END_REGISTRY_KEY.getValue().toString()};
    }
}