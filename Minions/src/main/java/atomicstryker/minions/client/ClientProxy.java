package atomicstryker.minions.client;

import atomicstryker.minions.common.IProxy;
import atomicstryker.minions.common.MinionsCore;
import atomicstryker.minions.common.entity.EntityMinion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientProxy implements IProxy
{
    
    private static MinionsClient client;
    
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        client =  new MinionsClient();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RenderChickenLightningBolt());
        MinecraftForge.EVENT_BUS.register(client);
    }
    
    @Override
    public void load(FMLInitializationEvent evt)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MinionsCore.instance.itemMastersStaff, 0, new ModelResourceLocation("minions:masterstaff", "inventory"));
    }
    
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent tick)
    {
        if (tick.phase == Phase.END)
        {
            client.onRenderTick(tick.renderTickTime);
        }
    }
    
    @SubscribeEvent
    public void onWorldTick(TickEvent.PlayerTickEvent tick)
    {
        if (tick.phase == Phase.END)
        {
            client.onPlayerTick(tick.player.world);
        }
    }
    
    @Override
    public void registerRenderInformation()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityMinion.class, new IRenderFactory<EntityMinion>()
        {
            @Override
            public Render<? super EntityMinion> createRenderFor(RenderManager manager)
            {
                return new RenderMinion(manager, new ModelMinion(), 0.25F);
            }
        });
    }
    
    @Override
    public boolean hasPlayerMinions(EntityPlayer player)
    {
        return MinionsClient.hasMinionsSMPOverride;
    }
    
    @Override
    public void onMastersGloveRightClickHeld(ItemStack itemstack, World world, EntityPlayer player)
    {
        MinionsClient.onMastersGloveRightClickHeld(player);
    }

    @Override
    public void onMastersGloveRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        client.onMastersGloveRightClick(world, player);
    }
}
