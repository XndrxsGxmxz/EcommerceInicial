package com.ecommerce.auth.infraestructure.driver_adapters;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.UsuarioGateway;
import com.ecommerce.auth.infraestructure.mapper.MapperUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGateway {

    private final MapperUsuario mapperUsuario;
    private final UsuarioDataJpaRepository repository;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioData usuarioData = mapperUsuario.toData(usuario);
        return mapperUsuario.toUsuario(repository.save(usuarioData));
    }

    @Override
    public void eliminarPorId(Long idUsuario) {
        repository.deleteById(idUsuario);
    }

    @Override
    public Usuario buscarPorId(Long idUsuario) {
        return repository.findById(idUsuario)
                .map(mapperUsuario::toUsuario)
                .orElse(null);
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        UsuarioData usuarioData = mapperUsuario.toData(usuario);
        return mapperUsuario.toUsuario(repository.save(usuarioData));
    }


    @Override
    public Usuario buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .map(mapperUsuario::toUsuario)
                .orElse(null);
    }

}