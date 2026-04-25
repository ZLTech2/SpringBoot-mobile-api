package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.EmpresaModel;
import com.negocionaarea.mobile_api.repository.EmpresaRepository;
import com.negocionaarea.mobile_api.security.EmpresaAuthenticated;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpresaAuthenticatedService implements UserDetailsService {
    private final EmpresaRepository empresaRepository;

    public EmpresaAuthenticatedService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EmpresaModel empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Empresa não cadastrada"));
        return new EmpresaAuthenticated(empresa);
    }
}
