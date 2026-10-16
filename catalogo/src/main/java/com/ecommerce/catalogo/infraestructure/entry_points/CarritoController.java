package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.domain.exception.CarritoNoFoundException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.usecase.CarritoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/ecommerce/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoUseCase carritoUseCase;

    @PostMapping("/agregar")
    public ResponseEntity<Carrito> agregarItem(@RequestParam Long usuarioId, @RequestBody ItemCarrito item) {
        return ResponseEntity.ok(carritoUseCase.agregarProductoAlCarrito(usuarioId, item));
    }

    @GetMapping
    public ResponseEntity<Carrito> verCarrito(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(carritoUseCase.verCarrito(usuarioId));
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<Carrito> eliminarItemDelCarrito(@RequestParam Long usuarioId,
                                                          @PathVariable Long productoId) {
        return ResponseEntity.ok(carritoUseCase.eliminarProductoDelCarrito(usuarioId, productoId));
    }

    @PostMapping("/venta/{usuarioId}")
    public ResponseEntity<String> realizarVenta(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoUseCase.realizarVenta(usuarioId));
    }
}
