package com.develop.nvh;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraIT {

    @Test
    void testMultiplicarScenario() {
        // Arrange: Creamos la instancia de nuestra clase.
        Calculadora calculadora = new Calculadora();
        int value = 8;
        int multiplier = 10;

        // Act: Llamamos al método de multiplicación.
        int result = calculadora.multiplicar(value, multiplier);

        // Assert: Verificamos el resultado.
        assertEquals(80, result, "8 * 10 debería ser 80");
    }
}
