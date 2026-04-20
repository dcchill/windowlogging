package mod.grimmauld.windowlogging;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class DeferredRegistries {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredBlock<WindowInABlockBlock> WINDOW_IN_A_BLOCK = registerBlock("window_in_a_block", WindowInABlockBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindowInABlockTileEntity>> WINDOW_IN_A_BLOCK_TILE_ENTITY =
        registerTE("window_in_a_block", () -> BlockEntityType.Builder.of(WindowInABlockTileEntity::new, Blocks.CAULDRON, WINDOW_IN_A_BLOCK.get()));

    private DeferredRegistries() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
        BLOCKS.register(modBus);
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerTE(String id, Supplier<BlockEntityType.Builder<T>> type) {
        return BLOCK_ENTITY_TYPES.register(id, () -> type.get().build(null));
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String id, Supplier<T> blockSupplier) {
        return BLOCKS.register(id, key -> blockSupplier.get());
    }
}
