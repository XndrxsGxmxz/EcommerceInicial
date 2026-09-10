package com.ecommerce.auth.infraestructure.mapper;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.infraestructure.driver_adapters.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class MapperUsuario {

    public Usuario toUsuario(UsuarioData usuarioData){
        return new Usuario(
                usuarioData.getIdUsuario(),
                usuarioData.getNombre(),
                usuarioData.getCorreo(),
                usuarioData.getClave(),
                usuarioData.getRol(),
                usuarioData.getNumeroTelefonico(),
                usuarioData.getEdad()
        );

    }

    public UsuarioData toData(Usuario usuario){
        return new UsuarioData(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getClave(),
                usuario.getRol(),
                usuario.getEdad(),
                usuario.getNumeroTelefonico()
        );
    }
}