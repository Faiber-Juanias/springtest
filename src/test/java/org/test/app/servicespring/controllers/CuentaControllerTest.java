package org.test.app.servicespring.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.test.app.servicespring.iservices.ICuentaService;
import org.test.app.servicespring.models.Cuenta;
import org.test.app.servicespring.models.TransaccionDto;
import org.test.app.servicespring.util.Datos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @MockBean
    private ICuentaService cuentaService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDetalle() throws Exception {
        Mockito.when(cuentaService.findById(1L)).thenReturn(Datos.CUENTA_001.orElseThrow());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/cuentas/detalle/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persona").value("Andres"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("1000"));

        Mockito.verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception {
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        Mockito.doNothing().when(cuentaService).transfer(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/cuentas/transferir")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaccion.cuentaOrigenId").value(1L));

        Mockito.verify(cuentaService).transfer(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
    }

    @Test
    void testListar() throws Exception {
        List<Cuenta> cuentas = Arrays.asList(Datos.CUENTA_001.orElseThrow(), Datos.CUENTA_002.orElseThrow());
        Mockito.when(cuentaService.findAll()).thenReturn(cuentas);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].persona").value("Andres"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].saldo").value("1000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].persona").value("Jhon"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].saldo").value("2000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(cuentas)));

        Mockito.verify(cuentaService).findAll();
    }

    @Test
    void testGuardar() throws Exception {
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Mockito.when(cuentaService.save(Mockito.any())).then(invocationOnMock -> {
            Cuenta c = invocationOnMock.getArgument(0);
            c.setId(3L);
            return c;
        });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persona", Matchers.is("Pepe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo", Matchers.is(3000)));

        Mockito.verify(cuentaService).save(Mockito.any());
    }
}