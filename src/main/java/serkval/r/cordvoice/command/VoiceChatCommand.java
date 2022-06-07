package serkval.r.cordvoice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import serkval.r.cordvoice.cast.DiscordedPlayer;
import serkval.r.cordvoice.voicechat.ChatRoomManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoiceChatCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated){
        dispatcher.register(CommandManager.literal("cvchat")
                .then(CommandManager.literal("leave")
                        .executes(VoiceChatCommand::leaveVoiceChat)
                )
                .then(CommandManager.literal("invite")
                        .then(CommandManager.argument("players", EntityArgumentType.players())
                                .executes(VoiceChatCommand::inviteVoiceChat)
                        )
                )
                .then(CommandManager.literal("accept")
                        .then(CommandManager.argument("sender", StringArgumentType.word())
                                .suggests(new PendingInvitesSuggester())
                                    .executes(VoiceChatCommand::acceptInvite)
                        ).executes(VoiceChatCommand::acceptInvite)
                )
        );
    }

    public static int leaveVoiceChat(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        return ChatRoomManager.leaveChatRoom((DiscordedPlayer) context.getSource().getPlayer())? 1: 0;
    }

    public static int inviteVoiceChat(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        DiscordedPlayer player = (DiscordedPlayer) context.getSource().getPlayer();
        DiscordedPlayer[] targets = (DiscordedPlayer[]) context.getArgument("players",PlayerEntity[].class);
        List<DiscordedPlayer> a = Arrays.asList(targets);
        a.remove(player);
        return ChatRoomManager.sendInvite(player, (DiscordedPlayer[]) a.toArray())? 1:0;
    }

    public static int acceptInvite(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        DiscordedPlayer player = (DiscordedPlayer) context.getSource().getPlayer();
        String sender = context.getArgument("sender",String.class);
        if(sender!=null)
            ChatRoomManager.acceptInvite(player,sender);
        return 1;
    }

    private static class PendingInvitesSuggester implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            DiscordedPlayer player = (DiscordedPlayer) context.getSource().getPlayer();
            String[] list = ChatRoomManager.getPendingInviteList(player);
            CommandSource.suggestMatching(ChatRoomManager.getPendingInvitePlayers(player),builder,);
            if(list!=null)
                for(String str: list)
                    builder.suggest(str);
            return builder.buildFuture();
        }
    }
}
