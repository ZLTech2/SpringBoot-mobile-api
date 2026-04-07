package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.ProdutoCreateRequest;
import com.negocionaarea.mobile_api.dto.ProdutoResponse;
import com.negocionaarea.mobile_api.dto.ProdutoUpdateRequest;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;

    public ProdutoService(EmpresaRepository empresaRepository, ProdutoRepository produtoRepository) {
        this.empresaRepository = empresaRepository;
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponse create(ProdutoCreateRequest request) {
        ProdutoModel produto = new ProdutoModel();
        produto.setNome(request.nome());
        produto.setDescricaoProduto(request.descricaoProduto());
        produto.setPrecoProduto(request.precoProduto());
        produto.setImagem(request.imagem());

        if (request.empresaId() != null) {
            EmpresaModel empresa = empresaRepository.findById(request.empresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada com ID " + request.empresaId()));
            produto.setEmpresa(empresa);
        }

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

    public void delete(UUID id) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado com ID " + id));
        produtoRepository.delete(produto);
    }

    public ProdutoResponse updateAll(UUID id, ProdutoUpdateRequest request) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado com ID " + id));

        produto.setNome(request.nome());
        produto.setPrecoProduto(request.precoProduto());
        produto.setDescricaoProduto(request.descricaoProduto());
        produto.setImagem(request.imagem());

        if (request.empresaId() != null) {
            EmpresaModel empresa = empresaRepository.findById(request.empresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada com ID " + request.empresaId()));
            produto.setEmpresa(empresa);
        } else {
            produto.setEmpresa(null);
        }

        return toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse update(UUID id, ProdutoUpdateRequest request) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado com ID " + id));

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
        if (request.empresaId() != null) {
            EmpresaModel empresa = empresaRepository.findById(request.empresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada com ID " + request.empresaId()));
            produto.setEmpresa(empresa);
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
