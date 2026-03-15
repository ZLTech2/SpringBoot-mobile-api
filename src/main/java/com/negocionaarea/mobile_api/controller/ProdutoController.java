package com.negocionaarea.mobile_api.controller;
import com.negocionaarea.mobile_api.model.ProdutoModel;
import com.negocionaarea.mobile_api.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProdutoModel> create(@RequestBody ProdutoModel novoProduto){
        ProdutoModel produto = produtoService.create(novoProduto);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/findall")
    public ResponseEntity <List<ProdutoModel>> getAll(){
        List<ProdutoModel> produtos = produtoService.getAll();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoModel> getById(@PathVariable Long id) {
        ProdutoModel produto = produtoService.getbyId(id);
        return ResponseEntity.ok(produto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoModel> updateAll(@PathVariable Long id, @RequestBody ProdutoModel novosDados){
        ProdutoModel atualizado = produtoService.updateAll(id, novosDados);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoModel> update(@PathVariable Long id, @RequestBody ProdutoModel novosDados){
        ProdutoModel atualizado = produtoService.update(id, novosDados);
        return ResponseEntity.ok(atualizado);
    }
}
