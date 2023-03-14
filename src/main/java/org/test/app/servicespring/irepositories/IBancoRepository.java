package org.test.app.servicespring.irepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.app.servicespring.models.Banco;

public interface IBancoRepository extends JpaRepository<Banco, Long> {
}
