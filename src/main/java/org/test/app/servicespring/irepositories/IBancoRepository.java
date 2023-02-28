package org.test.app.servicespring.irepositories;

import org.test.app.servicespring.models.Banco;

import java.util.List;

public interface IBancoRepository {
    List<Banco> findAll();
    Banco findById(Long id);
    void update(Banco banco);
}
