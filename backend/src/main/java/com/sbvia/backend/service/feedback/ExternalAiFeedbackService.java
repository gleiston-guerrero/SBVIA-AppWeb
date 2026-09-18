package com.sbvia.backend.service.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbvia.backend.dto.FeedbackIaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExternalAiFeedbackService implements FeedbackProvider {

    public static final String ORIGIN = "OPENAI";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String provider;
    private final String apiUrl;
    private final String apiKey;
    private final String model;

    public ExternalAiFeedbackService(
            ObjectMapper objectMapper,
            @Value("${ia.proveedor:local}") String provider,
            @Value("${ia.api-url:}") String apiUrl,
            @Value("${ia.api-key:}") String apiKey,
            @Value("${ia.modelo:gpt-4o-mini}") String model,
            @Value("${ia.timeout-segundos:15}") int timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.provider = provider;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutSeconds * 1000);
        factory.setReadTimeout(timeoutSeconds * 1000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    public String origin() {
        return ORIGIN;
    }

    public boolean isEnabled() {
        return "openai".equalsIgnoreCase(provider) && apiKey != null && !apiKey.isBlank()
                && apiUrl != null && !apiUrl.isBlank();
    }

    @Override
    @SuppressWarnings("unchecked")
    public FeedbackIaResponse generate(DrivingData data) {
        if (!isEnabled()) {
            throw new AiUnavailableException("Proveedor externo no configurado (ia.proveedor=openai + AI_API_KEY + AI_API_URL)");
        }
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "temperature", 0.3,
                    "max_tokens", 2048,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "Eres un instructor de conducción. Responde SOLO con un JSON válido con las claves: "
                                            + "resumen (string), aciertos (array de strings), errores (array de strings), "
                                            + "nivelRiesgo (BAJO, MEDIO o ALTO), recomendaciones (array de exactamente 3 strings), "
                                            + "mensajeMotivador (string). Sin texto fuera del JSON."),
                            Map.of("role", "user", "content", userPrompt(data))));
            Map<?, ?> response = restClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return mapResponse(response, data);
        } catch (AiUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new AiUnavailableException("Fallo la llamada al proveedor externo", e);
        }
    }

    private String userPrompt(DrivingData d) {
        return "Evalúa esta práctica de simulación (métricas agregadas, sin datos personales): "
                + "scenario=" + d.scenarioName()
                + ", durationSeconds=" + d.durationSeconds()
                + ", velocidadPromedio=" + d.averageSpeed()
                + ", velocidadMaxima=" + d.maxSpeed()
                + ", excesos=" + d.speedingIncidents()
                + ", colisiones=" + d.collisions()
                + ", salidas=" + d.laneDepartures()
                + ", semaforosIgnorados=" + d.ignoredRedLights()
                + ", semaforosRespetados=" + d.respectedRedLights()
                + ", distanciaInsegura=" + d.unsafeDistanceIncidents()
                + ", puntaje=" + d.score()
                + ", practicasPrevias=" + d.previousPractices();
    }

    @SuppressWarnings("unchecked")
    private FeedbackIaResponse mapResponse(Map<?, ?> response, DrivingData data) {
        try {
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
            String content = String.valueOf(message.get("content"));
            int start = content.indexOf('{');
            int end = content.lastIndexOf('}');
            if (start != -1 && end != -1 && start <= end) {
                content = content.substring(start, end + 1);
            } else {
                throw new IllegalArgumentException("No se encontró JSON en la respuesta. Contenido crudo: " + content);
            }
            Map<?, ?> json;
            try {
                json = objectMapper.readValue(content, Map.class);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Fallo al parsear JSON. Contenido extraído: " + content, ex);
            }
            return FeedbackIaResponse.builder()
                    .resumen(getText(json.get("resumen"), "Práctica analizada por el modelo externo."))
                    .aciertos(getList(json.get("aciertos")))
                    .errores(getList(json.get("errores")))
                    .nivelRiesgo(getLevel(json.get("nivelRiesgo")))
                    .recomendaciones(getRecs(json.get("recomendaciones")))
                    .puntaje(data.score())
                    .mensajeMotivador(getText(json.get("mensajeMotivador"), "Sigue practicando con constancia."))
                    .comparacion(null)
                    .origen(ORIGIN)
                    .build();
        } catch (Exception e) {
            throw new AiUnavailableException("Respuesta del proveedor externo inválida", e);
        }
    }

    private String getText(Object value, String defaultValue) {
        return value instanceof String s && !s.isBlank() ? s : defaultValue;
    }

    private List<String> getList(Object value) {
        if (value instanceof List<?> l) {
            return l.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private List<String> getRecs(Object value) {
        List<String> base = new ArrayList<>(getList(value));
        while (base.size() < 3) base.add("Mantener la atención plena durante todo el recorrido");
        return base.subList(0, 3);
    }

    private String getLevel(Object value) {
        String n = value instanceof String s ? s.toUpperCase() : "";
        return "BAJO".equals(n) || "MEDIO".equals(n) || "ALTO".equals(n) ? n : "MEDIO";
    }
}
