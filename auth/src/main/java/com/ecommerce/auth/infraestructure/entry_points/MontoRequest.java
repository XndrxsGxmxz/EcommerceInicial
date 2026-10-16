package com.ecommerce.auth.infraestructure.entry_points;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MontoRequest {
    private BigDecimal monto;
}