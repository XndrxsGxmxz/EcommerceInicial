package com.ecommerce.auth.domain.model.gateway;

public interface EncrypterGateway {

    String encrypt(String clave);

    boolean checkPass(String claveIngresada, String claveEncriptada);
}