package com.negocionaarea.mobile_api.service;

import com.negocionaarea.mobile_api.model.ClienteModel;
import com.negocionaarea.mobile_api.repository.ClienteRepository;
import com.negocionaarea.mobile_api.security.ClienteAuthenticated;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClienteAuthenticatedService implements UserDetailsService {
    private final ClienteRepository clienteRepository;

    public ClienteAuthenticatedService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //procura o cliente pelo email
        ClienteModel cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não cadastrado"));

        return new ClienteAuthenticated(cliente);

    }
}
