package dev.cuican.staypro.mixin.network;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.module.modules.movement.NoSlowDown;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by 086 on 16/12/2017.
 */
@Mixin(BlockSoulSand.class)
public class MixinBlockSoulSand {

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo info) {
        // If noslowdown is on, just don't do anything else in this method (slow the player)
        if (ModuleManager.getModuleByName("NoSlowDown").isEnabled() && ((NoSlowDown) ModuleManager.getModuleByName("NoSlowDown")).soulSand.getValue()) info.cancel();
    }


}
