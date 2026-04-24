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

        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nome e obrigatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email e obrigatorio");
        }
        if (dto.getSenha() == null || dto.getSenha().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "senha e obrigatoria");
        }
        if (dto.getTelefone() == null || dto.getTelefone().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "telefone e obrigatorio");
        }
        if (dto.getUrlPerfil() == null || dto.getUrlPerfil().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "urlPerfil e obrigatorio");
        }

        ClienteModel cliente = new ClienteModel();

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setCreatedAt(cliente.getCreatedAt());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setUrlPerfil(dto.getUrlPerfil());
        cliente.setTelefone(dto.getTelefone());
        cliente.setRole(Role.CUSTOMER);

        cliente = repository.save(cliente);

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setUrlPerfil(cliente.getUrlPerfil());
        response.setTelefone(cliente.getTelefone());

        return response;
    }


    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(cliente -> {

            ClienteResponse response = new ClienteResponse();
            response.setId(cliente.getId());
            response.setCreatedAt(cliente.getCreatedAt());
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
            response.setUrlPerfil(cliente.getUrlPerfil());
            response.setTelefone(cliente.getTelefone());

            return response;

        }).collect(Collectors.toList());
    }
}
