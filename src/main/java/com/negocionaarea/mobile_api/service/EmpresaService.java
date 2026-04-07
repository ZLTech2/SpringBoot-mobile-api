package com.negocionaarea.mobile_api.service; // Verifique se este nome de pacote bate com a pasta

import com.negocionaarea.mobile_api.dto.EmpresaCreateRequest;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EmpresaModel create(EmpresaCreateRequest request) {
        // ACRESCENTANDO AS VALIDAÇÕES:
        if (empresaRepository.existsByCnpj(request.getCnpj())) {
            throw new RuntimeException("Este CNPJ já está sendo usado!");
        }

        if (empresaRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este Email já está sendo usado!");
        }

        // MANTENDO O QUE JÁ EXISTIA:

        EmpresaModel  empresa = new EmpresaModel();
        empresa.setNome(request.getNome());
        empresa.setEmail(request.getEmail());
        empresa.setCnpj(request.getCnpj());
        empresa.setCategoria(request.getCategoria());
        empresa.setTelefone(request.getTelefone());
        empresa.setDescricao(request.getDescricao());
        empresa.setSenha(passwordEncoder.encode(request.getSenha()));

        EnderecoModel endereco = new EnderecoModel();
        endereco.setCep(request.getEndereco().getCep());
        endereco.setRua(request.getEndereco().getRua());
        endereco.setBairro(request.getEndereco().getBairro());
        endereco.setNumero(request.getEndereco().getNumero());
        endereco.setCidade(request.getEndereco().getCidade());
        endereco.setEstado(request.getEndereco().getEstado());

        empresa.setEndereco(endereco);

        return empresaRepository.save(empresa);
    }

    public List<EmpresaModel> findAll() {
        return empresaRepository.findAll();
    }
}