package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.repository.ProdutoRepository;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoModel create(ProdutoModel novoProduto){
        return produtoRepository.save(novoProduto);
    }

    public List<ProdutoModel> getAll(){
        return produtoRepository.findAll();
    }

    public void delete(Long id){
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Empresa não encontrada"));
        produtoRepository.delete(produto);
    }
    public ProdutoModel getbyId(Long id){
        return produtoRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Produto não encontrado com ID" + id));
    }

    public ProdutoModel updateAll(Long id, ProdutoModel novosDados){
        ProdutoModel produto= produtoRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Produto não encontrado"));
        produto.setNome(novosDados.getNome());
        produto.setPrecoProduto(novosDados.getPrecoProduto());
        produto.setDescricaoProduto(novosDados.getDescricaoProduto());
        produto.setImagem(novosDados.getImagem());
        return produtoRepository.save(produto);
    }

    public ProdutoModel update(Long id, ProdutoModel novosDados){
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Produto não encontrado"));
        if(novosDados.getNome()!=null){
            produto.setNome(novosDados.getNome());
        }
        if(novosDados.getPrecoProduto()!=null){
            produto.setPrecoProduto(novosDados.getPrecoProduto());
        }
        if(novosDados.getDescricaoProduto()!=null){
            produto.setDescricaoProduto(novosDados.getDescricaoProduto());
        }
        if(novosDados.getImagem()!=null){
            produto.setImagem(novosDados.getImagem());
        }
        return produtoRepository.save(produto);
    }
}
