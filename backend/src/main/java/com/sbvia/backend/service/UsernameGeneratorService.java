package com.sbvia.backend.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that generates deterministic, normalised and unique usernames,
 * inspired by the UTEQ SGA standard (Sistema de Gestión Académica):
 * first-name initial + first surname + second-surname initial (e.g. jcruzp).
 *
 * It meets the database constraints:
 * - longitud mínima >= 4 (chk_usuario_nombre_usuario)
 * - longitud máxima &lt;= 60 (VARCHAR(60) en user.nombre_usuario)
 * - alphanumeric characters safe for login
 *
 * @author Keitho_
 */
@Service
public class UsernameGeneratorService {

    private static final Set<String> PARTICULAS_APELLIDO = Set.of(
            "de", "del", "la", "las", "los", "san", "santa"
    );

    /**
     * Normalises a text by removing accents and diacritics (e.g. ñ -> n),
     * caracteres especiales, espacios extras y convirtiendo a minúsculas.
     *
     * @param texto a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        // Unicode canonical decomposition (NFD) to separate base letters from accents
        String descompuesto = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        // Eliminar marcas diacríticas
        String sinDiacriticos = descompuesto.replaceAll("\\p{M}", "");
        // Keep only alphanumeric characters and spaces
        String soloAlfanumerico = sinDiacriticos.replaceAll("[^a-zA-Z0-9\\s]", " ");
        // Reducir espacios múltiples y pasar a minúsculas
        return soloAlfanumerico.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Builds the username base from the first and last names.
     * Formato UTEQ/SGA:
     * - Initial letter of the first name
     * - First surname, ignoring particles such as 'de' or 'la', or merging them when needed
     * - Initial letter of the second surname, when present
     *
     * Ejemplo:
     * "Justyn Keith", "Cruz Perez" -> "jcruzp"
     * "Ana", "Li"                  -> "anli" (expandido a >= 4 caracteres)
     * "José Ángel", "Muñoz"        -> "jmunoz"
     *
     * @param firstName a {@link java.lang.String} object
     * @param lastName a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String generateBase(String firstName, String lastName) {
        String normNombres = normalizar(firstName);
        String normApellidos = normalizar(lastName);

        String[] tokensNombres = normNombres.isEmpty() ? new String[0] : normNombres.split(" ");
        String[] tokensApellidos = normApellidos.isEmpty() ? new String[0] : normApellidos.split(" ");

        String primerNombre = tokensNombres.length > 0 ? tokensNombres[0] : "user";
        // When both fields are empty, return the UTEQ default username
        if (normNombres.isEmpty() && normApellidos.isEmpty()) {
            return "user0";
        }
        String inicialNombre = primerNombre.substring(0, 1);

        // Procesar lastName considerando partículas comunes en español
        List<String> apellidosLimpios = new ArrayList<>();
        for (int i = 0; i < tokensApellidos.length; i++) {
            String token = tokensApellidos[i];
            if (PARTICULAS_APELLIDO.contains(token) && (i + 1 < tokensApellidos.length)) {
                // When it reads 'de la cruz', it joins the particle to the surname, giving 'delacruz'
                // or keeps the stem according to the remaining tokens
                StringBuilder compuesto = new StringBuilder(token);
                while (i + 1 < tokensApellidos.length && PARTICULAS_APELLIDO.contains(tokensApellidos[i + 1])) {
                    i++;
                    compuesto.append(tokensApellidos[i]);
                }
                if (i + 1 < tokensApellidos.length) {
                    i++;
                    String apellidoToken = tokensApellidos[i];
                    // Avoid a doubled letter at the particle-surname boundary
                    // E.g. "delos" plus "santos" gives "delosantos", not "delossantos"
                    if (compuesto.length() > 0 && !apellidoToken.isEmpty()
                            && compuesto.charAt(compuesto.length() - 1) == apellidoToken.charAt(0)) {
                        compuesto.append(apellidoToken.substring(1));
                    } else {
                        compuesto.append(apellidoToken);
                    }
                }
                apellidosLimpios.add(compuesto.toString());
            } else if (!token.isBlank()) {
                apellidosLimpios.add(token);
            }
        }

        String primerApellido;
        String inicialSegundoApellido = "";

        if (apellidosLimpios.isEmpty()) {
            // With no surname, the base is the first 4 characters of the name
            // E.g. "Justyn" gives "just". The initial is not used on its own, because that would give "j" + "justyn" = "jjustyn"
            String baseNombre = primerNombre.substring(0, Math.min(primerNombre.length(), 4));
            while (baseNombre.length() < 4) {
                baseNombre = baseNombre + "0";
            }
            if (baseNombre.length() > 50) {
                baseNombre = baseNombre.substring(0, 50);
            }
            return baseNombre;
        } else {
            primerApellido = apellidosLimpios.get(0);
            if (apellidosLimpios.size() > 1) {
                String segundo = apellidosLimpios.get(1);
                if (!segundo.isEmpty()) {
                    inicialSegundoApellido = segundo.substring(0, 1);
                }
            }
        }

        String base = inicialNombre + primerApellido + inicialSegundoApellido;

        // Enforce the database constraint: minimum length of 4 characters
        if (base.length() < 4) {
            // Try to take more characters from the first name
            if (primerNombre.length() > 1) {
                int letrasFaltantes = 4 - base.length();
                int endIndex = Math.min(primerNombre.length(), 1 + letrasFaltantes);
                String prefijoExtendido = primerNombre.substring(0, endIndex);
                base = prefijoExtendido + primerApellido + inicialSegundoApellido;
            }
            // When still under 4 (e.g. name "A", surname "Li"), pad it safely
            while (base.length() < 4) {
                base = base + "0";
            }
        }

        // Cap the base length at 50 characters to leave room for numeric suffixes
        // (PostgreSQL has VARCHAR(60))
        if (base.length() > 50) {
            base = base.substring(0, 50);
        }

        return base;
    }

    /**
     * Works out the next available username from a list of existing ones.
     * If 'base' does not exist, it returns 'base'.
     * If 'base' already exists, it generates 'base1', 'base2', and so on.
     *
     * @param base a {@link java.lang.String} object
     * @param existentes a {@link java.util.Collection} object
     * @return a {@link java.lang.String} object
     */
    public String generateNextAvailable(String base, Collection<String> existentes) {
        if (existentes == null || existentes.isEmpty()) {
            return base;
        }

        Set<String> existentesSet = new HashSet<>();
        for (String exist : existentes) {
            if (exist != null) {
                existentesSet.add(exist.trim().toLowerCase(Locale.ROOT));
            }
        }

        String baseLower = base.toLowerCase(Locale.ROOT);
        if (!existentesSet.contains(baseLower)) {
            return baseLower;
        }

        // Find the highest available numeric suffix
        Pattern pattern = Pattern.compile("^" + Pattern.quote(baseLower) + "(\\d*)$");
        int maxNumero = 0;
        boolean baseSolaExiste = false;

        for (String exist : existentesSet) {
            Matcher matcher = pattern.matcher(exist);
            if (matcher.matches()) {
                String numStr = matcher.group(1);
                if (numStr.isEmpty()) {
                    baseSolaExiste = true;
                } else {
                    try {
                        int num = Integer.parseInt(numStr);
                        if (num > maxNumero) {
                            maxNumero = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        int siguiente = (maxNumero == 0 && baseSolaExiste) ? 1 : (maxNumero + 1);
        return baseLower + siguiente;
    }
}
