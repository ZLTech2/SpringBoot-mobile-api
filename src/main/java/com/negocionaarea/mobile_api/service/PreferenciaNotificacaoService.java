package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.PreferenciaNotificacaoRequest;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.PreferenciaNotificacaoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class PreferenciaNotificacaoService {
    private final PreferenciaNotificacaoRepository preferenciaNotificacaoRepository;
    private final TemplateEngine templateEngine;
    private final ClienteRepository clienteRepository;
    private final ResendEmailService resendEmailService;

    public PreferenciaNotificacaoService(
            PreferenciaNotificacaoRepository preferenciaNotificacaoRepository,
            TemplateEngine templateEngine,
            ClienteRepository clienteRepository,
            ResendEmailService resendEmailService) {
        this.preferenciaNotificacaoRepository = preferenciaNotificacaoRepository;
        this.templateEngine = templateEngine;
        this.clienteRepository = clienteRepository;
        this.resendEmailService = resendEmailService;
    }

    @Async
    public void dispararNotificacoes(ProdutoModel novoProduto){

        EmpresaModel empresa = novoProduto.getEmpresa();
        //usa a query
        List<PreferenciaNotificacaoModel> preferencias = preferenciaNotificacaoRepository.buscarUsuariosParaNotificacao(empresa.getCategoria(), novoProduto.getEmpresa().getId());

        //percorre para enviar o email
        for (PreferenciaNotificacaoModel pref : preferencias){

            ClienteModel cliente = pref.getCliente();

            enviarEmail(cliente, novoProduto);
            System.out.println("✅ Email enviado para: " + cliente.getEmail());
        }

    }

    public PreferenciaNotificacaoModel salvar (PreferenciaNotificacaoRequest request, String email){
        ClienteModel cliente = clienteRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Cliente não encontrado"));

        PreferenciaNotificacaoModel pref = preferenciaNotificacaoRepository
                .findByCliente_Id(cliente.getId())
                .orElse(new PreferenciaNotificacaoModel());;

        pref.setCliente(cliente);
        pref.setRaioMaximoKm(request.getRaioMaximoKm());
        pref.setCategoriasInteresse(request.getCategoriasInteresse());
        pref.setReceberQualquerPromo(request.isReceberQualquerPromo());

        return preferenciaNotificacaoRepository.save(pref);
    }

    private void enviarEmail(ClienteModel cliente, ProdutoModel produto){
        try {
            //contexto para substituir as variaveis no template
            Context context = new Context();
            context.setVariable("nomeCliente", cliente.getNome());
            context.setVariable("nomeProduto", produto.getNome());
            context.setVariable("preco", produto.getPrecoProduto());

            String linkProduto = "http://localhost:3000/produto/" + produto.getIdProduto();
            context.setVariable("linkProduto", linkProduto);

            //processa o template
            String html = templateEngine.process("email-template", context);

            //envia via Resend
            resendEmailService.enviar(cliente.getEmail(), "Nova oferta: " + produto.getNome(), html);

        } catch (Exception e){
            System.out.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}