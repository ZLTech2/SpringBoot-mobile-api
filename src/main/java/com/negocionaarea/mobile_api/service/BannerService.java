package com.negocionaarea.mobile_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;

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

            // 2. Extrai o base64
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(openAiResponse.body());
            String base64 = root.path("data").get(0).path("b64_json").asText();
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            // 3. Envia para o Cloudinary via SDK
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", cloudinaryApiKey,
                    "api_secret", cloudinaryApiSecret
            ));

            Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                    "folder", "banners"
            ));

            String url = (String) uploadResult.get("secure_url");
            System.out.println(">>> BANNER URL: " + url);
            return url;

        } catch (Exception e) {
            System.out.println(">>> ERRO AO GERAR BANNER: " + e.getMessage());
            return null;
        }
    }
}