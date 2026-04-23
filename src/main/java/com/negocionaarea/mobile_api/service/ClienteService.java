package com.negocionaarea.mobile_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.negocionaarea.mobile_api.dto.ClienteRequest;
import com.negocionaarea.mobile_api.dto.ClienteResponse;
import com.negocionaarea.mobile_api.dto.EnderecoRequest;
import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class ClienteService {
    private static final String DEFAULT_CIDADE = "Sao Paulo";
    private static final String DEFAULT_ESTADO = "SP";

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    public ClienteResponse salvar(ClienteRequest dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload invalido");
        }
        if (dto.getEndereco() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endereco e obrigatorio");
        }

        ClienteModel cliente = new ClienteModel();

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setUrlPerfil(dto.getUrlPerfil());
        cliente.setTelefone(dto.getTelefone());
        cliente.setRole(Role.CUSTOMER);

        EnderecoModel endereco = new EnderecoModel();
        EnderecoRequest enderecoDto = dto.getEndereco();
        endereco.setRua(enderecoDto.getRua());
        endereco.setNumero(enderecoDto.getNumero());
        endereco.setBairro(enderecoDto.getBairro());
        endereco.setCep(enderecoDto.getCep());
        endereco.setCidade(defaultCidade(enderecoDto.getCidade()));
        endereco.setEstado(DEFAULT_ESTADO);

        cliente.setEndereco(endereco);

        cliente = repository.save(cliente);

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setUrlPerfil(cliente.getUrlPerfil());
        response.setTelefone(cliente.getTelefone());

        EnderecoRequest enderecoResp = new EnderecoRequest();
        enderecoResp.setRua(cliente.getEndereco().getRua());
        enderecoResp.setNumero(cliente.getEndereco().getNumero());
        enderecoResp.setBairro(cliente.getEndereco().getBairro());
        enderecoResp.setCidade(cliente.getEndereco().getCidade());
        enderecoResp.setCep(cliente.getEndereco().getCep());
        response.setEndereco(enderecoResp);

        return response;
    }


    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(cliente -> {

            ClienteResponse response = new ClienteResponse();
            response.setId(cliente.getId());
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
            response.setUrlPerfil(cliente.getUrlPerfil());
            response.setTelefone(cliente.getTelefone());

            return response;

        }).collect(Collectors.toList());
    }

    private String defaultCidade(String cidade) {
        if (cidade == null) {
            return DEFAULT_CIDADE;
        }
        String t = cidade.trim();
        return t.isEmpty() ? DEFAULT_CIDADE : t;
    }
}
