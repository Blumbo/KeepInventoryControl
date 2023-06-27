package net.blumbo.keepinvcontrol.mixin;

import net.blumbo.keepinvcontrol.KeepInvControl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean keepInv(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        return false;
    }
    @Redirect(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    private boolean wasSpec(ServerPlayerEntity instance) {
        return false;
    }

    @Inject(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private void cloneInvAndExp(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        getInventory().clone(oldPlayer.getInventory());
        if (!KeepInvControl.dropExp(getWorld())) {
            experienceLevel = oldPlayer.experienceLevel;
            totalExperience = oldPlayer.totalExperience;
            experienceProgress = oldPlayer.experienceProgress;
        }
        if (getServerWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
            setScore(oldPlayer.getScore());
        }
    }

}
