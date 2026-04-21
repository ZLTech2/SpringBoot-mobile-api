package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.OpenCageResponse;
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
            System.out.println("📍 BUSCA COM OPENCAGE");
            System.out.println("📦 Endereço: " + endereco);

            String baseUrl = "https://api.opencagedata.com/geocode/v1/json";

            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("q", endereco)
                    .queryParam("key", apikey)
                    .queryParam("limit", 1)
                    .build()
                    .encode()
                    .toUriString();

            System.out.println("🌐 URL: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<OpenCageResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            OpenCageResponse.class
                    );

            if (response.getBody() == null ||
                    response.getBody().getResults() == null ||
                    response.getBody().getResults().isEmpty()) {

                throw new RuntimeException("Endereço não encontrado");
            }

            Double latitude = response.getBody()
                    .getResults()
                    .get(0)
                    .getGeometry()
                    .getLat();

            Double longitude = response.getBody()
                    .getResults()
                    .get(0)
                    .getGeometry()
                    .getLng();

            System.out.println("📍 LAT: " + latitude);
            System.out.println("📍 LON: " + longitude);

            LocalizacaoModel localizacao = new LocalizacaoModel();
            localizacao.setLatitude(latitude);
            localizacao.setLongitude(longitude);

            return localizacao;

        } catch (Exception e) {
            System.out.println("❌ Erro detalhado: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar coordenadas", e);
        }
    }


    public  String montarEndereco (EnderecoModel endereco){

        String enderecoCompleto = String.format("%s, %s, Brasil",
                valorSeguro(endereco.getRua()).trim(),
                valorSeguro(endereco.getCidade()).trim()
        );

        System.out.println("📦 Endereço montado: " + enderecoCompleto);

        return enderecoCompleto;
    }

    private String valorSeguro(Object valor){
        return valor != null ? valor.toString() : "";
    }


}
