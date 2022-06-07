package serkval.r.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import serkval.r.cordvoice.CordVoiceMod;

import javax.security.auth.login.LoginException;

public class CordBot {
    public static JDA jda;

    public static void startDiscord(){
        try {
            JDABuilder builder = JDABuilder.createDefault("token");

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
            // Enable the bulk delete event
            builder.setBulkDeleteSplittingEnabled(false);
            // Disable compression (not recommended)
            builder.setCompression(Compression.ZLIB);
            // Set activity (like "playing Something")
            builder.setActivity(Activity.playing("Minecraft Server"));

            jda = builder.build();
        } catch (LoginException e) {
            CordVoiceMod.LOGGER.error("Failed to start discord!\n" +
                    "Token error perhaps?");
        }
    }
}
