package com.ecommerce.auth.domain.usecase.validation;

import com.ecommerce.auth.domain.exception.UsuarioInvalidoException;
import com.ecommerce.auth.domain.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class UsuarioValidator {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern TELEFONO_REGEX =
            Pattern.compile("^\\d{7,10}$");
    private static final Set<String> ROLES_VALIDOS =
            Set.of("ADMIN", "CLIENTE", "VENDEDOR");
    private static final int EDAD_MINIMA = 18;

    private static final List<Function<Usuario, Optional<String>>> REGLAS_DATOS = List.of(
            u -> requerido(u.getNombre(), "El nombre"),
            u -> requerido(u.getCorreo(), "El correo"),
            u -> formatoValido(u.getCorreo(), EMAIL_REGEX, "El correo tiene un formato inválido"),
            u -> requerido(u.getClave(), "La clave"),
            u -> claveSegura(u.getClave()),
            u -> rolValido(u.getRol()),
            u -> formatoValido(u.getNumeroTelefonico(), TELEFONO_REGEX, "El número telefónico es inválido"),
            u -> edadValida(u.getEdad())
    );

    private static final List<Function<Usuario, Optional<String>>> REGLA_ID = List.of(
            u -> idValido(u.getIdUsuario())
    );

    private UsuarioValidator() {}

    public static void validar(Usuario usuario) {
        ejecutarReglas(REGLAS_DATOS, usuario);
    }

    public static void validarActualizacion(Usuario usuario) {
        List<Function<Usuario, Optional<String>>> todasLasReglas =
                Stream.concat(REGLA_ID.stream(), REGLAS_DATOS.stream()).toList();
        ejecutarReglas(todasLasReglas, usuario);
    }

    public static void validarId(Long id) {
        Optional.of(idValido(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(mensaje -> { throw new UsuarioInvalidoException(List.of(mensaje)); });
    }

    private static void ejecutarReglas(List<Function<Usuario, Optional<String>>> reglas, Usuario usuario) {
        List<String> errores = reglas.stream()
                .map(regla -> regla.apply(usuario))
                .flatMap(Optional::stream)
                .toList();

        Optional.of(errores)
                .filter(lista -> !lista.isEmpty())
                .ifPresent(lista -> { throw new UsuarioInvalidoException(lista); });
    }

    private static Optional<String> requerido(String valor, String etiqueta) {
        return Optional.of(Optional.ofNullable(valor).orElse(""))
                .map(String::trim)
                .filter(String::isEmpty)
                .map(v -> etiqueta + " es obligatorio");
    }

    private static Optional<String> idValido(Long id) {
        return Optional.of(Optional.ofNullable(id).orElse(0L))
                .filter(v -> v <= 0)
                .map(v -> "El id de usuario es obligatorio y debe ser un número positivo");
    }

    private static Optional<String> formatoValido(String valor, Pattern patron, String mensaje) {
        return Optional.ofNullable(valor)
                .filter(v -> !v.isBlank())
                .filter(patron.asPredicate().negate())
                .map(v -> mensaje);
    }

    private static Optional<String> claveSegura(String clave) {
        List<Predicate<String>> reglasClave = List.of(
                v -> v.length() >= 8,
                v -> v.chars().anyMatch(Character::isUpperCase),
                v -> v.chars().anyMatch(Character::isDigit)
        );

        return Optional.ofNullable(clave)
                .filter(v -> !v.isBlank())
                .filter(v -> !reglasClave.stream().allMatch(regla -> regla.test(v)))
                .map(v -> "La clave debe tener mínimo 8 caracteres, una mayúscula y un número");
    }

    private static Optional<String> rolValido(String rol) {
        return Optional.of(Optional.ofNullable(rol).orElse(""))
                .filter(r -> !ROLES_VALIDOS.contains(r))
                .map(r -> "Rol inválido, debe ser uno de: " + ROLES_VALIDOS);
    }

    private static Optional<String> edadValida(Integer edad) {
        return Optional.of(Optional.ofNullable(edad).orElse(0))
                .filter(e -> e < EDAD_MINIMA)
                .map(e -> "El usuario debe ser mayor o igual a " + EDAD_MINIMA + " años");
    }
}