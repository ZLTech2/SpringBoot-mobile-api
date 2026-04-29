package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.ProdutoCreateRequest;
import com.negocionaarea.mobile_api.dto.ProdutoResponse;
import com.negocionaarea.mobile_api.dto.ProdutoUpdateRequest;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProdutoService {
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;
    private final FileStorageService fileStorageService;
    private final PreferenciaNotificacaoService preferenciaNotificacao;

    public ProdutoService(EmpresaRepository empresaRepository, ProdutoRepository produtoRepository, FileStorageService fileStorageService, PreferenciaNotificacaoService preferenciaNotificacao) {
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
        this.fileStorageService = fileStorageService;
        this.preferenciaNotificacao = preferenciaNotificacao;
    }

    public ProdutoResponse create(ProdutoCreateRequest request, String empresaEmail) {
        ProdutoModel produto = new ProdutoModel();
        produto.setNome(request.nome());
        produto.setDescricaoProduto(request.descricaoProduto());
        produto.setPrecoProduto(request.precoProduto());
        produto.setImagem(request.imagem()); // pode ser URL/base64; ou null se voce for fazer upload depois

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));
        produto.setEmpresa(empresa);

        produto = produtoRepository.save(produto);

        preferenciaNotificacao.dispararNotificacoes(produto);
        System.out.println();

        return toResponse(produto);
    }

    public List<ProdutoResponse> getAll() {
        return produtoRepository.findAll().stream().map(ProdutoService::toResponse).toList();
    }

    public List<ProdutoResponse>getProdutosByEmpresa(String empresaEmail){
        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(()->new ResponseStatusException(FORBIDDEN, "Empresa não encontrada"));
        return produtoRepository.findByEmpresa(empresa)
                .stream().map(ProdutoService::toResponse).toList();
    }

    public List<ProdutoResponse> getProdutosPorEmpresaId(UUID empresaId) {
        EmpresaModel empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa não encontrada"));
        return produtoRepository.findByEmpresa(empresa)
                .stream().map(ProdutoService::toResponse).toList();
    }


    public ProdutoResponse getbyId(UUID id) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado com ID " + id));
        return toResponse(produto);
    }

    public void delete(UUID id, String empresaEmail) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto nao encontrado com ID " + id));

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));

        if (produto.getEmpresa() == null || !produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Voce nao pode alterar produtos de outra empresa");
        }

        produtoRepository.delete(produto);
    }

    public ProdutoResponse updateAll(UUID id, ProdutoUpdateRequest request, String empresaEmail) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto nao encontrado com ID " + id));

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));

        if (produto.getEmpresa() == null || !produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Voce nao pode alterar produtos de outra empresa");
        }

        produto.setNome(request.nome());
        produto.setPrecoProduto(request.precoProduto());
        produto.setDescricaoProduto(request.descricaoProduto());
        produto.setImagem(request.imagem());

        return toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse update(UUID id, ProdutoUpdateRequest request, String empresaEmail) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto nao encontrado com ID " + id));

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));

        if (produto.getEmpresa() == null || !produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Voce nao pode alterar produtos de outra empresa");
        }

        if (request.nome() != null) {
            produto.setNome(request.nome());
        }
        if (request.precoProduto() != null) {
            produto.setPrecoProduto(request.precoProduto());
        }
        if (request.descricaoProduto() != null) {
            produto.setDescricaoProduto(request.descricaoProduto());
        }
        if (request.imagem() != null) {
            produto.setImagem(request.imagem());
        }

        return toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse uploadImagem(UUID id, org.springframework.web.multipart.MultipartFile imagem, String empresaEmail) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Produto nao encontrado com ID " + id));

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));

        if (produto.getEmpresa() == null || !produto.getEmpresa().getId().equals(empresa.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Voce nao pode alterar produtos de outra empresa");
        }

        String oldPublicPath = produto.getImagem();
        var stored = fileStorageService.storeProdutoImagem(produto.getIdProduto(), imagem);

        produto.setImagem(stored.publicPath());
        ProdutoModel saved = produtoRepository.save(produto);

        // Best-effort cleanup if we're replacing a previous local upload
        if (oldPublicPath != null && !oldPublicPath.equals(saved.getImagem())) {
            fileStorageService.tryDeleteIfUnderUploads(oldPublicPath);
        }

        return toResponse(saved);
    }

    private static ProdutoResponse toResponse(ProdutoModel produto) {
        return new ProdutoResponse(
                produto.getIdProduto(),
                produto.getNome(),
                produto.getDescricaoProduto(),
                produto.getPrecoProduto(),
                produto.getDataPostagem(),
                produto.getImagem(),
                produto.getEmpresa() == null ? null : produto.getEmpresa().getId(),
                produto.getEmpresa() == null ? null : produto.getEmpresa().getNome()
        );
    }
}
