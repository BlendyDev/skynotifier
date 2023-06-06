package me.blendy.skynotifier;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Mod(modid = SkyNotifier.MODID, version = SkyNotifier.VERSION)
public class SkyNotifier {
    public static final String MODID = "skynotifier";
    public static final String VERSION = "1.0";
    public static String token = "";
    public static String channelID = "";
    public static String keywords = "";
    public static String message = "";


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ConfigGUI());
        ClientRegistry.registerKeyBinding(ConfigGUI.KEYBINDING);
        ClientRegistry.registerKeyBinding(BlockDataScanner.KEYBINDING);
        ConfigHandler.init();
        //fileStuff();
		// some example code
    }
    @SuppressWarnings("unused")
    public static void runOnMainThread(Runnable task) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.isCallingFromMinecraftThread()) {
            // If we're already on the main thread, just run the task
            task.run();
        } else {
            // Otherwise, schedule the task to be run on the main thread
            minecraft.addScheduledTask(task);
        }
    }

//    public void fileStuff() {
//        //dumb code wrote in a tight deadline, TODO reimplement
//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // Your code to run every 1 second goes here
//                String path = "/home/blendy/slimeafk";
//                File file = new File (path);
//                StringBuilder sb = new StringBuilder();
//
//                try (FileInputStream fis = new FileInputStream(file)) {
//                    int content;
//                    while ((content = fis.read()) != -1) {
//                        sb.append((char) content);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                String fileContent = sb.toString();
//                if (fileContent.equals("")) return;
//
//                try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
//                    writer.write("");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (fileContent.toLowerCase().contains("/hotbar")) {
//                    Runnable mainThread = () -> {
//                        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[Integer.parseInt(fileContent.split(" ")[1].substring(0,1))-1].getKeyCode(), true);
//                        Timer b = new Timer();
//                        b.schedule(new TimerTask() {
//                            @Override
//                            public void run(){
//                                KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[Integer.parseInt(fileContent.split(" ")[1].substring(0,1))-1].getKeyCode(), false);
//                            }
//                        }, 250L);
//                    };
//                    runOnMainThread(mainThread);
//                    return;
//                }
//                if (fileContent.toLowerCase().contains("/ss")) {
//                    Runnable mainThread = () -> ScreenshotHandler.takeScreenshot(path+".png");
//                    runOnMainThread(mainThread);
//                    return;
//                }
//                System.out.println(fileContent);
//                Minecraft.getMinecraft().thePlayer.sendChatMessage(fileContent);
//            }
//        }, 0, 300);
//    }
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String msg = message.replaceAll("%msg", event.message.getUnformattedText()); // get the unformatted chat message
        String[] triggers = keywords.toLowerCase().split(";");
        if (Arrays.stream(triggers).anyMatch(msg.toLowerCase()::contains)) {sendDiscordMessage(msg);}

    }

    @SubscribeEvent
    public void onDisconnect(ClientDisconnectionFromServerEvent event) {
        sendDiscordMessage("Disconnected from server.");
    }
    void sendDiscordMessage (String message) {
        Thread thread = new Thread(() -> {
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost("https://discord.com/api/v9/channels/"+ channelID +"/messages");

                // Add headers
                request.addHeader("content-type", "application/json");
                request.addHeader("authorization", "Bot " + token);
                // Add body
                String requestBody = "{\"content\":\"" + message + "\"}";
                StringEntity params = new StringEntity(requestBody);
                request.setEntity(params);

                // Send request
                HttpResponse response = httpClient.execute(request);
                System.out.println(response.getStatusLine().getStatusCode() + "\n" + EntityUtils.toString(response.getEntity()));
            } catch (UnsupportedEncodingException ignored) {} catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (ConfigGUI.KEYBINDING.isKeyDown()) {
            Minecraft.getMinecraft().displayGuiScreen(new ConfigGUI());
        }
        if (BlockDataScanner.KEYBINDING.isKeyDown()) {
            BlockDataScanner.copyBlockData(Minecraft.getMinecraft().thePlayer);
        }
    }






}
