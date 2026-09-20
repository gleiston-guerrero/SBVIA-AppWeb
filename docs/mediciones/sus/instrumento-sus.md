# Instrumento System Usability Scale (SUS) — Estudio SBVIA

> **Instrumento vigente del estudio de septiembre de 2026.**
> El formulario imprimible es [`Cuestionario-SUS-Participante.pdf`](Cuestionario-SUS-Participante.pdf)
> (LaTeX: [`Cuestionario-SUS-Participante.tex`](Cuestionario-SUS-Participante.tex), formulario v1.0).
> El consentimiento asociado está en
> [`Consentimiento-Informado-SBVIA.pdf`](../../etica/consentimientos/Consentimiento-Informado-SBVIA.pdf).

## 1. Consentimiento informado

El modelo de consentimiento vigente se documenta en `docs/etica/consentimientos/`. El formulario
que se imprime y se firma es `Consentimiento-Informado-SBVIA.pdf`, y quién lo firmó y cuándo consta
en [`registro-aceptacion.md`](../../etica/consentimientos/registro-aceptacion.md).

## 2. Instrumento de medición (SUS)

*System Usability Scale* — Brooke (1996). Diez ítems con escala Likert de 1 a 5
(1 = Muy en desacuerdo, 2 = En desacuerdo, 3 = Neutral, 4 = De acuerdo, 5 = Muy de acuerdo).
Los ítems impares (1, 3, 5, 7, 9) están redactados en positivo y los pares (2, 4, 6, 8, 10) en
negativo.

| Ítem | Afirmación | 1 | 2 | 3 | 4 | 5 |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: |
| 1 | Me gustaría usar este sistema con frecuencia. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 2 | Encontré el sistema innecesariamente complejo. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 3 | Pensé que el sistema era fácil de usar. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 4 | Creo que necesitaría ayuda técnica para poder usar este sistema. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 5 | Encontré que las diversas funciones del sistema estaban bien integradas. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 6 | Pensé que había demasiada inconsistencia en el sistema. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 7 | Imagino que la mayoría de las personas aprendería a usar este sistema muy rápidamente. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 8 | Encontré el sistema muy complicado de usar. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 9 | Me sentí muy seguro(a) al usar el sistema. | [ ] | [ ] | [ ] | [ ] | [ ] |
| 10 | Necesité aprender muchas cosas antes de poder empezar a usar el sistema. | [ ] | [ ] | [ ] | [ ] | [ ] |

### Puntuación

Para cada participante: a los ítems impares se les resta 1 y a los pares se les resta su valor
de 5. La suma de las diez contribuciones (rango 0–40) se multiplica por **2.5**, lo que da una
puntuación de 0 a 100. La implementación verificada está en [`calcular_sus.py`](calcular_sus.py).

### Datos de sesión que se registran

El formulario recoge, como uso interno: fecha de respuesta, duración de la sesión, dispositivo
utilizado y sistema evaluado (URL pública y versión).

### Alcance y límites

El SUS devuelve una medida **percibida** de usabilidad, no un resultado de desempeño. El umbral de
referencia del proyecto (media ≥ 68) corresponde al criterio de aceptación de `RNF-06` y se
interpreta sobre la media de la muestra, nunca sobre respuestas individuales.
