package org.test.app.servicespring.irepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.app.servicespring.models.Cuenta;

public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {
}
