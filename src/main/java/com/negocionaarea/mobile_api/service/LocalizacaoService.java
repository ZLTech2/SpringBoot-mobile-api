package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.PhotonResponse;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@Service
public class LocalizacaoService {

    @Value("${opencage.api.key}")
    private String apikey;
    private final RestTemplate restTemplate;

    public LocalizacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocalizacaoModel buscarCoordenadas(String endereco){
        try {
            System.out.println("======================================");
            System.out.println("📍 BUSCA COM PHOTON");
            System.out.println("📦 Endereço recebido: " + endereco);

            String url = UriComponentsBuilder
                    .fromUriString("https://photon.komoot.io/api/")
                    .queryParam("q", endereco)
                    .queryParam("limit", 5)
                    .build()
                    .toUriString();

            System.out.println("🌐 URL: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("Resposta vazia do Photon");
            }

            ObjectMapper mapper = new ObjectMapper();

            PhotonResponse photon = mapper.readValue(
                    response.getBody(),
                    PhotonResponse.class
            );

            if (photon.getFeatures() == null || photon.getFeatures().isEmpty()) {
                throw new RuntimeException("Nenhum resultado encontrado no Photon");
            }

            // 🔥 SELEÇÃO INTELIGENTE DO MELHOR RESULTADO
            PhotonResponse.Feature melhor = photon.getFeatures().stream()
                    .filter(f -> f.getProperties() != null)
                    .filter(f -> f.getProperties().getCity() != null)
                    .filter(f -> f.getProperties().getCity().equalsIgnoreCase("São Paulo"))
                    .findFirst()
                    .orElse(photon.getFeatures().get(0));

            double[] coords = melhor.getGeometry().getCoordinates();

            System.out.println("======================================");
            System.out.println("📍 RESULTADO ESCOLHIDO:");
            System.out.println("🏙️ Cidade: " + melhor.getProperties().getCity());
            System.out.println("📍 LAT: " + coords[1]);
            System.out.println("📍 LON: " + coords[0]);

            LocalizacaoModel localizacao = new LocalizacaoModel();
            localizacao.setLatitude(coords[1]);
            localizacao.setLongitude(coords[0]);

            return localizacao;

        } catch (Exception e) {
            System.out.println("❌ ERRO DETALHADO:");
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar coordenadas com Photon", e);
        }

    }


    public  String montarEndereco (EnderecoModel endereco){

        String enderecoCompleto = String.format("%s, %s, Brasil",
                valorSeguro(endereco.getRua()),
                valorSeguro(endereco.getCidade())
        );

        System.out.println("📦 Endereço montado: " + enderecoCompleto);

        return enderecoCompleto;
    }

    private String valorSeguro(Object valor){
        return valor != null ? valor.toString() : "";
    }


}
