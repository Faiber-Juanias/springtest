package org.test.app.servicespring.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.test.app.servicespring.exceptions.DineroInsuficienteException;
import org.test.app.servicespring.irepositories.IBancoRepository;
import org.test.app.servicespring.irepositories.ICuentaRepository;
import org.test.app.servicespring.iservices.ICuentaService;
import org.test.app.servicespring.models.Banco;
import org.test.app.servicespring.models.Cuenta;
import org.test.app.servicespring.util.Datos;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CuentaServiceImplTest {
    @MockBean
    ICuentaRepository cuentaRepository;
    @MockBean
    IBancoRepository bancoRepository;
    @Autowired
    ICuentaService service;

    @Test
    void contextLoads() {
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);
        Mockito.when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002);
        Mockito.when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO);

        BigDecimal saldoOrigin = service.reviewSaldo(1L);
        BigDecimal saldoFinal = service.reviewSaldo(2L);

        assertEquals("1000", saldoOrigin.toPlainString());
        assertEquals("2000", saldoFinal.toPlainString());

        service.transfer(1L, 2L, new BigDecimal("100"), 1L);

        saldoOrigin = service.reviewSaldo(1L);
        saldoFinal = service.reviewSaldo(2L);

        assertEquals("900", saldoOrigin.toPlainString());
        assertEquals("2100", saldoFinal.toPlainString());
        assertEquals(1, service.getTotalTransferences(1L));

        Mockito.verify(cuentaRepository, Mockito.times(3)).findById(1L);
        Mockito.verify(cuentaRepository, Mockito.times(3)).findById(2L);
        Mockito.verify(cuentaRepository, Mockito.times(2)).save(Mockito.any(Cuenta.class));

        Mockito.verify(bancoRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(bancoRepository).save(Mockito.any(Banco.class));
    }

    @Test
    void contextLoads2() {
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);
        Mockito.when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002);
        Mockito.when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO);

        BigDecimal saldoOrigin = service.reviewSaldo(1L);
        BigDecimal saldoFinal = service.reviewSaldo(2L);

        assertEquals("1000", saldoOrigin.toPlainString());
        assertEquals("2000", saldoFinal.toPlainString());

        assertThrows(DineroInsuficienteException.class, () ->
                service.transfer(1L, 2L, new BigDecimal("1200"), 1L));

        saldoOrigin = service.reviewSaldo(1L);
        saldoFinal = service.reviewSaldo(2L);

        assertEquals("1000", saldoOrigin.toPlainString());
        assertEquals("2000", saldoFinal.toPlainString());
        assertEquals(0, service.getTotalTransferences(1L));

        Mockito.verify(cuentaRepository, Mockito.times(3)).findById(1L);
        Mockito.verify(cuentaRepository, Mockito.times(2)).findById(2L);
        Mockito.verify(cuentaRepository, Mockito.never()).save(Mockito.any(Cuenta.class));

        Mockito.verify(bancoRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bancoRepository, Mockito.never()).save(Mockito.any(Banco.class));
    }

    @Test
    void contextLoads3() {
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);

        Cuenta c1 = service.findById(1L);
        Cuenta c2 = service.findById(1L);

        assertSame(c1, c2);
        assertEquals("Andres", c1.getPersona());
        assertEquals("Andres", c2.getPersona());

        Mockito.verify(cuentaRepository, Mockito.times(2)).findById(1L);
    }

    @Test
    void testFindAll() {
        List<Cuenta> datos = Arrays.asList(Datos.CUENTA_001.orElseThrow(), Datos.CUENTA_002.orElseThrow());
        Mockito.when(cuentaRepository.findAll()).thenReturn(datos);

        List<Cuenta> cuentas = service.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(Datos.CUENTA_002.orElseThrow()));

        Mockito.verify(cuentaRepository).findAll();
    }

    @Test
    void testSave() {
        Cuenta cP = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Mockito.when(cuentaRepository.save(Mockito.any())).then(invocationOnMock -> {
            Cuenta c = invocationOnMock.getArgument(0);
            c.setId(3L);
            return c;
        });
        Cuenta cuenta = service.save(cP);
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals(3L, cuenta.getId());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        Mockito.verify(cuentaRepository).save(Mockito.any());
    }
}