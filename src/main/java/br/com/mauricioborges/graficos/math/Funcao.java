package br.com.mauricioborges.graficos.math;

/**
 * Interface para declaração de funções através de expressões lambda
 *
 * @author Mauricio Borges
 * @since 2018
 */
public interface Funcao {

    /**
     * Função matemática
     *
     * @param x parâmetro
     * @return resultado da expressão matemática
     */
    public abstract double f(double x);
}
