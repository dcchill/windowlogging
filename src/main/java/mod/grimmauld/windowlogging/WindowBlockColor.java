package mod.grimmauld.windowlogging;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class WindowBlockColor implements BlockColor {
    public static void registerFor(RegisterColorHandlersEvent.Block event, WindowInABlockBlock block) {
        event.register(new WindowBlockColor(), block);
    }

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos, int color) {
        if (!(state.getBlock() instanceof WindowInABlockBlock windowInABlockBlock) || world == null || pos == null) {
            return -1;
        }
        return Minecraft.getInstance().getBlockColors().getColor(windowInABlockBlock.getSurroundingBlockState(world, pos), world, pos, color);
    }
}
