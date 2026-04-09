package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.dto.Role;
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

    public EmpresaModel create(EmpresaModel empresa) {
        // ACRESCENTANDO AS VALIDAÇÕES:
        if (empresaRepository.existsByCnpj(empresa.getCnpj())) {
            throw new RuntimeException("Este CNPJ já está sendo usado!");
        }

        if (empresaRepository.existsByEmail(empresa.getEmail())) {
            throw new RuntimeException("Este Email já está sendo usado!");
        }

        // MANTENDO O QUE JÁ EXISTIA:
        empresa.setSenha(passwordEncoder.encode(empresa.getSenha()));
        empresa.setRoleEmpresa(Role.ENTERPRISE);

        if (empresa.getEndereco() != null) {
            empresa.getEndereco().setEmpresa(empresa);
        }
        return empresaRepository.save(empresa);
    }

    public List<EmpresaModel> findAll() {
        return empresaRepository.findAll();
    }
}
