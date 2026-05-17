package com.negocionaarea.mobile_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class BannerService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api-secret}")
    private String cloudinaryApiSecret;

    public String gerarBanner(String nomeProduto, Double precoOriginal, Double precoPromocional, Double porcentagem) {
        try {
            // 1. Gera imagem na OpenAI
            String prompt = String.format(
                    "Promotional banner for a product called '%s'. Original price R$%.2f, now R$%.2f, %.0f%% off. Modern style, vibrant colors, clean design.",
                    nomeProduto, precoOriginal, precoPromocional, porcentagem
            );

            String openAiBody = String.format("""
                {
                    "model": "gpt-image-1",
                    "prompt": "%s",
                    "n": 1,
                    "size": "1024x1024"
                }
                """, prompt);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest openAiRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/generations"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(openAiBody))
                    .build();

            HttpResponse<String> openAiResponse = client.send(openAiRequest, HttpResponse.BodyHandlers.ofString());
            String openAiResponseBody = openAiResponse.body();

            // 2. Extrai o base64
            int b64Start = openAiResponseBody.indexOf("\"b64_json\":\"") + 12;
            int b64End = openAiResponseBody.indexOf("\"", b64Start);
            String base64 = openAiResponseBody.substring(b64Start, b64End);

            // 3. Envia para o Cloudinary
            String dataUri = "data:image/png;base64," + base64;
            String cloudinaryBody = "file=" + URLEncoder.encode(dataUri, StandardCharsets.UTF_8)
                    + "&upload_preset=negocionaarea";

            HttpRequest cloudinaryRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(cloudinaryBody))
                    .build();

            HttpResponse<String> cloudinaryResponse = client.send(cloudinaryRequest, HttpResponse.BodyHandlers.ofString());
            String cloudinaryResponseBody = cloudinaryResponse.body();

            System.out.println(">>> CLOUDINARY STATUS: " + cloudinaryResponse.statusCode());
            System.out.println(">>> CLOUDINARY RESPOSTA: " + cloudinaryResponseBody);

            // 4. Extrai a URL pública
            int urlStart = cloudinaryResponseBody.indexOf("\"secure_url\":\"") + 14;
            int urlEnd = cloudinaryResponseBody.indexOf("\"", urlStart);
            return cloudinaryResponseBody.substring(urlStart, urlEnd);

        } catch (Exception e) {
            System.out.println(">>> ERRO AO GERAR BANNER: " + e.getMessage());
            return null;
        }
    }
}