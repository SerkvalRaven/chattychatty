package serkval.r.cordvoice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import serkval.r.cordvoice.cast.DiscordedPlayer;
import serkval.r.discordbot.MinecraftCordAuth;

public class AuthCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated){
        dispatcher.register(CommandManager.literal("cvauth")
                .then(CommandManager
                        .argument("code", StringReader::read).executes(AuthCommand::run))
        );
    }

    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ((DiscordedPlayer)player).setDiscordId(MinecraftCordAuth.Auths.get(context.getArgument("code",String.class)));
        return 1;
    }
}
