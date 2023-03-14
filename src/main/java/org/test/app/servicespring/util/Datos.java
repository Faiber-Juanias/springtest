package org.test.app.servicespring.util;

import org.test.app.servicespring.models.Banco;
import org.test.app.servicespring.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    public static final Optional<Cuenta> CUENTA_001 = Optional.of(new Cuenta(1L, "Andres", new BigDecimal("1000")));
    public static final Optional<Cuenta> CUENTA_002 = Optional.of(new Cuenta(2L, "Jhon", new BigDecimal("2000")));
    public static final Optional<Banco> BANCO = Optional.of(new Banco(1L, "El banco financiero", 0));
}
