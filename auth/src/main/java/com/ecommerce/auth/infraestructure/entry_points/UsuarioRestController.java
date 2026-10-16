package com.ecommerce.auth.infraestructure.entry_points;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import com.ecommerce.auth.infraestructure.driver_adapters.UsuarioData;
import com.ecommerce.auth.infraestructure.mapper.MapperUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping(path = "/api/usuarios")
@RequiredArgsConstructor


public class UsuarioRestController {
    private final UsuarioUseCase usuarioUseCase;
    private final MapperUsuario mapperUsuario;


    @PostMapping("/save")
    public ResponseEntity<Usuario> saveUsuario(@RequestBody UsuarioData usuarioData){
        Usuario usuario = mapperUsuario.toUsuario(usuarioData);
        Usuario usuarioValidadoGuardado = usuarioUseCase.guardar(usuario);

        if(usuarioValidadoGuardado.getIdUsuario() != null){
            return new ResponseEntity<>(usuarioValidadoGuardado, HttpStatus.OK);
        }

        return new ResponseEntity<>(usuarioValidadoGuardado, HttpStatus.CONFLICT);
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
    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioUseCase.consultarSaldo(id));
    }

    @PutMapping("/{id}/saldo/recargar")
    public ResponseEntity<BigDecimal> recargar(@PathVariable Long id, @RequestBody MontoRequest request) {
        return ResponseEntity.ok(usuarioUseCase.recargarSaldo(id, request.getMonto()).getSaldo());
    }

    @PutMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> asignar(@PathVariable Long id, @RequestBody MontoRequest request) {
        return ResponseEntity.ok(usuarioUseCase.asignarSaldo(id, request.getMonto()).getSaldo());
    }

    @PostMapping("/{id}/saldo/descontar")
    public ResponseEntity<BigDecimal> descontar(@PathVariable Long id, @RequestBody MontoRequest request) {
        return ResponseEntity.ok(usuarioUseCase.descontarSaldo(id, request.getMonto()).getSaldo());
    }
}