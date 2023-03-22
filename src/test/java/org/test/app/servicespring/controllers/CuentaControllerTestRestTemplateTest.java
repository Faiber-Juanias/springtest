package org.test.app.servicespring.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.test.app.servicespring.models.Cuenta;
import org.test.app.servicespring.models.TransaccionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integracion_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

    @LocalServerPort
    private int puerto;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Order(1)
    @Test
    void listar() throws JsonProcessingException {
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        ResponseEntity<String> responseEntity = client.postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);
        String json = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));

        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("OK", jsonNode.path("status").asText());
        assertEquals("Transferencia realizada con éxito", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals(100, jsonNode.path("transaccion").path("monto").asInt());
    }

    @Order(2)
    @Test
    void testDetalle() {
        ResponseEntity<Cuenta> response = client.getForEntity(crearUri("/api/cuentas/detalle/1"), Cuenta.class);
        Cuenta cuenta = response.getBody();

        assertNotNull(cuenta);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, cuenta.getId());
        assertEquals("Andrés", cuenta.getPersona());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
    }

    @Order(3)
    @Test
    void testListar() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(Objects.requireNonNull(response.getBody()));

        assertEquals(2, cuentas.size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Andrés", cuentas.get(0).getPersona());
        assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
        assertEquals(2L, cuentas.get(1).getId());
        assertEquals("John", cuentas.get(1).getPersona());
        assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));

        assertEquals(1L, json.get(0).path("id").asLong());
        assertEquals("Andrés", json.get(0).path("persona").asText());
        assertEquals("900.0", json.get(0).path("saldo").asText());
        assertEquals(2L, json.get(1).path("id").asLong());
        assertEquals("John", json.get(1).path("persona").asText());
        assertEquals("2100.0", json.get(1).path("saldo").asText());
    }

    @Order(4)
    @Test
    void testGuardar() {
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3800"));

        ResponseEntity<Cuenta> response = client.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Cuenta cuentaC = response.getBody();
        assertNotNull(cuentaC);
        assertEquals(3L, cuentaC.getId());
        assertEquals("Pepa", cuentaC.getPersona());
        assertEquals("3800", cuentaC.getSaldo().toPlainString());
    }

    @Order(5)
    @Test
    void testEliminar() {
        ResponseEntity<Cuenta[]> response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertEquals(3, cuentas.size());

        //client.delete((crearUri("/api/cuentas/3")));
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        ResponseEntity<Void> exchange =
                client.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, Void.class, pathVariables);
        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        response = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        cuentas = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> response2 = client.getForEntity(crearUri("/api/cuentas/detalle/3"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertFalse(response2.hasBody());
    }

    private String crearUri(String uri) {
        return "http://localhost:" + puerto + uri;
    }
}