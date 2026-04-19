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
        EmpresaModel empresa = novoProduto.getEmpresa();
        //usa a query
        List<PreferenciaNotificacaoModel> preferencias = preferenciaNotificacaoRepository.buscarUsuariosParaNotificacao(empresa.getCategoria(), novoProduto.getEmpresa().getId());

        //percorre para enviar o email
        for (PreferenciaNotificacaoModel pref : preferencias){
            ClienteModel cliente = pref.getCliente();

            if(!novoProduto.isPromocao() && pref.isReceberQualquerPromo()){
                continue;
            }

            enviarEmail(cliente, novoProduto);
        }

    }

    private void enviarEmail(ClienteModel cliente, ProdutoModel produto){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("dtayna3@gmail.com");
            helper.setTo(cliente.getEmail());
            helper.setSubject("Nova oferta " + produto.getNome());

            //contexto
            Context context = new Context();
            context.setVariable("nomeCliente", cliente.getNome());
            context.setVariable("nomeProduto", produto.getNome());
            context.setVariable("preco", produto.getPrecoProduto());

            String linkProduto = "http://localhost:3000/produto/" + produto.getIdProduto();
            context.setVariable("linkProduto", linkProduto);

            //processa o template
            String html = templateEngine.process("email-template", context);

            //envia como html
            helper.setText(html, true);
            helper.addInline("logoEmpresa", new ClassPathResource("images/logoApp.png"));
            ClassPathResource resource = new ClassPathResource("images/logoApp.png");
            System.out.println(resource.getPath());
            System.out.println("Imagem existe? " + resource.exists());

            javaMailSender.send(message);

            System.out.println("Email enviado para: " + cliente.getEmail());

        }catch (Exception e){
            System.out.println("Erro ao enviar email: " + e.getMessage());
        }


    }

}


