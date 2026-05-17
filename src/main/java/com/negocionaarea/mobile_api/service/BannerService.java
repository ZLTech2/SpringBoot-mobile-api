package com.negocionaarea.mobile_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class BannerService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String gerarBanner(String nomeProduto, Double precoOriginal, Double precoPromocional, Double porcentagem) {
        try {
            String prompt = String.format(
                    "Promotional banner for a product called '%s'. Original price R$%.2f, now R$%.2f, %.0f%% off. Modern style, vibrant colors, clean design.",
                    nomeProduto, precoOriginal, precoPromocional, porcentagem
            );

            String body = String.format("""
                {
                    "model": "gpt-image-1",
                    "prompt": "%s",
                    "n": 1,
                    "size": "1024x1024",
                    "response_format": "url"
                }
                """, prompt);

            System.out.println(">>> CHAMANDO OPENAI COM PROMPT: " + prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/generations"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(">>> STATUS OPENAI: " + response.statusCode());
            System.out.println(">>> RESPOSTA OPENAI: " + response.body());

            // Extrai a URL da resposta JSON
            String responseBody = response.body();
            int urlStart = responseBody.indexOf("\"url\":\"") + 7;
            int urlEnd = responseBody.indexOf("\"", urlStart);
            return responseBody.substring(urlStart, urlEnd);

        } catch (Exception e) {
            System.out.println(">>>ERRO AO GERAR BANNER: " + e.getMessage());
            return null;
        }
    }
}