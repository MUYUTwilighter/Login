package cool.muyucloud.login.mixin;

import cool.muyucloud.login.Accounts;
import cool.muyucloud.login.access.ServerPlayerEntityAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnected", at = @At("TAIL"))
    public void onPlayerConnected(ServerPlayerEntity player, CallbackInfo ci) {
        if (!this.server.isDedicated() || ((ServerPlayerEntityAccess) player).isLoggedIn()) {
            return;
        }

        if (Accounts.isNewPlayer(player.getName().getString())) {
            player.sendMessage(Text.literal("使用 /register <密码> 来注册")
                    .setStyle(
                            Style.EMPTY.withColor(TextColor.parse("blue"))
                                    .withUnderline(true)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/register "))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击复制到聊天栏")))
                    ));
        } else {
            player.sendMessage(Text.literal("使用 /login <密码> 来登录")
                    .setStyle(
                            Style.EMPTY.withColor(TextColor.parse("blue"))
                                    .withUnderline(true)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/login "))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击复制到聊天栏")))
                    ));
        }
    }
}
