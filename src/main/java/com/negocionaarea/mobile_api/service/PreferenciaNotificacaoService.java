package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.PreferenciaNotificacaoRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
public class PreferenciaNotificacaoService {
    private final PreferenciaNotificacaoRepository preferenciaNotificacaoRepository;
    private final JavaMailSender javaMailSender;


    public PreferenciaNotificacaoService(PreferenciaNotificacaoRepository preferenciaNotificacaoRepository, JavaMailSender javaMailSender) {
        this.preferenciaNotificacaoRepository = preferenciaNotificacaoRepository;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void dispararNotificacoes(ProdutoModel novoProduto){
        // acessa a empresa pelo produto para saber de onde esta vindo
        EmpresaModel empresa = novoProduto.getEmpresa();

        // buscar todas as preferencias salvas
        List<PreferenciaNotificacaoModel> listaPreferenciasSalvas = preferenciaNotificacaoRepository.findAll();

        // "filtro" para saber se a categoria esta no interesse do cliente e o produto esta em promocao
        for (PreferenciaNotificacaoModel pref : listaPreferenciasSalvas){
            boolean interesseCategoria = pref.getCategoriasInteresse().equals(empresa.getCategoriaEmpresa());
            boolean receberPromocao = pref.isReceberQualquerPromo() && novoProduto.isPromocao();

            if (interesseCategoria || receberPromocao){
                double distanciaClienteEmpresa = calcularDistancia(pref.getLatitudeCliente(), pref.getLongitudeCliente(), empresa.getLatitudeEmpresa(), empresa.getLongitudeEmpresa());

                if (distanciaClienteEmpresa <= pref.getRaioMaximoKm()){
                    enviarEmail(pref.getCliente(), novoProduto);
                }
            }

        }

    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Raio da Terra em quilômetros
        double R = 6371;

        // Conversão de graus para radianos
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Resultado em km
    }

    private void enviarEmail(ClienteModel cliente, ProdutoModel produto){
        var message = new SimpleMailMessage();
        message.setFrom("zltech64@gmail.com");
        message.setTo(cliente.getEmailCliente());
        message.setSubject("Nova oferta: " + produto.getNomeProduto());

        // construção do corpo do email com StringBuilder
        String corpoEmail = new StringBuilder()
                .append("Olá").append(cliente.getNomeCliente()).append("!\n\n")
                .append("Encontramoms um produto do seu interesse bem perto de você:\n")
                .append("Produto: ").append(produto.getNomeProduto()).append("\n")
                .append("Preço: R$ ").append(produto.getPrecoProduto()).append("\n")
                .append("Acesse o app para ver os detalhes do produto e da empresa").toString();

        message.setText(corpoEmail);

        // enviando o email
        javaMailSender.send(message);


    }
}
