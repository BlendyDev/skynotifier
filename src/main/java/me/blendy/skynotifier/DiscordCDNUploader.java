package me.blendy.skynotifier;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class DiscordCDNUploader {
    private static final String DISCORD_API_URL = "https://discord.com/api/v9";
    private static final String CHANNEL_ID = "1076656732471824466"; // Replace with your Discord channel ID

    public static void sendScreenshot(String fileName) throws IOException {
        URL url = new URL(DISCORD_API_URL + "/channels/" + CHANNEL_ID + "/messages");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "your-user-agent-here");
        connection.setRequestProperty("Authorization", "Bot " + SkyNotifier.token);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=------------------------abcdef1234567890");

        String boundary = "------------------------abcdef1234567890";
        String attachmentName = "file.png";

        // Load the file contents into a byte array
        byte[] fileContent = Files.readAllBytes(Paths.get(fileName));

        // Create the request body
        String requestBody = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"payload_json\"\r\n"
                + "\r\n"
                + "{\r\n"
                + "  \"content\": \"Screenshot:\",\r\n"
                + "  \"tts\": false,\r\n"
                + "  \"embeds\": [\r\n"
                + "    {\r\n"
                + "      \"image\": {\r\n"
                + "        \"url\": \"attachment://" + attachmentName + "\"\r\n"
                + "      }\r\n"
                + "    }\r\n"
                + "  ]\r\n"
                + "}\r\n"
                + "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + attachmentName + "\"\r\n"
                + "Content-Type: image/png\r\n"
                + "Content-Transfer-Encoding: base64\r\n"
                + "\r\n"
                + Base64.getEncoder().encodeToString(fileContent) + "\r\n"
                + "--" + boundary + "--\r\n";

        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(requestBody);
        writer.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            System.out.println("Failed to upload file to Discord CDN. Response code: " + responseCode);
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println(errorLine);
                }
            }
        } else {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String responseLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responseLine = reader.readLine()) != null) {
                responseBuilder.append(responseLine);
            }
            System.out.println("Successfully uploaded file to Discord CDN. Response: " + responseBuilder.toString());
        }

        writer.close();
    }
}
