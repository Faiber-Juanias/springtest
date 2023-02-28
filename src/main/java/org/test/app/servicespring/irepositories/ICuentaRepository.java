package org.test.app.servicespring.irepositories;

import org.test.app.servicespring.models.Cuenta;

import java.util.List;

public interface ICuentaRepository {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    void update(Cuenta cuenta);
}
