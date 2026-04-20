package mod.grimmauld.windowlogging;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WindowInABlockTileEntity extends BlockEntity {
    public static final ModelProperty<WindowInABlockTileEntity> WINDOWLOGGED_TE = new ModelProperty<>();

    private final ModelData modelData = ModelData.builder().with(WINDOWLOGGED_TE, this).build();
    private BlockState partialBlock = Blocks.AIR.defaultBlockState();
    private BlockState windowBlock = Blocks.AIR.defaultBlockState();
    private CompoundTag partialBlockTileData = new CompoundTag();
    private BlockEntity partialBlockTileEntity;

    public WindowInABlockTileEntity(BlockPos pos, BlockState blockState) {
        super(DeferredRegistries.WINDOW_IN_A_BLOCK_TILE_ENTITY.get(), pos, blockState);
        setPartialBlockTileData(new CompoundTag());
    }

    public CompoundTag getPartialBlockTileData() {
        return partialBlockTileData;
    }

    public void setPartialBlockTileData(CompoundTag partialBlockTileData) {
        this.partialBlockTileData = partialBlockTileData;
        this.partialBlockTileEntity = null;
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        partialBlock = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compound.getCompound("PartialBlock"));
        windowBlock = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compound.getCompound("WindowBlock"));
        setPartialBlockTileData(compound.getCompound("PartialData"));
        requestModelDataUpdate();
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.put("PartialBlock", NbtUtils.writeBlockState(getPartialBlock()));
        compound.put("WindowBlock", NbtUtils.writeBlockState(getWindowBlock()));
        compound.put("PartialData", partialBlockTileData);
    }

    public void updateWindowConnections() {
        if (level == null) {
            return;
        }
        for (Direction side : Direction.values()) {
            BlockPos offsetPos = worldPosition.relative(side);
            windowBlock = getWindowBlock().updateShape(side, level.getBlockState(offsetPos), level, worldPosition, offsetPos);
        }
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2 | 16);
        setChanged();
    }

    @Override
    @Nonnull
    public ModelData getModelData() {
        return modelData;
    }

    public BlockState getPartialBlock() {
        return partialBlock;
    }

    public void setPartialBlock(BlockState partialBlock) {
        this.partialBlock = partialBlock;
        this.partialBlockTileEntity = null;
    }

    public BlockState getWindowBlock() {
        return windowBlock;
    }

    public void setWindowBlock(BlockState windowBlock) {
        this.windowBlock = windowBlock;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    public BlockEntity getPartialBlockTileEntityIfPresent() {
        if (!(getPartialBlock().getBlock() instanceof EntityBlock entityBlock) || level == null) {
            return null;
        }
        if (partialBlockTileEntity == null) {
            try {
                partialBlockTileEntity = entityBlock.newBlockEntity(worldPosition, partialBlock);
                if (partialBlockTileEntity != null) {
                    partialBlockTileEntity.setBlockState(getPartialBlock());
                    partialBlockTileEntity.loadWithComponents(partialBlockTileData, level.registryAccess());
                    partialBlockTileEntity.setLevel(level);
                }
            } catch (Exception e) {
                partialBlockTileEntity = null;
            }
        }
        return partialBlockTileEntity;
    }
}
