package serkval.r.cordvoice.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import serkval.r.cordvoice.cast.DiscordedPlayer;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_DiscordedPlayer implements DiscordedPlayer {
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    private long discordId;

    @Override
    public void setDiscordId(long id) {
        discordId = id;
    }

    @Override
    public long getDiscordId() {
        return discordId;
    }

    @Override
    public String getUserName(){
        return player.getName().asString();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci){
        NbtCompound compound = new NbtCompound();
        compound.putLong("id",discordId);
        nbt.put("cordvoice:discord_data",compound);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci){
        NbtCompound compound = nbt.getCompound("cordvoice:discord_data");
        if(compound!=null){
            this.discordId = compound.contains("id") ? compound.getLong("id") : -1;
        }
    }
}
