package com.negocionaarea.mobile_api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.negocionaarea.mobile_api.model.LocalizacaoModel;
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
    private final LocalizacaoService localizacaoService;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder, LocalizacaoService localizacaoService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.localizacaoService = localizacaoService;
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
        cliente.setEmail(dto.getEmail().trim().toLowerCase());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setUrlPerfil(dto.getUrlPerfil());
        cliente.setTelefone(dto.getTelefone());
        cliente.setRole(Role.CUSTOMER);

        EnderecoModel endereco = new EnderecoModel();
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setEstado(dto.getEndereco().getEstado());

        cliente.setEndereco(endereco);
        //inserindo a latitude e longitude
        String enderecoFormatado = localizacaoService.montarEndereco(cliente.getEndereco());
        LocalizacaoModel localizacao = localizacaoService.buscarCoordenadas(enderecoFormatado);

        cliente.setLocalizacao(localizacao);



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
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
            response.setUrlPerfil(cliente.getUrlPerfil());
            response.setTelefone(cliente.getTelefone());

            return response;

        }).collect(Collectors.toList());
    }
}
