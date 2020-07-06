package me.xa5.modconfig;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import me.xa5.smoothbedrock.Config;
import me.xa5.smoothbedrock.SmoothBedrock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModConfig {
    public File configFile;

    public Config loadConfig() {
        try {
            Jankson jankson = Jankson.builder().build();
            JsonObject configJson = jankson.load(configFile);
            Config config = jankson.fromJson(configJson, Config.class);
            SmoothBedrock.LOGGER.info("Loaded config!");
            return config;
        } catch (IOException | SyntaxError e) {
            SmoothBedrock.LOGGER.warn("Failed to load config, using defaults");
            throw new RuntimeException(e);
        }
    }

    public void saveDefaultConfig() {
        Jankson jankson = Jankson.builder().build();
        String result = jankson
                .toJson(new Config()) //The first call makes a JsonObject
                .toJson(true, true, 0);     //The second turns the JsonObject into a String -
        //in this case, preserving comments and pretty-printing with newlines
        try {
            if (configFile.createNewFile()) {
                FileOutputStream out = new FileOutputStream(configFile, false);
                out.write(result.getBytes());
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            SmoothBedrock.LOGGER.warn("Failed to save default config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}