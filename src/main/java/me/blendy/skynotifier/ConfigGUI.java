package me.blendy.skynotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGUI extends GuiScreen {
    private static final int GUI_ID = 1;
    public static final KeyBinding KEYBINDING = new KeyBinding("Open GUI", Keyboard.KEY_N, "Skynotifier");

    private ConfigButton exitButton;

    GuiTextField channelIDField;
    GuiTextField tokenField;
    GuiTextField keywordsField;
    GuiTextField messageField;

    @Override
    public void initGui() {
        //Start
        super.initGui();

        channelIDField = new GuiTextField(0, fontRendererObj, width -200, height / 2 - 85, 200, 20);
        channelIDField.setMaxStringLength(19); // Set the maximum number of characters
        channelIDField.setFocused(true); // Set the focus to the text field
        channelIDField.setText(SkyNotifier.channelID); // Set the default text
        tokenField = new GuiTextField(0, fontRendererObj, width -200, height / 2 - 50, 200, 20);
        tokenField.setMaxStringLength(90);
        tokenField.setText(SkyNotifier.token.isEmpty() ? "" : "*****"); // Set the default text
        keywordsField = new GuiTextField(0, fontRendererObj, width -200, height / 2 - 15, 200, 20);
        keywordsField.setMaxStringLength(500);
        keywordsField.setText(SkyNotifier.keywords);
        messageField = new GuiTextField(0, fontRendererObj, width -200, height / 2 + 20, 200, 20);
        messageField.setMaxStringLength(500);
        messageField.setText(SkyNotifier.message);
        // Add the text field to the button list so it can receive updates and be rendered
        exitButton = new ConfigButton(0, this.width/2-100 , this.height - 20,   "Save");
        this.buttonList.add(exitButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        // Pass the key typed event to the text field so it can handle input
        channelIDField.textboxKeyTyped(typedChar, keyCode);
        tokenField.textboxKeyTyped(typedChar, keyCode);
        keywordsField.textboxKeyTyped(typedChar, keyCode);
        messageField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        // Pass the mouse click event to the text field so it can handle input
        channelIDField.mouseClicked(mouseX, mouseY, mouseButton);
        tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        keywordsField.mouseClicked(mouseX, mouseY, mouseButton);
        messageField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    @Override
    public void actionPerformed(GuiButton button) {
        if (button == exitButton) {

            SkyNotifier.channelID = channelIDField.getText();
            if (!tokenField.getText().equals("*****")) SkyNotifier.token = tokenField.getText();
            SkyNotifier.keywords = keywordsField.getText();
            SkyNotifier.message = messageField.getText();
            Minecraft.getMinecraft().thePlayer.closeScreen();
            saveConfig(SkyNotifier.channelID, SkyNotifier.token, SkyNotifier.keywords, SkyNotifier.message);
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Update

        this.drawGradientRect(0, 0, this.width, this.height, 0x55000000, 0x55000000);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRendererObj.drawString("Channel ID", 0, height / 2 - 85, 0xFFFFFF);
        this.fontRendererObj.drawString("Bot token", 0, height / 2 - 50, 0xFFFFFF);
        this.fontRendererObj.drawString("Keywords (use \";\" to separate)", 0, height / 2 - 15, 0xFFFFFF);
        this.fontRendererObj.drawString("Message", 0, height / 2 + 20, 0xFFFFFF);
        channelIDField.drawTextBox();
        tokenField.drawTextBox();
        keywordsField.drawTextBox();
        messageField.drawTextBox();

    }

    public void saveConfig (String channelID, String token, String keywords, String message) {
        String generalCategory = "general";
        ConfigHandler.config.get(generalCategory, "channelID", "0").set(channelID);
        ConfigHandler.config.get(generalCategory, "token", "").set(token);
        ConfigHandler.config.get(generalCategory, "keywords", "").set(keywords);
        ConfigHandler.config.get(generalCategory, "message", "").set(message);
        ConfigHandler.config.save();
    }



}
