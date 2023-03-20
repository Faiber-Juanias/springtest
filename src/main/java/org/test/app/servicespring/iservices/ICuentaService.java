package org.test.app.servicespring.iservices;

import org.test.app.servicespring.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface ICuentaService {
    Cuenta findById(Long id);
    int getTotalTransferences(Long bancoId);
    BigDecimal reviewSaldo(Long cuentaId);
    void transfer(Long numCuentaOrigin, Long numCuentaFinal, BigDecimal monto, Long bancoId);
    List<Cuenta> findAll();
    Cuenta save(Cuenta cuenta);
}
