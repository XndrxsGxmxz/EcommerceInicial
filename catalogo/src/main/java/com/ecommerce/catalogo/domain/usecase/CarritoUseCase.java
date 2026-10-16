package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.CarritoNoFoundException;
import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import com.ecommerce.catalogo.domain.usecase.validation.CarritoValidator;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final ProductoUseCase productoUseCase;
    private final UsuarioGateway usuarioGateway;

    public Carrito agregarProductoAlCarrito(Long usuarioId, ItemCarrito itemCarrito) {
        CarritoValidator.validarAgregar(usuarioId, itemCarrito);

        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        Producto producto = productoUseCase.obtenerProducto(itemCarrito.getProductoId());

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
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
            item.setSubtotal(producto.getPrecio() * cantidadFinal);
        } else {
            itemCarrito.setNombreProducto(producto.getNombre());
            itemCarrito.setPrecioUnitario(producto.getPrecio());
            itemCarrito.setSubtotal(producto.getPrecio() * itemCarrito.getCantidad());
            carrito.getItems().add(itemCarrito);
        }

        carrito.setPreciototal(calcularTotal(carrito.getItems()));
        return carritoGateway.guardar(carrito);
    }

    public Carrito verCarrito(Long usuarioId) {
        CarritoValidator.validarUsuarioId(usuarioId);
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoNoFoundException("Carrito no encontrado para el usuario ID: " + usuarioId);
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
            throw new CarritoNoFoundException(
                    "El producto " + productoId + " no está en el carrito del usuario " + usuarioId);
        }

        carrito.setItems(itemsFiltrados);
        carrito.setPreciototal(calcularTotal(itemsFiltrados));
        return carritoGateway.guardar(carrito);
    }

    public String realizarVenta(Long usuarioId) {
        CarritoValidator.validarUsuarioId(usuarioId);

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoNoFoundException("Carrito no encontrado para el usuario ID: " + usuarioId);
        }
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException(usuarioId);
        }

        Map<ItemCarrito, Producto> productos = new LinkedHashMap<>();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = productoUseCase.obtenerProducto(item.getProductoId());
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        producto.getStock() == null ? 0 : producto.getStock(),
                        item.getCantidad());
            }
            productos.put(item, producto);
        }

        // 2) Descontar stock
        productos.forEach((item, producto) -> {
            producto.setStock(producto.getStock() - item.getCantidad());
            productoUseCase.guardarProducto(producto);
        });

        carritoGateway.eliminarCarritoByUsuarioId(usuarioId);
        return "La compra fue realizada exitosamente";
    }

    private double calcularTotal(List<ItemCarrito> items) {
        return items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
    }
}
