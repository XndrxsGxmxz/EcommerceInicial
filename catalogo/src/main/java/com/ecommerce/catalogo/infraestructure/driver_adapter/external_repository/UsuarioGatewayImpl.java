package com.ecommerce.catalogo.infraestructure.driver_adapter.external_repository;

import com.ecommerce.catalogo.domain.exception.SaldoInsuficienteException;
import com.ecommerce.catalogo.domain.exception.ServicioExternoNoDisponibleException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

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
        }
    }

    @Override
    public BigDecimal consultarSaldo(Long usuarioId) {
        try {
            BigDecimal saldo = restTemplate.getForObject(
                    authUrl + "/api/usuarios/" + usuarioId + "/saldo", BigDecimal.class);
            return saldo == null ? BigDecimal.ZERO : saldo;
        } catch (HttpClientErrorException.NotFound e) {
            throw new UsuarioNoEncontradoException(usuarioId);
        } catch (RestClientException e) {
            throw new ServicioExternoNoDisponibleException("Auth", e);
        }
    }

    @Override
    public void descontarSaldo(Long usuarioId, BigDecimal monto) {
        try {
            restTemplate.postForObject(
                    authUrl + "/api/usuarios/" + usuarioId + "/saldo/descontar",
                    Map.of("monto", monto), BigDecimal.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new UsuarioNoEncontradoException(usuarioId);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new SaldoInsuficienteException("Auth rechazó el descuento: saldo insuficiente o monto inválido");
        } catch (RestClientException e) {
            throw new ServicioExternoNoDisponibleException("Auth", e);
        }
    }

    @Override
    public void reintegrarSaldo(Long usuarioId, BigDecimal monto) {
        try {
            restTemplate.put(authUrl + "/api/usuarios/" + usuarioId + "/saldo/recargar",
                    Map.of("monto", monto));
        } catch (RestClientException e) {
            throw new ServicioExternoNoDisponibleException("Auth", e);
        }
    }
}