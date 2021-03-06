package de.mineformers.investiture.core;

import de.mineformers.investiture.client.renderer.block.ModuleStateMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A proxy acts as handler for side-specific operations. All actions involving classes, fields or methods marked with
 * {@link SideOnly SideOnly} should go through a proxy.
 */
public interface Proxy
{
    /**
     * Fired during the pre-initialisation phase. Should be used for registering blocks, items etc.
     *
     * @param event the event that triggers this method
     */
    default void preInit(FMLPreInitializationEvent event)
    {
    }

    /**
     * Fired during the initialisation phase. Should be used for registering recipes.
     *
     * @param event the event that triggers this method
     */
    default void init(FMLInitializationEvent event)
    {
    }

    /**
     * Fired during the post-initialisation phase. Should be used for all kinds of interaction with other mods.
     *
     * @param event the event that triggers this method
     */
    default void postInit(FMLPostInitializationEvent event)
    {
    }

    default void serverStart(FMLServerStartingEvent event)
    {

    }

    /**
     * Automatically registers all resources associated with a block.
     *
     * @param domain the domain of the module the resources are associated with
     * @param block  the block to register
     */
    @SideOnly(Side.CLIENT)
    default void registerBlockResources(String domain, Block block)
    {
        registerBlockResources(domain, block, ModuleStateMap.builder());
    }

    /**
     * Automatically registers all resources associated with a block.
     *
     * @param domain the domain of the module the resources are associated with
     * @param block  the block to register
     * @param map    the state mapper determining which properties to use in the block's rendering
     */
    @SideOnly(Side.CLIENT)
    default void registerBlockResources(String domain, Block block, ModuleStateMap.Builder map)
    {
        Item item = Item.getItemFromBlock(block);
        List<IBlockState> states = block.getBlockState().getValidStates();
        ModuleStateMap mapper = map.withDomain(domain).build();
        final Map<IBlockState, ModelResourceLocation> resources =
            states.stream().collect(Collectors.toMap(Function.identity(), mapper::getModelResourceLocation));
        ModelLoader.setCustomMeshDefinition(item, stack -> resources.get(block.getStateFromMeta(stack.getItemDamage())));
        ModelBakery.registerItemVariants(item, resources.values().toArray(new ModelResourceLocation[resources.size()]));
        ModelLoader.setCustomStateMapper(block, mapper);
    }
}
