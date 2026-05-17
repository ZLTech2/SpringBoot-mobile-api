package com.negocionaarea.mobile_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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

    public String gerarBanner(String nomeProduto, Double precoOriginal, Double precoPromocional, Double porcentagem, String imagemUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // 1. Baixa a imagem do produto do Cloudinary
            HttpRequest imagemRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imagemUrl))
                    .GET()
                    .build();

            HttpResponse<byte[]> imagemResponse = client.send(imagemRequest, HttpResponse.BodyHandlers.ofByteArray());
            byte[] imagemBytes = imagemResponse.body();

            // 2. Monta o prompt
            String prompt = String.format(
                    "Add a bold promotional sale badge to this product image. " +
                            "The badge should show '%.0f%% OFF' in large white text inside a red starburst shape, " +
                            "positioned in the top-right corner. Also show the new price R$%.2f. " +
                            "Keep the product fully visible. Modern e-commerce style.",
                    porcentagem, precoPromocional
            );

            // 3. Envia para OpenAI images/edits como multipart
            String boundary = "----FormBoundary" + System.currentTimeMillis();

            byte[] promptPart = ("--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"prompt\"\r\n\r\n" +
                    prompt + "\r\n").getBytes();

            byte[] modelPart = ("--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"model\"\r\n\r\n" +
                    "gpt-image-1\r\n").getBytes();

            byte[] imageHeader = ("--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"image\"; filename=\"product.png\"\r\n" +
                    "Content-Type: image/png\r\n\r\n").getBytes();

            byte[] imageFooter = ("\r\n--" + boundary + "--\r\n").getBytes();

            byte[] body = new byte[promptPart.length + modelPart.length + imageHeader.length + imagemBytes.length + imageFooter.length];
            System.arraycopy(promptPart, 0, body, 0, promptPart.length);
            System.arraycopy(modelPart, 0, body, promptPart.length, modelPart.length);
            System.arraycopy(imageHeader, 0, body, promptPart.length + modelPart.length, imageHeader.length);
            System.arraycopy(imagemBytes, 0, body, promptPart.length + modelPart.length + imageHeader.length, imagemBytes.length);
            System.arraycopy(imageFooter, 0, body, promptPart.length + modelPart.length + imageHeader.length + imagemBytes.length, imageFooter.length);

            HttpRequest editRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/edits"))
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> editResponse = client.send(editRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println(">>> OPENAI EDIT STATUS: " + editResponse.statusCode());

            // 4. Extrai o base64
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(editResponse.body());
            String base64 = root.path("data").get(0).path("b64_json").asText();
            byte[] imageEdited = Base64.getDecoder().decode(base64);

            // 5. Sobe para o Cloudinary
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", cloudinaryApiKey,
                    "api_secret", cloudinaryApiSecret
            ));

            Map uploadResult = cloudinary.uploader().upload(imageEdited, ObjectUtils.asMap(
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