package com.develop.nvh;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraTest {
    
    private final Calculadora calculadora = new Calculadora();

    @Test
    public void testMultiplicar() {
        assertEquals(6, calculadora.multiplicar(2, 3));
    }
    @Test
    public void testDividir() {
        assertEquals(2.0, calculadora.dividir(4, 2));
    }
    @Test
    public void testEsPar() {
        assertTrue(calculadora.esPar(4));
        assertFalse(calculadora.esPar(3));
    }
}