package me.xa5.modconfig;

import me.xa5.smoothbedrock.SmoothBedrock;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class FabricModConfig {
    private CommentedConfigurationNode config;
    private File configFolder = new File("config");
    private HoconConfigurationLoader loader;

    public void loadConfig(File configFile, Runnable onLoad) {
        try {
            configFolder.mkdirs();
            if (configFile.createNewFile()) {
                SmoothBedrock.LOGGER.info("Created default config file " + configFile.getName());
            }

            HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder();
            this.loader = builder.setFile(configFile).build();

            this.config = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            onLoad.run();
            saveConfig();
        } catch (IOException e) {
            SmoothBedrock.LOGGER.warn("Failed to load config", e);
        }
    }

    public CommentedConfigurationNode getNode(Object... node) {
        return config.getNode(node);
    }

    public void saveConfig() {
        try {
            this.loader.save(config);
        } catch (IOException e) {
            SmoothBedrock.LOGGER.warn("Failed to save config!", e);
        }
    }
}