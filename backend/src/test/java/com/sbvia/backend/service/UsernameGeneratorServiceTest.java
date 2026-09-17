package com.sbvia.backend.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsernameGeneratorServiceTest {

    private final UsernameGeneratorService service = new UsernameGeneratorService();

    @Test
    void testNormalizar() {
        assertEquals("", service.normalizar(null));
        assertEquals("", service.normalizar(""));
        assertEquals("nino", service.normalizar("Niño"));
        assertEquals("jose perez", service.normalizar("José  Pérez "));
    }

    @Test
    void testGenerarBase() {
        // Base normal
        assertEquals("jcruzp", service.generateBase("Justyn Keith", "Cruz Perez"));
        // Base muy corta
        assertEquals("anli", service.generateBase("Ana", "Li"));
        // Base sin apellidos
        assertEquals("just", service.generateBase("Justyn", ""));
        assertEquals("user0", service.generateBase("", ""));
        // Particulas
        assertEquals("jdelacruzp", service.generateBase("Juan", "de la Cruz Perez"));
        assertEquals("mdelosantosp", service.generateBase("Maria", "de los Santos Perez"));
        // Base muy larga truncada
        String largoNombre = "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String largoBase = service.generateBase(largoNombre, largoNombre);
        assertEquals(50, largoBase.length());
    }

    @Test
    void testGenerarSiguienteDisponible() {
        assertEquals("jcruzp", service.generateNextAvailable("jcruzp", null));
        assertEquals("jcruzp", service.generateNextAvailable("jcruzp", List.of()));
        assertEquals("jcruzp1", service.generateNextAvailable("jcruzp", List.of("jcruzp")));
        assertEquals("jcruzp2", service.generateNextAvailable("jcruzp", List.of("jcruzp", "jcruzp1")));
        assertEquals("jcruzp", service.generateNextAvailable("jcruzp", List.of("otro")));
        assertEquals("jcruzp3", service.generateNextAvailable("jcruzp", List.of("jcruzp", "jcruzp2")));
    }
}
