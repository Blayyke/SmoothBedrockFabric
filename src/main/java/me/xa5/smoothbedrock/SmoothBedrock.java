package me.xa5.smoothbedrock;

import me.xa5.modconfig.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;

public class SmoothBedrock implements ModInitializer {
    public static final String MOD_ID = "smoothbedrock";
    private static SmoothBedrock instance;
    public static SBLogger LOGGER = new SBLogger();
    private Config config;

    @Override
    public void onInitialize() {
        instance = this;

        ModConfig modConfig = new ModConfig();
        modConfig.configFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), MOD_ID + ".hjson");
        modConfig.saveDefaultConfig();
        this.config = modConfig.loadConfig();
    }

    public static SmoothBedrock getInstance() {
        return instance;
    }

    public boolean shouldModifyBedrock(IWorld world) {
        boolean isInList = config.dimensionFilter.contains(DimensionType.getId(world.getDimension().getType()).toString());

        if (config.isWhitelist) {
            // Is a whitelist; only return true if the dimension is inside the list
            return isInList;
        } else {
            // Return true if the dimension is not in the list, as it is a blacklist.
            return !isInList;
        }
    }
}