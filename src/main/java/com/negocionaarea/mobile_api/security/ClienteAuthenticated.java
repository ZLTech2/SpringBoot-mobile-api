package com.negocionaarea.mobile_api.security;

import com.negocionaarea.mobile_api.dto.Role;
import com.negocionaarea.mobile_api.model.ClienteModel;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ClienteAuthenticated implements UserDetails {
    private final ClienteModel clienteModel;

    public ClienteAuthenticated(ClienteModel clienteModel) {
        this.clienteModel = clienteModel;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(clienteModel.getRoleCliente() == Role.ENTERPRISE){
            return List.of(new SimpleGrantedAuthority("ROLE_ENTERPRISE"), new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }else{
            return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
    }

    @Override
    public @Nullable String getPassword() {
        return clienteModel.getSenhaCliente();
    }

    @Override
    public String getUsername() {
        return clienteModel.getEmailCliente();
    }
}
