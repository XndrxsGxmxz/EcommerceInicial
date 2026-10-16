package com.ecommerce.catalogo.infraestructure.driver_adapter.external_repository;

import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authUrl;

    @Override
    public boolean usuarioExiste(Long usuarioId) {
        try {
            restTemplate.getForEntity(authUrl + "/api/usuarios/" + usuarioId, Void.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception errorMensaje) {
            throw new RuntimeException("Error al consultar el servicio de autenticación", errorMensaje);
        }
    }
}