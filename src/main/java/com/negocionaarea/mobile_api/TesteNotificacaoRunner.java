package com.negocionaarea.mobile_api;

import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.PreferenciaNotificacaoModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.repository.PreferenciaNotificacaoRepository;
import com.negocionaarea.mobile_api.repository.ProdutoRepository;
import com.negocionaarea.mobile_api.service.PreferenciaNotificacaoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TesteNotificacaoRunner implements CommandLineRunner {
    private final PreferenciaNotificacaoService notificacaoService;
    private final PreferenciaNotificacaoRepository repository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;

    public TesteNotificacaoRunner(PreferenciaNotificacaoService notificacaoService, PreferenciaNotificacaoRepository repository, ClienteRepository clienteRepository, EmpresaRepository empresaRepository, ProdutoRepository produtoRepository) {
        this.notificacaoService = notificacaoService;
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("======= INICIANDO SETUP DE TESTE DE NOTIFICAÇÃO =======");

        // 1. CRIAR UM CLIENTE FICTÍCIO COM PREFERÊNCIAS
        ClienteModel cliente = new ClienteModel();
        cliente.setNomeCliente("Maria");
        cliente.setEmailCliente("taynasantos20191@outlook.com");// Coloque o e-mail que quer ver no Mailtrap
        cliente.setSenhaCliente("123");
        cliente.setUrlPerfilCliente("www.iushuivhbsuij");
        clienteRepository.save(cliente);

        PreferenciaNotificacaoModel pref = new PreferenciaNotificacaoModel();
        pref.setCliente(cliente);
        pref.setLatitudeCliente(-23.550520); // Coordenada de São Paulo (Exemplo)
        pref.setLongitudeCliente(-46.633308);
        pref.setRaioMaximoKm(10.0); // Raio de 10km
        pref.setReceberQualquerPromo(true);
        // Ajuste conforme o tipo do seu atributo (List ou String)
        pref.setCategoriasInteresse(Arrays.asList("Eletronicos", "Vestuario"));

        // Salva a preferência (e o cliente por causa do CascadeType.ALL)
        repository.save(pref);

        // 2. CRIAR UMA EMPRESA FICTÍCIA (Perto do cliente)
        EmpresaModel empresa = new EmpresaModel();
        empresa.setNomeEmpresa("Loja de Teste Tech");
        empresa.setCnpjEmpresa("12345678000199"); // 14 dígitos
        empresa.setCategoriaEmpresa("Eletronicos");

// Coordenadas (Latitude e Longitude)
        empresa.setLatitudeEmpresa(-23.550520);
        empresa.setLongitudeEmpresa(-46.633308);

// Contatos e Acesso
        empresa.setContato("(11) 99999-9999");
        empresa.setEmailEmpresa("empresa@gmail.com");
        empresa.setSenhaEmpesa("senha123"); // Em prod use BCrypt!

// URLs de Imagem
        empresa.setUrlPerfilEmpresa("https://via.placeholder.com");
        empresa.setUrlCapaEmpresa("https://via.placeholder.com");
        empresaRepository.save(empresa);


        // 3. CRIAR UM PRODUTO NOVO (O gatilho)
        ProdutoModel novoProduto = new ProdutoModel();
        novoProduto.setNomeProduto("Smartphone Top de Linha");
        novoProduto.setDescricaoProduto("Processador octa-core, 128GB de memória e tela 120Hz.");
        novoProduto.setPrecoProduto(2500.00);
        novoProduto.setPromocao(true);
        novoProduto.setEmpresa(empresa);

        produtoRepository.save(novoProduto);

        System.out.println(">>> Disparando lógica de notificações...");

        // 4. CHAMAR SUA LÓGICA
        notificacaoService.dispararNotificacoes(novoProduto);

        System.out.println("======= TESTE DISPARADO! VERIFIQUE O GMAIL =======");
    }
}
