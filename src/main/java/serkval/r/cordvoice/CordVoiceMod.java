package serkval.r.cordvoice;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CordVoiceMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("CordVoice");
    public static CordVoiceMod INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info("CordVoice enabled");
    }
}
