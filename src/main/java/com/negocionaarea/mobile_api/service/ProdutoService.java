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

    public ProdutoService(EmpresaRepository empresaRepository, ProdutoRepository produtoRepository) {
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponse create(ProdutoCreateRequest request, String empresaEmail) {
        ProdutoModel produto = new ProdutoModel();
        produto.setNome(request.nome());
        produto.setDescricaoProduto(request.descricaoProduto());
        produto.setPrecoProduto(request.precoProduto());
        produto.setImagem(request.imagem());

        EmpresaModel empresa = empresaRepository.findByEmail(empresaEmail)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa autenticada nao encontrada"));
        produto.setEmpresa(empresa);

        return toResponse(produtoRepository.save(produto));
    }

    public List<ProdutoResponse> getAll() {
        return produtoRepository.findAll().stream().map(ProdutoService::toResponse).toList();
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

    private static ProdutoResponse toResponse(ProdutoModel produto) {
        return new ProdutoResponse(
                produto.getIdProduto(),
                produto.getNome(),
                produto.getDescricaoProduto(),
                produto.getPrecoProduto(),
                produto.getDataPostagem(),
                produto.getImagem(),
                produto.getEmpresa() == null ? null : produto.getEmpresa().getId()
        );
    }
}
