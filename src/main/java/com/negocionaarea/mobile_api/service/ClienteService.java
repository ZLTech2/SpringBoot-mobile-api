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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CupomService cupomService;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder, CupomService cupomService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.cupomService = cupomService;
    }


    public ClienteResponse salvar(ClienteRequest dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload invalido");
        }
        if (dto.getDataNascimento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataNascimento e obrigatorio");
        }

        ClienteModel cliente = new ClienteModel();

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail() == null ? null : dto.getEmail().trim().toLowerCase());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setUrlPerfil(dto.getUrlPerfil());
        cliente.setTelefone(dto.getTelefone());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setRole(Role.CUSTOMER);

        EnderecoModel endereco = new EnderecoModel();
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setCep(dto.getEndereco().getCep());

        cliente.setEndereco(endereco);

        cliente = repository.save(cliente);

        // Se o cadastro acontecer no dia do aniversario, emite o cupom na hora (nao depende do job diario).
        cupomService.emitirCupomAniversarioParaClienteSeElegivel(cliente, java.time.LocalDate.now());

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setUrlPerfil(cliente.getUrlPerfil());
        response.setTelefone(cliente.getTelefone());
        response.setDataNascimento(cliente.getDataNascimento());
        response.setEndereco(dto.getEndereco());

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
            response.setDataNascimento(cliente.getDataNascimento());

            return response;

        }).collect(Collectors.toList());
    }
}
