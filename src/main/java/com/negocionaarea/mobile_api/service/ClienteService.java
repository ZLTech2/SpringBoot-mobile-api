package com.negocionaarea.mobile_api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (dto.getDataNascimento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data de nascimento é obrigatória");
        }
        if (dto.getEndereco() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endereco é obrigatorio");
        }

        // validar senha
        validarSenha(dto.getSenha());
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        // validar email
        if(repository.existsByEmail(emailNormalizado)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        ClienteModel cliente = new ClienteModel();

        cliente.setNome(dto.getNome());
        cliente.setEmail(emailNormalizado);
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setTelefone(dto.getTelefone().trim());
        cliente.setRole(Role.CUSTOMER);
        cliente.setDataNascimento(dto.getDataNascimento());


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

        try {
            LocalizacaoModel localizacao = localizacaoService.buscarCoordenadas(enderecoFormatado);
            cliente.setLocalizacao(localizacao);
        }catch (Exception e){
            cliente.setLocalizacao(null);
        }

        try{
            cliente = repository.save(cliente);
        }catch(DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());

        return response;
    }

    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(cliente -> {

            ClienteResponse response = new ClienteResponse();
            response.setId(cliente.getId());
            response.setNome(cliente.getNome());
            response.setEmail(cliente.getEmail());
            response.setTelefone(cliente.getTelefone());

            return response;

        }).collect(Collectors.toList());
    }

    public ClienteResponse getMe(String email){
        ClienteModel cliente = repository.findByEmail(email)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        return response;
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if (!senha.matches(regex)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter no mínimo 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial"
            );
        }
    }

}
