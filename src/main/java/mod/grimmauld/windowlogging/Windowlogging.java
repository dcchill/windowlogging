package mod.grimmauld.windowlogging;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class Windowlogging {
    public static final TagKey<Block> WINDOWABLE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "windowable"));
    public static final TagKey<Block> WINDOW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "window"));

    public Windowlogging(IEventBus modBus) {
        DeferredRegistries.register(modBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.register(modBus);
        }
        NeoForge.EVENT_BUS.addListener(EventListener::rightClickPartialBlockWithPaneMakesItWindowLogged);
    }
}
