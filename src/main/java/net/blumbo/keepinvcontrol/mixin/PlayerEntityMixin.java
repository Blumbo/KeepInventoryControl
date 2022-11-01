package net.blumbo.keepinvcontrol.mixin;

import com.google.common.collect.ImmutableList;
import net.blumbo.keepinvcontrol.KeepInvControl;
import net.blumbo.keepinvcontrol.misc.KeepInvListData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow protected abstract void dropInventory();

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow public int experienceLevel;

    @Shadow public int totalExperience;

    @Shadow public float experienceProgress;

    @Shadow public abstract void setScore(int score);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "getXpToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean keepExp(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        return !KeepInvControl.dropExp(getWorld());
    }

    @Inject(method = "dropInventory", cancellable = true, at = @At("HEAD"))
    private void dropInventoryMixin(CallbackInfo ci) {
        super.dropInventory();

        boolean blacklistMode = world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY);

        vanishAndDropInv(KeepInvListData.get(!blacklistMode).list, blacklistMode);
        ci.cancel();
    }

    private void vanishAndDropInv(ArrayList<Item> list, boolean dropListedItems) {
        PlayerInventory inv = getInventory();
        List<DefaultedList<ItemStack>> invItems = ImmutableList.of(inv.main, inv.armor, inv.offHand);

        for (List<ItemStack> invItem : invItems) {
            for (int i = 0; i < invItem.size(); ++i) {
                ItemStack itemStack = invItem.get(i);
                if (!itemStack.isEmpty() && list.contains(itemStack.getItem()) == dropListedItems) {
                    if (EnchantmentHelper.hasVanishingCurse(itemStack)) {
                        inv.removeStack(i);
                    } else {
                        inv.player.dropItem(itemStack, true, false);
                        invItem.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
