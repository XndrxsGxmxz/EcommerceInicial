package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import com.ecommerce.auth.infraestructure.driver_adapters.UsuarioData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{id}")
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UsuarioData usuarioData){
        try{
            String mensajeRespuesta = usuarioUseCase.loginUsuario(usuarioData.getCorreo(),usuarioData.getClave());
            return new ResponseEntity<>(mensajeRespuesta, HttpStatus.OK);
        } catch (Exception error){
            return new ResponseEntity<>("Falló el logueo", HttpStatus.CONFLICT);
        }
    }
}