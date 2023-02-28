package org.test.app.servicespring;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.test.app.servicespring.irepositories.IBancoRepository;
import org.test.app.servicespring.irepositories.ICuentaRepository;
import org.test.app.servicespring.iservices.ICuentaService;
import org.test.app.servicespring.services.CuentaServiceImpl;
import org.test.app.servicespring.util.Datos;

import java.math.BigDecimal;

@SpringBootTest
class SpringtestApplicationTests {

	ICuentaRepository cuentaRepository;
	IBancoRepository bancoRepository;
	ICuentaService service;

	@BeforeEach
	void setUp() {
		cuentaRepository = Mockito.mock(ICuentaRepository.class);
		bancoRepository = Mockito.mock(IBancoRepository.class);
		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

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
	}

}
