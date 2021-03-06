package atomicstryker.simplyhax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.minecraftforge.api.distmarker.Dist;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(SimplyHaxFlying.MOD_ID)
@Mod.EventBusSubscriber(modid = SimplyHaxFlying.MOD_ID, value = Dist.CLIENT)
public class SimplyHaxFlying
{
    public final static String MOD_ID = "simplyhaxflying";

    private Minecraft mc = null;
    private EntityPlayer player = null;
    private PlayerCapabilities pcb = null;

    private File configfile = null;
    private float sprintspeed = 0.75f;
    private float maxflyspeed = 1.0f;
    private float defaultWalkSpeed = 0.3f;

    private KeyBinding sprintButton;

    public SimplyHaxFlying() {

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        configfile = evt.getSuggestedConfigurationFile();
        initSettings();
    }

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        sprintButton = new KeyBinding("Simply Hax Sprint", Keyboard.KEY_LSHIFT, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(sprintButton);
    }

    private void initSettings()
    {
        Properties properties = new Properties();

        if (!configfile.exists())
        {
            System.out.println("No SimplyHaxFlying config found, trying to create...");

            try
            {
                configfile.createNewFile();
                properties.load(new FileInputStream(configfile));
            }
            catch (IOException localIOException)
            {
                System.out.println("SimplyHaxFlying config exception: " + localIOException);
            }

            properties.setProperty("maxflyspeed", "1.0");
            properties.setProperty("maxrunspeed", "0.5");

            try
            {
                FileOutputStream fostream = new FileOutputStream(configfile);
                properties.store(fostream, "Derp it's the key settings");
                fostream.close();
            }
            catch (IOException localIOException)
            {
                System.out.println("SimplyHaxFlying config exception: " + localIOException);
            }
        }

        System.out.println("SimplyHaxFlying config found, proceeding...");

        try
        {
            properties.load(new FileInputStream(configfile));
        }
        catch (IOException localIOException)
        {
            System.out.println("SimplyHaxFlying config exception: " + localIOException);
        }

        maxflyspeed = Float.parseFloat(properties.getProperty("maxflyspeed", "1.0"));
        sprintspeed = Float.parseFloat(properties.getProperty("sprintspeed", "0.75"));
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent tick)
    {
        if (tick.phase == Phase.END)
        {
            if (mc == null)
            {
                mc = FMLClientHandler.instance().getClient();
            }

            if (mc.player != player)
            {
                player = mc.player;
                if (player != null)
                {
                    pcb = player.capabilities;
                    defaultWalkSpeed = pcb.getWalkSpeed();
                }
                else
                {
                    pcb = null;
                }
            }

            if (pcb != null)
            {
                pcb.allowFlying = true;

                if (sprintButton.isKeyDown())
                {
                    pcb.setFlySpeed(maxflyspeed);
                    pcb.setPlayerWalkSpeed(sprintspeed);
                }
                else
                {
                    pcb.setFlySpeed(defaultWalkSpeed);
                    pcb.setPlayerWalkSpeed(defaultWalkSpeed);
                }
            }
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            event.setCanceled(true);
        }
    }
}
