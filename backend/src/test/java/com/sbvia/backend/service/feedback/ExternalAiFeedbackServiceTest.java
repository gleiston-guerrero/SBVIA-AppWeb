package com.sbvia.backend.service.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalAiFeedbackServiceTest {

    private HttpServer servidor;

    @AfterEach
    void detener() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    private DrivingData datos() {
        return new DrivingData(100, new BigDecimal("45.00"), new BigDecimal("70.00"),
                1, 0, 0, 0, 1, 0, new BigDecimal("95.00"), "Centro urbano", 0,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private ExternalAiFeedbackService servicio(String url) {
        return new ExternalAiFeedbackService(new ObjectMapper(), "openai", url,
                "clave-de-prueba", "modelo-test", 5);
    }

    private void responder(String cuerpo, String contentType) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        servidor.createContext("/chat", intercambio -> {
            intercambio.getResponseHeaders().set("Content-Type", contentType);
            intercambio.sendResponseHeaders(200, bytes.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(bytes);
            }
        });
        servidor.start();
    }

    private String url() {
        return "http://127.0.0.1:" + servidor.getAddress().getPort() + "/chat";
    }

    @Test
    void interpretaLaRespuestaJsonDelProveedor() throws Exception {
        responder("{\"choices\":[{\"message\":{\"content\":"
                + "\"```json\\n{\\\"resumen\\\":\\\"Buen manejo\\\","
                + "\\\"aciertos\\\":[\\\"Respeta límites\\\"],"
                + "\\\"errores\\\":[],"
                + "\\\"nivelRiesgo\\\":\\\"BAJO\\\","
                + "\\\"recomendaciones\\\":[\\\"Uno\\\",\\\"Dos\\\",\\\"Tres\\\"],"
                + "\\\"mensajeMotivador\\\":\\\"Sigue así\\\"}\\n```\"}}]}",
                "application/json");

        FeedbackIaResponse informe = servicio(url()).generate(datos());

        assertThat(informe.getOrigen()).isEqualTo("OPENAI");
        assertThat(informe.getResumen()).isEqualTo("Buen manejo");
        assertThat(informe.getNivelRiesgo()).isEqualTo("BAJO");
        assertThat(informe.getRecomendaciones()).containsExactly("Uno", "Dos", "Tres");
        // El puntaje siempre lo impone el servidor, nunca el modelo externo.
        assertThat(informe.getPuntaje()).isEqualByComparingTo("95.00");
    }

    @Test
    void lanzaExcepcionCuandoLaRespuestaEsInvalida() throws Exception {
        responder("{\"choices\":[]}", "application/json");

        assertThatThrownBy(() -> servicio(url()).generate(datos()))
                .isInstanceOf(AiUnavailableException.class);
    }

    @Test
    void lanzaExcepcionCuandoNoEstaConfigurado() {
        ExternalAiFeedbackService sinClave = new ExternalAiFeedbackService(
                new ObjectMapper(), "local", "", "", "modelo-test", 5);

        assertThat(sinClave.isEnabled()).isFalse();
        assertThatThrownBy(() -> sinClave.generate(datos()))
                .isInstanceOf(AiUnavailableException.class);
    }

    private void responderCapturando(String cuerpo, AtomicReference<String> bodyCapture) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        servidor.createContext("/chat", intercambio -> {
            InputStream requestBody = intercambio.getRequestBody();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = requestBody.read(chunk)) != -1) {
                buffer.write(chunk, 0, n);
            }
            bodyCapture.set(buffer.toString(StandardCharsets.UTF_8));

            intercambio.getResponseHeaders().set("Content-Type", "application/json");
            intercambio.sendResponseHeaders(200, bytes.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(bytes);
            }
        });
        servidor.start();
    }

    @Test
    void noPiiEnviadoAlProveedorExterno() throws Exception {
        AtomicReference<String> bodyCapture = new AtomicReference<>("");

        String respuestaValida = "{\"choices\":[{\"message\":{\"content\":"
                + "\"```json\\n{\\\"resumen\\\":\\\"OK\\\","
                + "\\\"aciertos\\\":[],\\\"errores\\\":[],"
                + "\\\"nivelRiesgo\\\":\\\"BAJO\\\","
                + "\\\"recomendaciones\\\":[\\\"A\\\"],"
                + "\\\"mensajeMotivador\\\":\\\"Bien\\\"}\\n```\"}}]}";

        responderCapturando(respuestaValida, bodyCapture);
        servicio(url()).generate(datos());

        String payload = bodyCapture.get().toLowerCase();

        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (email)")
                .doesNotContain("email");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (correo)")
                .doesNotContain("correo");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (firstname)")
                .doesNotContain("firstname");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (lastname)")
                .doesNotContain("lastname");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (telefono)")
                .doesNotContain("telefono");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (userid)")
                .doesNotContain("userid");
        assertThat(payload)
                .as("El payload enviado al proveedor externo no debe contener PII (id_usuario)")
                .doesNotContain("id_usuario");
    }
}
