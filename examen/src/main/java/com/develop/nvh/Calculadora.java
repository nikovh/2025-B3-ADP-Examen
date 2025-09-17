package com.develop.nvh;

public class Calculadora {
    
    // multiplicar dos numeros
    public int multiplicar(int a, int b) {
        return a * b;
    }

    // dividir dos numeros
    public double dividir(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return (double) a / b;
    }

    // verificar si es par
    public boolean esPar(int numero) {
        return numero % 2 == 0;
    }
}
