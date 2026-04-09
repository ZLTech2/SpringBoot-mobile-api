package com.negocionaarea.mobile_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.negocionaarea.mobile_api.dto.ClienteRequest;
import com.negocionaarea.mobile_api.dto.ClienteResponse;
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

        ClienteModel cliente = new ClienteModel();

        cliente.setNomeCliente(dto.getNomeCliente());
        cliente.setEmail(dto.getEmail());
        cliente.setSenhaCliente(passwordEncoder.encode(dto.getSenhaCliente()));
        cliente.setUrlPerfilCliente(dto.getUrlPerfilCliente());
        cliente.setTelefone(dto.getTelefone());
        cliente.setRoleCliente(Role.CUSTOMER);

        EnderecoModel endereco = new EnderecoModel();
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(Integer.parseInt(dto.getEndereco().getNumero()));
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setCep(dto.getEndereco().getCep());

        cliente.setEndereco(endereco);

        cliente = repository.save(cliente);

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNomeCliente(cliente.getNomeCliente());
        response.setEmail(cliente.getEmail());
        response.setUrlPerfilCliente(cliente.getUrlPerfilCliente());
        response.setTelefone(cliente.getTelefone());
        response.setEndereco(dto.getEndereco());

        return response;
    }


    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(cliente -> {

            ClienteResponse response = new ClienteResponse();
            response.setId(cliente.getId());
            response.setNomeCliente(cliente.getNomeCliente());
            response.setEmail(cliente.getEmail());
            response.setUrlPerfilCliente(cliente.getUrlPerfilCliente());
            response.setTelefone(cliente.getTelefone());

            return response;

        }).collect(Collectors.toList());
    }
}
