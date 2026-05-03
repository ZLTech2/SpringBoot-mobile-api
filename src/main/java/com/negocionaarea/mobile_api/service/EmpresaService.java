package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.dto.*;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.model.EnderecoModel;
import com.negocionaarea.mobile_api.model.LocalizacaoModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmpresaService {
    private static final String DEFAULT_CIDADE = "Sao Paulo";
    private static final String DEFAULT_ESTADO = "SP";

    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizacaoService localizacaoService;
    private final FileStorageService fileStorageService;
    private ImageModerationService imageModerationService;

    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, LocalizacaoService localizacaoService, FileStorageService fileStorageService, ImageModerationService imageModerationService) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
        this.localizacaoService = localizacaoService;
        this.fileStorageService = fileStorageService;
        this.imageModerationService = imageModerationService;
    }

    private EmpresaResponse toResponse(EmpresaModel empresa) {
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setCreatedAt(empresa.getCreatedAt());
        response.setNome(empresa.getNome());
        response.setCnpj(empresa.getCnpj());
        response.setCategoria(empresa.getCategoria());
        response.setTelefone(empresa.getTelefone());
        response.setEmail(empresa.getEmail());
        response.setDescricao(empresa.getDescricao());
        response.setLogoUrl(empresa.getLogoUrl());
        response.setPercentualCupomAniversario(empresa.getPercentualCupomAniversario());


        if (empresa.getEndereco() != null) {
            EnderecoResponse enderecoResponse = new EnderecoResponse();
            enderecoResponse.setRua(empresa.getEndereco().getRua());
            enderecoResponse.setBairro(empresa.getEndereco().getBairro());
            enderecoResponse.setCidade(empresa.getEndereco().getCidade());
            enderecoResponse.setCep(empresa.getEndereco().getCep());
            enderecoResponse.setNumero(empresa.getEndereco().getNumero());
            enderecoResponse.setEstado(empresa.getEndereco().getEstado());
            response.setEndereco(enderecoResponse);
        }

        if (empresa.getLocalizacao() != null) {
            response.setLatitude(empresa.getLocalizacao().getLatitude());
            response.setLongitude(empresa.getLocalizacao().getLongitude());
        }

        return response;

    }


        public EmpresaResponse create(EmpresaRequest dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload invalido");
        }
        if (dto.getEndereco() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço é obrigatorio");
        }

        EmpresaModel empresa = new EmpresaModel();
        // ACRESCENTANDO AS VALIDAÇÕES:
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este CNPJ já está sendo usado!");
        }

        if (empresaRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Este Email já está sendo usado!");
        }

        if (dto.getPercentualCupomAniversario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "percentual do cupom é obrigatório");
        }

        if (dto.getPercentualCupomAniversario() < 0 || dto.getPercentualCupomAniversario() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "percentual deve estar entre 0 e 100");
        }

        // validar senha
        validarSenha(dto.getSenha());
        //Transferindo os dados
        empresa.setNome(dto.getNome());
        empresa.setCnpj(dto.getCnpj());
        empresa.setCategoria(dto.getCategoria());
        empresa.setTelefone(dto.getTelefone());
        empresa.setDescricao(dto.getDescricao());
        empresa.setEmail(dto.getEmail().trim().toLowerCase());
        empresa.setSenha(passwordEncoder.encode(dto.getSenha()));
        empresa.setRole(Role.ENTERPRISE);
        empresa.setPercentualCupomAniversario(dto.getPercentualCupomAniversario());

        EnderecoModel endereco = new EnderecoModel();
        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setCidade(defaultCidade(dto.getEndereco().getCidade()));
        endereco.setEstado(DEFAULT_ESTADO);

        empresa.setEndereco(endereco);

        //inserindo a latitude e longitude
        String enderecoFormatado = localizacaoService.montarEndereco(empresa.getEndereco());
        LocalizacaoModel localizacao = localizacaoService.buscarCoordenadas(enderecoFormatado);

        empresa.setLocalizacao(localizacao);

        //salvando
        empresa = empresaRepository.save(empresa);

        return toResponse(empresa);
    }

    public EmpresaResponse getMe(String email) {
        EmpresaModel empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
        return toResponse(empresa);
    }


    public EmpresaResponse findById(UUID id) {
        EmpresaModel empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
        return toResponse(empresa);
    }

    public List<EmpresaResponse> findAll() {
        return empresaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    private String defaultCidade(String cidade) {
        if (cidade == null) {
            return DEFAULT_CIDADE;
        }
        String t = cidade.trim();
        return t.isEmpty() ? DEFAULT_CIDADE : t;
    }

    private void validarSenha(String senha){
        if(senha == null || senha.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha é obrigatória");
        }
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if(!senha.matches(regex)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter no mínimo 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial"
            );
        }
    }

    public EmpresaResponse updateMe(EmpresaUpdateRequest request, String email){
        EmpresaModel empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
        if (request.nome() != null) empresa.setNome(request.nome());
        if (request.descricao() != null) empresa.setDescricao(request.descricao());

        empresa = empresaRepository.save(empresa);

        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setNome(empresa.getNome());
        response.setDescricao(empresa.getDescricao());
        response.setLogoUrl(empresa.getLogoUrl());
        return response;
    }

    public EmpresaResponse uploadLogo(MultipartFile logo, String email) {
        EmpresaModel empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));

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


        var stored = fileStorageService.storeEmpresaLogo(empresa.getId(), logo);
        empresa.setLogoUrl(stored.publicPath());
        empresa = empresaRepository.save(empresa);

        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setNome(empresa.getNome());
        response.setDescricao(empresa.getDescricao());
        response.setLogoUrl(empresa.getLogoUrl());
        return response;
    }
}
