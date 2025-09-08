package com.parser.server.controllers;

import com.parser.core.client.dto.ClientDto;
import com.parser.core.client.dto.ClientRegistrationDto;
import com.parser.core.client.dto.ClientAuthResponseDto;
import com.parser.core.client.entity.ClientEntity;
import com.parser.core.client.service.ClientService;
import com.parser.core.client.service.ClientAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    private final ClientAuthService clientAuthService;
    
    @PostMapping("/register")
    public ResponseEntity<ClientAuthResponseDto> registerClient(@Valid @RequestBody ClientRegistrationDto registrationDto) {
        log.info("Registering new client with username: {}", registrationDto.getUsername());
        ClientDto client = clientService.registerClient(registrationDto);
        
        // Создаем сущность клиента для создания токена
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(client.getId());
        clientEntity.setEmail(client.getEmail());
        clientEntity.setUsername(client.getUsername());
        clientEntity.setAuthType(client.getAuthType());
        
        // Создаем бессрочный токен для клиента
        String token = clientAuthService.createClientToken(clientEntity);
        
        ClientAuthResponseDto response = new ClientAuthResponseDto(client, token, "Bearer");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientDto> getClientByEmail(@PathVariable String email) {
        return clientService.getClientByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ClientDto> getClientByUsername(@PathVariable String username) {
        return clientService.getClientByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/google/{googleId}")
    public ResponseEntity<ClientDto> getClientByGoogleId(@PathVariable String googleId) {
        return clientService.getClientByGoogleId(googleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    

    
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientDto clientDto) {
        ClientDto updatedClient = clientService.updateClient(id, clientDto);
        return ResponseEntity.ok(updatedClient);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/block")
    public ResponseEntity<Void> blockClient(@PathVariable UUID id) {
        clientService.blockClient(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/unblock")
    public ResponseEntity<Void> unblockClient(@PathVariable UUID id) {
        clientService.unblockClient(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = clientService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = clientService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/check/google/{googleId}")
    public ResponseEntity<Boolean> checkGoogleIdExists(@PathVariable String googleId) {
        boolean exists = clientService.existsByGoogleId(googleId);
        return ResponseEntity.ok(exists);
    }
    

    
    /**
     * Получает бессрочный токен для существующего клиента по email
     */
    @PostMapping("/login")
    public ResponseEntity<ClientAuthResponseDto> loginClient(@RequestBody LoginRequest request) {
        try {
            Optional<ClientDto> clientOpt = clientService.getClientByEmail(request.getEmail());
            if (clientOpt.isPresent()) {
                ClientDto client = clientOpt.get();
                
                // Создаем сущность клиента для создания токена
                ClientEntity clientEntity = new ClientEntity();
                clientEntity.setId(client.getId());
                clientEntity.setEmail(client.getEmail());
                clientEntity.setUsername(client.getUsername());
                clientEntity.setAuthType(client.getAuthType());
                
                // Создаем бессрочный токен для клиента
                String token = clientAuthService.createClientToken(clientEntity);
                
                ClientAuthResponseDto response = new ClientAuthResponseDto(client, token, "Bearer");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error during client login: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Внутренний класс для запроса логина
     */
    public static class LoginRequest {
        private String email;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }
} 