package org.test.app.servicespring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.test.app.servicespring.irepositories.ICuentaRepository;
import org.test.app.servicespring.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    ICuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        Cuenta cuentaP = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Cuenta save = cuentaRepository.save(cuentaP);

        Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testUpdate() {
        Cuenta cuentaP = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Cuenta save = cuentaRepository.save(cuentaP);

        Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal("3800"));

        Cuenta update = cuentaRepository.save(cuenta);
        assertEquals("3800", update.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("John", cuenta.getPersona());

        cuentaRepository.delete(cuenta);
        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByPersona("John").orElseThrow();
        });
    }
}
