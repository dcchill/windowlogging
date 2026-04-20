package mod.grimmauld.windowlogging;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Arrays;

public final class EventListener {
    private EventListener() {
    }

    public static void rightClickPartialBlockWithPaneMakesItWindowLogged(PlayerInteractEvent.RightClickBlock event) {
        if (tryWindowlog(event.getUseItem(), event.getEntity(), event.getItemStack(), event.getLevel(), event.getPos(), event.getHand())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    public static boolean tryWindowlog(TriState useItem, Player player, ItemStack stack, LevelAccessor level, BlockPos pos, InteractionHand hand) {
        if (useItem == TriState.FALSE || player.isShiftKeyDown() ||
            !player.mayBuild() || stack.isEmpty() ||
            !(stack.getItem() instanceof BlockItem item &&
                item.getBlock() instanceof CrossCollisionBlock block &&
                block.defaultBlockState().is(Windowlogging.WINDOW))) {
            return false;
        }

        BlockState blockState = level.getBlockState(pos);
        if (!blockState.is(Windowlogging.WINDOWABLE) || blockState.getBlock() instanceof WindowInABlockBlock
            || blockState.getOptionalValue(BlockStateProperties.SLAB_TYPE).map(SlabType.DOUBLE::equals).orElse(false)) {
            return false;
        }

        BlockEntity currentTE = level.getBlockEntity(pos);
        level.setBlock(pos, DeferredRegistries.WINDOW_IN_A_BLOCK.get().defaultBlockState(), 3);
        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof WindowInABlockTileEntity wte)) {
            return true;
        }

        wte.setWindowBlock(item.getBlock().defaultBlockState());
        wte.updateWindowConnections();
        SoundType soundtype = wte.getWindowBlock().getSoundType(level, pos, player);
        level.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

        if (blockState.getBlock() instanceof CrossCollisionBlock) {
            for (BooleanProperty side : Arrays.asList(CrossCollisionBlock.EAST, CrossCollisionBlock.NORTH, CrossCollisionBlock.SOUTH, CrossCollisionBlock.WEST)) {
                blockState = blockState.setValue(side, false);
            }
        }
        if (blockState.getBlock() instanceof WallBlock) {
            blockState = blockState.setValue(WallBlock.UP, true);
        }

        wte.setPartialBlock(blockState);
        if (currentTE != null) {
            wte.setPartialBlockTileData(currentTE.saveWithoutMetadata(level.registryAccess()));
        }
        wte.requestModelDataUpdate();
        wte.setChanged();

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.swing(hand);
        return true;
    }
}
