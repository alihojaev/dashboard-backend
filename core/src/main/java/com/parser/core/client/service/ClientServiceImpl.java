package com.parser.core.client.service;

import com.parser.core.client.dto.ClientDto;
import com.parser.core.client.dto.ClientRegistrationDto;
import com.parser.core.client.entity.ClientEntity;
import com.parser.core.client.repo.ClientRepo;
import com.parser.core.exceptions.BadRequestException;
import com.parser.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepo clientRepo;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public ClientDto registerClient(ClientRegistrationDto registrationDto) {
        // Валидация данных
        if (!registrationDto.isValidForAuthType()) {
            throw new BadRequestException("Invalid data for auth type: " + registrationDto.getAuthType());
        }
        
        // Проверяем, существует ли пользователь с таким email
        Optional<ClientEntity> existingClient = clientRepo.findByEmail(registrationDto.getEmail());
        
        if (existingClient.isPresent()) {
            ClientEntity client = existingClient.get();
            
            // Если пользователь заблокирован
            if (client.getBlocked()) {
                throw new BadRequestException("Account is blocked");
            }
            
            // Для OAuth авторизации - обновляем данные и авторизуем
            if (registrationDto.getAuthType() == ClientEntity.AuthType.GOOGLE) {
                // Если у пользователя нет Google ID, добавляем его
                if (client.getGoogleId() == null) {
                    client.setGoogleId(registrationDto.getGoogleId());
                    client.setGoogleEmail(registrationDto.getGoogleEmail());
                    log.info("Linked Google account to existing user: {}", client.getUsername());
                }
                // Если Google ID не совпадает, обновляем его (пользователь мог сменить Google аккаунт)
                else if (!client.getGoogleId().equals(registrationDto.getGoogleId())) {
                    log.warn("Google ID changed for user {}: old={}, new={}", 
                            client.getUsername(), client.getGoogleId(), registrationDto.getGoogleId());
                    client.setGoogleId(registrationDto.getGoogleId());
                    client.setGoogleEmail(registrationDto.getGoogleEmail());
                }
                
                client.setLastActivity(LocalDateTime.now());
                ClientEntity savedClient = clientRepo.save(client);
                log.info("Client authenticated via Google: {}", savedClient.getUsername());
                return ClientDto.fromEntity(savedClient);
            }
            

            
            // Для email авторизации - проверяем пароль
            if (registrationDto.getAuthType() == ClientEntity.AuthType.EMAIL) {
                if (client.getPassword() == null) {
                    throw new BadRequestException("User exists but has no password set");
                }
                if (registrationDto.getPassword() == null || !passwordEncoder.matches(registrationDto.getPassword(), client.getPassword())) {
                    throw new BadRequestException("Invalid password");
                }
                
                client.setLastActivity(LocalDateTime.now());
                ClientEntity savedClient = clientRepo.save(client);
                log.info("Client authenticated via email: {}", savedClient.getUsername());
                return ClientDto.fromEntity(savedClient);
            }
        }
        
        // Если пользователь не существует, создаем нового
        // Проверяем уникальность username только для новых пользователей
        if (existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username already exists: " + registrationDto.getUsername());
        }
        
        // Проверка уникальности для OAuth ID только для новых пользователей
        if (registrationDto.getAuthType() == ClientEntity.AuthType.GOOGLE && 
            registrationDto.getGoogleId() != null && 
            existsByGoogleId(registrationDto.getGoogleId())) {
            throw new BadRequestException("Google ID already exists: " + registrationDto.getGoogleId());
        }
        

        
        // Создание новой сущности
        ClientEntity client = new ClientEntity();
        client.setId(UUID.randomUUID());
        client.setEmail(registrationDto.getEmail());
        client.setUsername(registrationDto.getUsername());
        client.setPhone(registrationDto.getPhone());
        client.setAuthType(registrationDto.getAuthType());
        client.setBlocked(false);
        client.setLastActivity(LocalDateTime.now());
        
        // Установка данных в зависимости от типа авторизации
        switch (registrationDto.getAuthType()) {
            case EMAIL:
                if (registrationDto.getPassword() == null || registrationDto.getPassword().trim().isEmpty()) {
                    throw new BadRequestException("Password is required for email authentication");
                }
                client.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
                break;
            case GOOGLE:
                client.setGoogleId(registrationDto.getGoogleId());
                client.setGoogleEmail(registrationDto.getGoogleEmail());
                break;

        }
        
        ClientEntity savedClient = clientRepo.save(client);
        log.info("Client registered successfully: {}", savedClient.getUsername());
        
        return ClientDto.fromEntity(savedClient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientById(UUID id) {
        return clientRepo.findById(id).map(ClientDto::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByEmail(String email) {
        return clientRepo.findByEmail(email).map(ClientDto::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByUsername(String username) {
        return clientRepo.findByUsername(username).map(ClientDto::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientByGoogleId(String googleId) {
        return clientRepo.findByGoogleId(googleId).map(ClientDto::fromEntity);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getAllClients() {
        return clientRepo.findAll().stream()
                .map(ClientDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClientDto updateClient(UUID id, ClientDto clientDto) {
        ClientEntity client = clientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        
        // Обновление полей
        if (clientDto.getEmail() != null) {
            client.setEmail(clientDto.getEmail());
        }
        if (clientDto.getUsername() != null) {
            client.setUsername(clientDto.getUsername());
        }
        if (clientDto.getPhone() != null) {
            client.setPhone(clientDto.getPhone());
        }
        if (clientDto.getBlocked() != null) {
            client.setBlocked(clientDto.getBlocked());
        }
        
        ClientEntity updatedClient = clientRepo.save(client);
        return ClientDto.fromEntity(updatedClient);
    }
    
    @Override
    public void deleteClient(UUID id) {
        if (!clientRepo.existsById(id)) {
            throw new NotFoundException("Client not found with id: " + id);
        }
        clientRepo.deleteById(id);
        log.info("Client deleted: {}", id);
    }
    
    @Override
    public void blockClient(UUID id) {
        ClientEntity client = clientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        client.setBlocked(true);
        clientRepo.save(client);
        log.info("Client blocked: {}", id);
    }
    
    @Override
    public void unblockClient(UUID id) {
        ClientEntity client = clientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        client.setBlocked(false);
        clientRepo.save(client);
        log.info("Client unblocked: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return clientRepo.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return clientRepo.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByGoogleId(String googleId) {
        return clientRepo.existsByGoogleId(googleId);
    }
    

    

    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with username: " + username));
    }
} 