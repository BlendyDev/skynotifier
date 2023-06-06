package me.blendy.skynotifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ConfigButton extends GuiButton {
    public ConfigButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        // Draw the button background and text
        super.drawButton(mc, mouseX, mouseY);
        // Add any custom rendering for the button here
    }

}
