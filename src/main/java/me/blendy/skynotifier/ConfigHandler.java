package me.blendy.skynotifier;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;

    public static void init() {
        File configFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "config/skynotifier.cfg");
        config = new Configuration(configFile);
        config.load();

        String generalCategory = "general";
        config.addCustomCategoryComment(generalCategory, "General Configuration");


        SkyNotifier.channelID = config.getString("channelID", generalCategory, "0",  "Discord channel ID to send messages to");
        SkyNotifier.token = config.getString("token", generalCategory, "", "Discord bot token");
        SkyNotifier.keywords = config.getString("keywords", generalCategory, "", "Keywords to trigger ping, separated by ;");
        SkyNotifier.message = config.getString("message", generalCategory, "@here%msg", "Message to send on discord. %msg will get replaced by message");
        // Set the value
        config.save();
    }

}