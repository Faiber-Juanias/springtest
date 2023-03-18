package org.test.app.servicespring.irepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.test.app.servicespring.models.Cuenta;

import java.util.Optional;

public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {

    @Query("select c from Cuenta c where c.persona = ?1")
    Optional<Cuenta> findByPersona(String persona);
}
