package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.EmpresaRequest;
import com.negocionaarea.mobile_api.dto.EmpresaResponse;
import com.negocionaarea.mobile_api.dto.EnderecoResponse;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.dto.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizacaoService localizacaoService;

    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, LocalizacaoService localizacaoService) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.localizacaoService = localizacaoService;
    }

    public EmpresaResponse create(EmpresaRequest dto) {
        EmpresaModel empresa = new EmpresaModel();
        // ACRESCENTANDO AS VALIDAÇÕES:
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new RuntimeException("Este CNPJ já está sendo usado!");
        }

        if (empresaRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Este Email já está sendo usado!");
        }

        //Transferindo os dados
        empresa.setNome(dto.getNome());
        empresa.setCnpj(dto.getCnpj());
        empresa.setCategoria(dto.getCategoria());
        empresa.setTelefone(dto.getTelefone());
        empresa.setDescricao(dto.getDescricao());
        empresa.setEmail(dto.getEmail());
        empresa.setSenha(passwordEncoder.encode(dto.getSenha()));
        empresa.setRoleEmpresa(Role.ENTERPRISE);

        EnderecoModel endereco = new EnderecoModel();
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setEstado(dto.getEndereco().getEstado());

        empresa.setEndereco(endereco);

        //inserindo a latitude e longitude
        String enderecoFormatado = localizacaoService.montarEndereco(empresa.getEndereco());
        LocalizacaoModel localizacao = localizacaoService.buscarCoordenadas(enderecoFormatado);

        empresa.setLocalizacao(localizacao);


        //salvando
        empresa = empresaRepository.save(empresa);

        //Response
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setNome(empresa.getNome());
        response.setCnpj(empresa.getCnpj());
        response.setCategoria(empresa.getCategoria());
        response.setTelefone(empresa.getTelefone());
        response.setEmail(empresa.getEmail());
        response.setDescricao(empresa.getDescricao());

        EnderecoResponse enderecoResponse = new EnderecoResponse();
        enderecoResponse.setRua(empresa.getEndereco().getRua());
        enderecoResponse.setBairro(empresa.getEndereco().getBairro());
        enderecoResponse.setCidade(empresa.getEndereco().getCidade());
        enderecoResponse.setCep(empresa.getEndereco().getCep());
        enderecoResponse.setNumero(empresa.getEndereco().getNumero());

        response.setEndereco(enderecoResponse);

        return response;

    }

    public List<EmpresaResponse> findAll() {
        return empresaRepository.findAll()
                .stream()
                .map(empresa -> {
                    EmpresaResponse response = new EmpresaResponse();
                    response.setId(empresa.getId());
                    response.setNome(empresa.getNome());
                    response.setTelefone(empresa.getTelefone());
                    response.setCnpj(empresa.getCnpj());
                    response.setDescricao(empresa.getDescricao());
                    response.setCategoria(empresa.getCategoria());

                    return response;
                })
                .collect(Collectors.toList());
    }
}
