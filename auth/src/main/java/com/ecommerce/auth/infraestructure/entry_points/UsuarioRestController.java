package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/usuarios")
public class UsuarioRestController {
    private final UsuarioUseCase usuarioUseCase;

    public UsuarioRestController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }
    @PostMapping("/save")
    public Usuario guardar(@RequestBody Usuario usuario) {
        return usuarioUseCase.guardar(usuario);
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioUseCase.buscarPorId(id);
    }

    @PutMapping
    public Usuario actualizar(@RequestBody Usuario usuario) {
        return usuarioUseCase.actualizarUsuario(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        usuarioUseCase.eliminarPorId(id);
    }

    @GetMapping("/correo/{correo}")
    public Usuario buscarPorCorreo(@PathVariable String correo) {
        return usuarioUseCase.buscarPorCorreo(correo);
    }
}