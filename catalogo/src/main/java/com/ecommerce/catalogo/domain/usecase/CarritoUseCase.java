package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.*;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.TransaccionGateway;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import com.ecommerce.catalogo.domain.usecase.validation.CarritoValidator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final ProductoUseCase productoUseCase;
    private final UsuarioGateway usuarioGateway;
    private final TransaccionGateway transaccionGateway;

    public Carrito agregarProductoAlCarrito(Long usuarioId, ItemCarrito itemCarrito) {
        CarritoValidator.validarAgregar(usuarioId, itemCarrito);

        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        Producto producto = productoUseCase.obtenerProducto(itemCarrito.getProductoId());

        Carrito carrito = carritoGateway.buscarActivoPorUsuarioId(usuarioId);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuarioId(usuarioId);
            carrito.setItems(new ArrayList<>());
        }

        Optional<ItemCarrito> existente = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(itemCarrito.getProductoId()))
                .findFirst();

        int cantidadFinal = itemCarrito.getCantidad()
                + existente.map(ItemCarrito::getCantidad).orElse(0);

        if (producto.getStock() == null || cantidadFinal > producto.getStock()) {
            throw new StockInsuficienteException(
                    producto.getNombre(),
                    producto.getStock() == null ? 0 : producto.getStock(),
                    cantidadFinal);
        }

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(cantidadFinal);
            item.setPrecioUnitario(producto.getPrecio());
            item.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidadFinal)));
        } else {
            itemCarrito.setNombreProducto(producto.getNombre());
            itemCarrito.setPrecioUnitario(producto.getPrecio());
            itemCarrito.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(itemCarrito.getCantidad())));
            carrito.getItems().add(itemCarrito);
        }

        carrito.setPreciototal(calcularTotal(carrito.getItems()));
        return carritoGateway.guardar(carrito);
    }

    public Carrito verCarrito(Long usuarioId) {
        CarritoValidator.validarUsuarioId(usuarioId);
        Carrito carrito = carritoGateway.buscarActivoPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoNoEncontradoException("Carrito no encontrado para el usuario ID: " + usuarioId);
        }
        return carrito;
    }

    public Carrito eliminarProductoDelCarrito(Long usuarioId, Long productoId) {
        CarritoValidator.validarEliminar(usuarioId, productoId);

        Carrito carrito = verCarrito(usuarioId);

        List<ItemCarrito> itemsFiltrados = carrito.getItems().stream()
                .filter(item -> !item.getProductoId().equals(productoId))
                .collect(Collectors.toList());

        if (itemsFiltrados.size() == carrito.getItems().size()) {
            throw new CarritoNoEncontradoException(
                    "El producto " + productoId + " no está en el carrito del usuario " + usuarioId);
        }

        carrito.setItems(itemsFiltrados);
        carrito.setPreciototal(calcularTotal(itemsFiltrados));
        return carritoGateway.guardar(carrito);
    }

    public String realizarVenta(Long usuarioId) {
        CarritoValidator.validarUsuarioId(usuarioId);

        Carrito carrito = carritoGateway.buscarActivoPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoNoEncontradoException("No hay un carrito activo para el usuario ID: " + usuarioId);
        }
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException(usuarioId);
        }
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        Map<ItemCarrito, Producto> productos = validarStock(carrito);

        BigDecimal total = carrito.getPreciototal();
        BigDecimal saldo = usuarioGateway.consultarSaldo(usuarioId);
        if (saldo.compareTo(total) < 0) {
            throw new SaldoInsuficienteException(saldo, total);
        }

        // 2. Ejecutar: stock + cierre del carrito + cobro en una transacción
        AtomicBoolean cobrado = new AtomicBoolean(false);
        try {
            transaccionGateway.ejecutar(() -> {
                descontarStock(productos);
                carrito.setComprado(true);
                carritoGateway.guardar(carrito);
                usuarioGateway.descontarSaldo(usuarioId, total);
                cobrado.set(true);
            });
        } catch (RuntimeException e) {
            // 3. Compensación: Auth cobró pero la transacción local falló
            if (cobrado.get()) {
                usuarioGateway.reintegrarSaldo(usuarioId, total);
            }
            throw e;
        }

        return "La compra fue realizada exitosamente. Total: " + total;
    }

    private Map<ItemCarrito, Producto> validarStock(Carrito carrito) {
        Map<ItemCarrito, Producto> productos = new LinkedHashMap<>();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = productoUseCase.obtenerProducto(item.getProductoId());
            int disponible = producto.getStock() == null ? 0 : producto.getStock();
            if (disponible < item.getCantidad()) {
                throw new StockInsuficienteException(producto.getNombre(), disponible, item.getCantidad());
            }
            productos.put(item, producto);
        }
        return productos;
    }

    private void descontarStock(Map<ItemCarrito, Producto> productos) {
        productos.forEach((item, producto) -> {
            producto.setStock(producto.getStock() - item.getCantidad());
            productoUseCase.guardarProducto(producto);
        });
    }

    private BigDecimal calcularTotal(List<ItemCarrito> items) {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
