package me.xa5.smoothbedrock;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import me.xa5.modconfig.FabricModConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SmoothBedrock implements ModInitializer {
    public static boolean IS_WHITELIST;
    private static SmoothBedrock instance;
    public static Logger LOGGER = LogManager.getFormatterLogger("SmoothBedrock");
    private final FabricModConfig config = new FabricModConfig();

    private List<Identifier> dimensionFilter;

    @Override
    public void onInitialize() {
        instance = this;
        config.loadConfig(new File("config", "smoothbedrock.conf"), () -> {
            try {
                this.dimensionFilter = config.getNode("Dimension filter")
                        .setComment("A list of dimension ids that this mod should filter based on the 'Act as whitelist' setting.\n" +
                                "Modded dimensions will need to be added here to be affected.")
                        .getList(TypeToken.of(String.class), Lists.newArrayList("minecraft:the_nether", "minecraft:overworld"))
                        .stream()
                        .map(Identifier::new)
                        .collect(Collectors.toList());
            } catch (ObjectMappingException e) {
                throw new RuntimeException(e);
            }

            IS_WHITELIST = config.getNode("Act as whitelist")
                    .setComment("Set to false to make the dimension whitelist act as a blacklist.")
                    .getBoolean(true);
        });
    }

    public static SmoothBedrock getInstance() {
        return instance;
    }

    public boolean shouldModifyBedrock(IWorld world) {
        boolean isInList = dimensionFilter.contains(DimensionType.getId(world.getDimension().getType()));

        if (IS_WHITELIST) {
            // Is a whitelist; only return true if the dimension is inside the list
            return isInList;
        } else {
            // Return true if the dimension is not in the list, as it is a blacklist.
            return !isInList;
        }
    }
}