package dev.cuican.staypro.mixin.network;


import dev.cuican.staypro.client.ModuleManager;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 086 on 16/12/2017.
 * Updated by S-B99 on 17/03/20
 */
@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

    @Inject(method = "modifyAcceleration", at = @At("HEAD"), cancellable = true)
    public void modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion, CallbackInfoReturnable<Vec3d> returnable) {
        if (ModuleManager.getModuleByName("Velocity").isEnabled()) {
            returnable.setReturnValue(motion);
            returnable.cancel();
        }
    }

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    public void canCollideCheck(final IBlockState blockState, final boolean b, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(ModuleManager.getModuleByName("LiquidInteract").isEnabled() || (b && blockState.getValue(BlockLiquid.LEVEL) == 0));
    }
}
