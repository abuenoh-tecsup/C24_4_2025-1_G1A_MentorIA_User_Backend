package com.tecsup.demo.content.integration_ia;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService {

    public enum PromptType {
        SUMMARY,
        FLASHCARDS
    }

    public String buildPrompt(PromptType type, String textPlain) {
        return switch (type) {
            case SUMMARY -> buildSummaryPrompt(textPlain);
            case FLASHCARDS -> buildFlashcardsPrompt(textPlain);
        };
    }

    private String buildSummaryPrompt(String text) {
        return String.format("""
        Actúa como un asistente de estudio. Resume el siguiente texto de manera clara y didáctica en formato Markdown, con el objetivo de facilitar su estudio.
    
        - Si el texto incluye varios temas o subtítulos, divídelos usando encabezados (`##` o `###`) según corresponda.
        - Usa listas con viñetas (`-`) para explicar conceptos clave.
        - El lenguaje debe ser simple y directo, como si estuvieras creando apuntes útiles para repasar.
        
        No incluyas introducciones ni despedidas. La salida debe ser estrictamente en formato Markdown, sin explicaciones adicionales.
    
        Texto:
        %s
        """, text);
    }



    private String buildFlashcardsPrompt(String text) {
        return String.format("""
    Actúa como un asistente educativo y generador de tarjetas de estudio (flashcards) para estudiantes.

    Lee el siguiente texto y extrae preguntas clave que ayuden a comprender y memorizar los conceptos más importantes. Las preguntas pueden ser definiciones, relaciones causa-efecto, diferencias entre conceptos o explicaciones breves.

    Genera entre 5 y 10 tarjetas que cubran los puntos más relevantes del texto.

    El formato de salida debe ser un arreglo JSON, con objetos que contengan "question" y "answer", como el siguiente ejemplo:

    [
      { "question": "¿Qué es un átomo?", "answer": "Es la unidad fundamental de los elementos químicos, compuesta por un núcleo y una nube de electrones." },
      { "question": "¿Cuál es la diferencia entre un protón y un electrón?", "answer": "El protón tiene carga positiva y se encuentra en el núcleo; el electrón tiene carga negativa y orbita alrededor del núcleo." }
    ]

    Usa un lenguaje claro y directo. Limita la extensión de cada respuesta a 2 o 3 líneas como máximo. No incluyas explicaciones complejas o tecnicismos innecesarios. No agregues ningún texto fuera del JSON.

    Texto:
    %s
    """, text);
    }


}
