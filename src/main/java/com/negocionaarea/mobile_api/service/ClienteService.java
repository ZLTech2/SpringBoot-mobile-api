package com.negocionaarea.mobile_api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.negocionaarea.mobile_api.dto.ClienteRequest;
import com.negocionaarea.mobile_api.dto.ClienteResponse;
import com.negocionaarea.mobile_api.dto.EmpresaResponse;
import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizacaoService localizacaoService;
    private final FileStorageService fileStorageService;
    private final CloudinaryService cloudinaryService;
    private ImageModerationService imageModerationService;

    public ClienteService(ClienteRepository repository, PasswordEncoder passwordEncoder, LocalizacaoService localizacaoService, FileStorageService fileStorageService, CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.localizacaoService = localizacaoService;
        this.fileStorageService = fileStorageService;
        this.cloudinaryService = cloudinaryService;
    }

    public ClienteResponse salvar(ClienteRequest dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload invalido");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nome e obrigatorio");
        }
        if (dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cpf e obrigatorio");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data de nascimento e obrigatoria");
        }
        if (dto.getEndereco() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endereco e obrigatorio");
        }

        validarSenha(dto.getSenha());

        String emailNormalizado = dto.getEmail().trim().toLowerCase();
        String cpfNormalizado = normalizarCpf(dto.getCpf());

        if (repository.existsByEmail(emailNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja cadastrado");
        }
        if (repository.existsByCpf(cpfNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF ja cadastrado");
        }

        ClienteModel cliente = new ClienteModel();
        cliente.setNome(dto.getNome().trim());
        cliente.setEmail(emailNormalizado);
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setTelefone(dto.getTelefone().trim());
        cliente.setCpf(cpfNormalizado);
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
        String enderecoFormatado = localizacaoService.montarEndereco(cliente.getEndereco());

        try {
            LocalizacaoModel localizacao = localizacaoService.buscarCoordenadas(enderecoFormatado);
            cliente.setLocalizacao(localizacao);
        } catch (Exception e) {
            cliente.setLocalizacao(null);
        }

        try {
            cliente = repository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ou CPF ja cadastrado");
        }

        return toResponse(cliente);
    }

    public List<ClienteResponse> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponse getMe(String email) {
        ClienteModel cliente = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));
        return toResponse(cliente);
    }

    private ClienteResponse toResponse(ClienteModel cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        response.setCpf(cliente.getCpf());
        response.setUrlPerfil(cliente.getUrlPerfil());
        return response;
    }

    private String normalizarCpf(String cpf) {
        String cpfNormalizado = cpf.replaceAll("\\D", "");
        if (cpfNormalizado.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cpf deve conter 11 digitos");
        }
        return cpfNormalizado;
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha e obrigatoria");
        }
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if (!senha.matches(regex)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter no minimo 8 caracteres, 1 letra maiuscula, 1 numero e 1 caractere especial"
            );
        }
    }

    public ClienteResponse uploadLogo(MultipartFile logo, String email) {
        ClienteModel cliente = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        try{
            if (!imageModerationService.imagemEhApropriada(logo.getBytes())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem rejeitada: conteúdo inapropriado detectado"
                );
            }
        }catch (ResponseStatusException e){
            throw e;
        }catch(Exception e){
            System.out.println("Erro ao verificar imagem: "+ e.getMessage());
        }


        String cloudinaryUrl = cloudinaryService.upload(logo, "logos");
        cliente.setUrlPerfil(cloudinaryUrl);
        cliente = repository.save(cliente);

        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        response.setUrlPerfil(cliente.getUrlPerfil());
        return response;
    }
}
