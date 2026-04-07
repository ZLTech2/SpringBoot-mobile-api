package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.PreferenciaNotificacaoRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.swing.*;
import java.util.List;

@Service
public class PreferenciaNotificacaoService {
    private final PreferenciaNotificacaoRepository preferenciaNotificacaoRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    public PreferenciaNotificacaoService(PreferenciaNotificacaoRepository preferenciaNotificacaoRepository, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.preferenciaNotificacaoRepository = preferenciaNotificacaoRepository;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void dispararNotificacoes(ProdutoModel novoProduto){
        // acessa a empresa pelo produto para saber de onde esta vindo
        EmpresaModel empresa = novoProduto.getEmpresa();

        // buscar todas as preferencias salvas
        List<PreferenciaNotificacaoModel> listaPreferenciasSalvas = preferenciaNotificacaoRepository.findAll();

        // "filtro" para saber se a categoria esta no interesse do cliente e o produto esta em promocao
        for (PreferenciaNotificacaoModel pref : listaPreferenciasSalvas){
            boolean interesseCategoria = pref.getCategoriasInteresse().contains(empresa.getCategoriaEmpresa());
            boolean receberPromocao = pref.isReceberQualquerPromo() && novoProduto.isPromocao();

            if (interesseCategoria || receberPromocao){
                double distanciaClienteEmpresa = calcularDistancia(pref.getLatitudeCliente(), pref.getLongitudeCliente(), empresa.getLatitudeEmpresa(), empresa.getLongitudeEmpresa());

                if (distanciaClienteEmpresa <= pref.getRaioMaximoKm()){
                    enviarEmail(pref.getCliente(), novoProduto);

                    break;
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
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("dtayna3@gmail.com");
            helper.setTo(cliente.getEmailCliente());
            helper.setSubject("Nova oferta " + produto.getNomeProduto());

            //contexto
            Context context = new Context();
            context.setVariable("nomeCliente", cliente.getNomeCliente());
            context.setVariable("nomeProduto", produto.getNomeProduto());
            context.setVariable("preco", produto.getPrecoProduto());

            String linkProduto = "http://localhost:3000/produto/" + produto.getIdProduto();
            context.setVariable("linknProduto", linkProduto);

            //processa o template
            String html = templateEngine.process("email-template", context);

            //envia como html
            helper.setText(html, true);
            javaMailSender.send(message);

            System.out.println("Email enviado para: " + cliente.getEmailCliente());

        }catch (Exception e){
            System.out.println("Erro ao enviar email: " + e.getMessage());
        }


    }
}
