package net.blumbo.keepinvcontrol.mixin;

import net.blumbo.keepinvcontrol.KeepInvControl;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(at = @At("HEAD"), method = "save")
    private void save(CallbackInfoReturnable<Boolean> cir) {
        KeepInvControl.save((MinecraftServer)(Object)this);
    }
    @Inject(at = @At("TAIL"), method = "loadWorld")
    private void load(CallbackInfo ci) {
        KeepInvControl.load((MinecraftServer)(Object)this);
    }
}
