package com.parser.core.client.service;

import com.parser.core.client.dto.ClientDto;
import com.parser.core.client.dto.ClientRegistrationDto;
import com.parser.core.client.entity.ClientEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService extends UserDetailsService {
    
    ClientDto registerClient(ClientRegistrationDto registrationDto);
    
    Optional<ClientDto> getClientById(UUID id);
    
    Optional<ClientDto> getClientByEmail(String email);
    
    Optional<ClientDto> getClientByUsername(String username);
    
    Optional<ClientDto> getClientByGoogleId(String googleId);
    

    
    List<ClientDto> getAllClients();
    
    ClientDto updateClient(UUID id, ClientDto clientDto);
    
    void deleteClient(UUID id);
    
    void blockClient(UUID id);
    
    void unblockClient(UUID id);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByGoogleId(String googleId);
    

} 