package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.PreferenciaNotificacaoRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
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
            boolean interesseCategoria = pref.getCategoriasInteresse().contains(empresa.getCategoria());
            boolean receberPromocao = pref.isReceberQualquerPromo() && novoProduto.isPromocao();

            }

    }

}


