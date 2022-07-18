package cool.muyucloud.login.mixin;

import com.mojang.authlib.GameProfile;
import cool.muyucloud.login.Accounts;
import cool.muyucloud.login.access.ServerPlayerEntityAccess;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerEntityAccess {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract boolean changeGameMode(GameMode gameMode);

    private boolean loggedIn = Accounts.isLoggedIn(this.getName().getString());
    private int loginCounter = 600;
    private Vec3d entrancePos;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (this.world.isClient() || !this.server.isDedicated()) {
            return;
        }

        if (!this.isLoggedIn()) {
            if (this.entrancePos == null) {
                this.entrancePos = this.getPos();
            }

            this.getAbilities().invulnerable = true;
            this.teleport(this.entrancePos.x, this.entrancePos.y, this.entrancePos.z);
            this.changeGameMode(GameMode.SPECTATOR);

            --this.loginCounter;

            if (this.loginCounter == 0) {
                if (!this.isLoggedIn()) {
                    this.changeGameMode(this.server.getDefaultGameMode());
                }
                this.networkHandler.disconnect(Text.literal("登录超时"));
            }
        }
    }

    @Inject(method = "onDisconnect", at = @At("TAIL"))
    public void onDisconnect(CallbackInfo ci) {
        Accounts.removeList(this.getName().getString());
    }

    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public void login() {
        Accounts.addList(this.getName().getString());
        this.loggedIn = true;
        this.getAbilities().invulnerable = false;
        this.changeGameMode(this.server.getDefaultGameMode());
    }
}
