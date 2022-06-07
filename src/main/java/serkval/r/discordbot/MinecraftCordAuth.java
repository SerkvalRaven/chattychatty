package serkval.r.discordbot;

import net.dv8tion.jda.api.entities.User;

import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

public class MinecraftCordAuth {
    public static Random rd = new Random();
    public static HashMap<String,Long> Auths = new HashMap<>();

    public static String generateAuthCode(long id){
        String ids = String.valueOf(id);
        int l = ids.length();
        String msb = ids.substring(0,l);
        String lsb = ids.substring(l);
        String rnd = String.valueOf(rd.nextLong());
        String res = Base64.getEncoder().encodeToString(msb.getBytes())
                +Base64.getEncoder().encodeToString(rnd.getBytes())
                +Base64.getEncoder().encodeToString(lsb.getBytes());
        Auths.put(res,id);
        return res;
    }

    public static void sendAuthMessage(long id){
        User user = CordBot.jda.getUserById(id);
        if(user.hasPrivateChannel()){
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage("Type this through your minecraft chat\n" +
                            "/cvauth "+generateAuthCode(id)))
                    .queue();
        }
    }

    public static void processAuth(){

    }
}
