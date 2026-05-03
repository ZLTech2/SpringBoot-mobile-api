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
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(
                            com.google.api.gax.core.FixedCredentialsProvider.create(
                                    ServiceAccountCredentials.fromStream(
                                            getClass().getResourceAsStream("/google-credentials.json")
                                    )
                            )
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

                // POSSIBLE = 2, LIKELY = 3, VERY_LIKELY = 4
                // bloqueia a partir de LIKELY (3)
                boolean adulto = safeSearch.getAdult().getNumber() >= 3;
                boolean violencia = safeSearch.getViolence().getNumber() >= 3;
                boolean sensual = safeSearch.getRacy().getNumber() >= 3;

                return !adulto && !violencia && !sensual;
            }

        } catch (Exception e) {
            System.out.println("Erro na moderação de imagem: " + e.getMessage());
            // se der erro na API, deixa passar para não bloquear o upload
            return true;
        }
    }
}
