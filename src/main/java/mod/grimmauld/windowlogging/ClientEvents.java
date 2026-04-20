package mod.grimmauld.windowlogging;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.Map;
import java.util.Optional;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientEvents::clientInit);
        modBus.addListener(ClientEvents::registerRenderers);
        modBus.addListener(ClientEvents::registerColorProviders);
        modBus.addListener(ClientEvents::onModelBake);
    }

    public static void clientInit(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(DeferredRegistries.WINDOW_IN_A_BLOCK.get(), renderType -> true));
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DeferredRegistries.WINDOW_IN_A_BLOCK_TILE_ENTITY.get(), WindowInABlockTileEntityRenderer::new);
    }

    public static void registerColorProviders(RegisterColorHandlersEvent.Block event) {
        WindowBlockColor.registerFor(event, DeferredRegistries.WINDOW_IN_A_BLOCK.get());
    }

    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        DeferredRegistries.WINDOW_IN_A_BLOCK.get().getStateDefinition()
            .getPossibleStates()
            .stream()
            .map(state -> Optional.ofNullable(DeferredRegistries.WINDOW_IN_A_BLOCK.getId())
                .map(rl -> new ModelResourceLocation(rl, BlockModelShaper.statePropertiesToString(state.getValues()))))
            .flatMap(Optional::stream)
            .forEach(location -> modelRegistry.computeIfPresent(location, (ignored, model) -> new WindowInABlockModel(model)));
    }
}
