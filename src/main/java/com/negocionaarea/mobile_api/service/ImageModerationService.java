package com.negocionaarea.mobile_api.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.stereotype.Service;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.util.List;

@Service
public class ImageModerationService {
    public boolean imagemEhApropriada(byte[] imagemBytes) {
        try {
            com.google.auth.oauth2.GoogleCredentials credentials;

            String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
            if (credentialsJson != null && !credentialsJson.isBlank()) {
                System.out.println("✅ Usando credenciais da variável de ambiente");
                credentials = ServiceAccountCredentials.fromStream(
                        new java.io.ByteArrayInputStream(credentialsJson.getBytes())
                );
            } else {
                System.out.println("✅ Usando credenciais do arquivo local");
                credentials = ServiceAccountCredentials.fromStream(
                        getClass().getResourceAsStream("/google-credentials.json")
                );
            }

            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(
                            com.google.api.gax.core.FixedCredentialsProvider.create(credentials)
                    )
                    .build();

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
                Image image = Image.newBuilder()
                        .setContent(ByteString.copyFrom(imagemBytes))
                        .build();

                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder()
                                .setType(Feature.Type.SAFE_SEARCH_DETECTION))
                        .setImage(image)
                        .build();

                BatchAnnotateImagesResponse response = client
                        .batchAnnotateImages(List.of(request));

                SafeSearchAnnotation safeSearch = response
                        .getResponses(0)
                        .getSafeSearchAnnotation();

                boolean adulto = safeSearch.getAdult().getNumber() >= 3;
                boolean violencia = safeSearch.getViolence().getNumber() >= 3;
                boolean sensual = safeSearch.getRacy().getNumber() >= 3;

                return !adulto && !violencia && !sensual;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        }
}
