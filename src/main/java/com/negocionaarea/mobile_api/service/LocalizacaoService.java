package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.config.RestTemplateConfig;
import com.negocionaarea.mobile_api.dto.NominatimResponse;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
public class LocalizacaoService {
    private final RestTemplate restTemplate;

    public LocalizacaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocalizacaoModel buscarCoordenadas(String endereco){
        String url = "https://nominatim.openstreetmap.org/search?q="
                + endereco.replace(" ", "+")
                + "&format=json&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "negocionaarea-app");

        HttpEntity<String> entity =  new HttpEntity<>(headers);

        ResponseEntity<NominatimResponse[]> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        NominatimResponse[].class
                );

        if(response.getBody() == null || response.getBody().length == 0){
            throw new RuntimeException("Endereço não encontrado");
        }

        NominatimResponse dados = response.getBody()[0];

        LocalizacaoModel localizacao = new LocalizacaoModel();
        localizacao.setLatitude(Double.parseDouble(dados.getLatitude()));
        localizacao.setLongitude(Double.parseDouble(dados.getLongitude()));

        return localizacao;
    }

    public  String montarEndereco (EnderecoModel endereco){
        return String.format("%s, %s, %s, %s, %s",
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep()
        );
    }
}
