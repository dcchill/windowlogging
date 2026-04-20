package mod.grimmauld.windowlogging;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mod.grimmauld.windowlogging.BuildConfig.MODID;

@Mod(MODID)
public class Windowlogging {
	public static final TagKey<Block> WINDOWABLE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "windowable"));
	public static final TagKey<Block> WINDOW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, "window"));

	public Windowlogging() {
		DeferredRegistries.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
