package com.negocionaarea.mobile_api.security;

import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.EmpresaModel;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class EmpresaAuthenticated implements UserDetails {
    private final EmpresaModel empresaModel;

    public EmpresaAuthenticated(EmpresaModel empresaModel) {
        this.empresaModel = empresaModel;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(empresaModel.getRoleEmpresa() == Role.ENTERPRISE){
            return List.of(new SimpleGrantedAuthority("ROLE_ENTERPRISE"), new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }else {
            return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
    }

    @Override
    public @Nullable String getPassword() {
        return empresaModel.getSenha();
    }

    @Override
    public String getUsername() {
        return empresaModel.getEmail();
    }
}
