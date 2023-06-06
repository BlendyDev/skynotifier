package me.blendy.skynotifier;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

public class ScreenshotHandler {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void takeScreenshot(String path) {
        File screenshotsDir = new File(mc.mcDataDir, "screenshots");
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdir();
        }

        String screenshotName = "screenshot-" + System.currentTimeMillis() + ".png";
        File screenshotFile = new File(screenshotsDir, screenshotName);

        try {
            int width = mc.displayWidth;
            int height = mc.displayHeight;
            IntBuffer pixels = BufferUtils.createIntBuffer(width * height);
            int pixelCount = width * height;

            Framebuffer framebuffer = mc.getFramebuffer();
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < pixelCount; i++) {
                int pixel = pixels.get(i);
                int j = i % width;
                int k = i / width;
                pixel = (pixel & 0xFF00FF00) | ((pixel & 0x00FF0000) >> 16) | ((pixel & 0x000000FF) << 16);
                image.setRGB(j, height - k - 1, pixel);
            }

            ImageIO.write(image, "png", screenshotFile);
            Path newFile = Paths.get(path);
            if (Files.exists(newFile)) Files.delete(newFile);
            Files.copy(screenshotFile.toPath(), newFile);
            DiscordCDNUploader.sendScreenshot(path);
            System.out.println("Screenshot saved as " + screenshotFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}