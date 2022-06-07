package serkval.r.cordvoice.voicechat;

import net.minecraft.entity.player.PlayerEntity;
import serkval.r.cordvoice.cast.DiscordedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ChatRoomManager {
    private static ArrayList<ChatRoom> registry = new ArrayList<>();
    private static HashMap<DiscordedPlayer, HashSet<DiscordedPlayer>> inviteQueue = new HashMap<>();

    private static class ChatRoom{
        private ArrayList<DiscordedPlayer> members = new ArrayList<>();

        public ChatRoom(DiscordedPlayer[] players){
            for(DiscordedPlayer player: players)
                members.add(player);
        }

        public DiscordedPlayer[] getMembers(){
            DiscordedPlayer[] toReturn = new DiscordedPlayer[members.size()];
            for(int i = 0; i< toReturn.length; i++)
                toReturn[i] = members.get(i);
            return toReturn;
        }

        public boolean removeMember(DiscordedPlayer player){
            if(!members.contains(player)) return false;
            members.remove(player);
            return true;
        }

        public boolean hasMember(DiscordedPlayer player){
            return members.contains(player);
        }

        public void addMember(DiscordedPlayer... players){
            members.addAll(Arrays.asList(players));
        }
    }

    public static boolean createChatRoom(DiscordedPlayer... players){
        registry.add(new ChatRoom(players));
        return true;
    }

    public static DiscordedPlayer[] getPlayersChatRoomMembers(DiscordedPlayer player){
        ChatRoom chat = getJoinedChatRoom(player);
        if(chat==null) return null;
        return chat.getMembers();
    }

    private static ChatRoom getJoinedChatRoom(DiscordedPlayer player){
        for (ChatRoom ChatRoom : registry)
            if (ChatRoom.hasMember(player))
                return ChatRoom;
        return null;
    }

    public static boolean leaveChatRoom(DiscordedPlayer player){
        ChatRoom chat = getJoinedChatRoom(player);
        if(chat==null) return false;
        chat.removeMember(player);
        if(chat.members.size()<=0)
            registry.remove(chat);
        return true;
    }

    public static boolean isInSameChatRoom(DiscordedPlayer player1, DiscordedPlayer player2){
        ChatRoom chat = getJoinedChatRoom(player1);
        if(chat==null) return false;
        return chat.hasMember(player2);
    }

    public static boolean sendInvite(DiscordedPlayer sender, DiscordedPlayer[] players){
        ChatRoom chat = getJoinedChatRoom(sender);
        ArrayList<DiscordedPlayer> targets = new ArrayList<>();
        if(chat==null)
            targets.addAll(Arrays.asList(players));
        else{
            for(DiscordedPlayer player: players)
                if(!chat.hasMember(player))
                    targets.add(player);
        }
        if(targets.size()<=0) return false;

        for(DiscordedPlayer player : targets){
            if(!inviteQueue.containsKey(player))
                inviteQueue.put(player,new HashSet<>());
            inviteQueue.get(player).add(sender);
        }
        return true;
    }

    public static boolean acceptInvite(DiscordedPlayer acceptor){
        if(!inviteQueue.containsKey(acceptor)) return false;
        DiscordedPlayer sender = (DiscordedPlayer) inviteQueue.get(acceptor).toArray()[0];
        ChatRoom chat = getJoinedChatRoom(sender);
        if(chat==null)
            createChatRoom(sender,acceptor);
        else
            chat.addMember(acceptor);
        inviteQueue.remove(acceptor);
        return true;
    }

    public static String[] getPendingInviteList(DiscordedPlayer player){
        if(!inviteQueue.containsKey(player)) return null;
        DiscordedPlayer[] arr = (DiscordedPlayer[]) inviteQueue.get(player).toArray();
        String[] toReturn = new String[arr.length];
        for(int i = 0; i<arr.length; i++)
            toReturn[i] = arr[i].getUserName();
        return toReturn;
    }

    public static DiscordedPlayer[] getPendingInvitePlayers(DiscordedPlayer player){
        if(!inviteQueue.containsKey(player)) return null;
        return (DiscordedPlayer[]) inviteQueue.get(player).toArray();
    }

    public static boolean acceptInvite(DiscordedPlayer acceptor, String senderName){
        if(!inviteQueue.containsKey(acceptor)) return false;
        DiscordedPlayer sender = inviteQueue.get(acceptor).stream().filter(n->n.getUserName().equals(senderName)).toList().get(0);
        ChatRoom chat = getJoinedChatRoom(sender);
        if(chat==null)
            createChatRoom(sender,acceptor);
        else
            chat.addMember(acceptor);
        inviteQueue.remove(acceptor);
        return true;
    }

    public static void flush(){
        registry.clear();
        inviteQueue.clear();
    }
}
