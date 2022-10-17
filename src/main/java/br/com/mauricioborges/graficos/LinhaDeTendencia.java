package br.com.mauricioborges.graficos;

import java.util.Objects;

/**
 * Linha de tendência para gráficos de conjuntos de pontos
 *
 * @author Mauricio Borges
 * @since 10/2022
 */
public class LinhaDeTendencia {

    /**
     * Linear (polinomial de grau 1)
     */
    public static LinhaDeTendencia LINEAR = new LinhaDeTendencia.Builder(Tipo.POLINOMIAL).setGrau(1).build();
    /**
     * Quadrática (polinomial de grau 2)
     */
    public static LinhaDeTendencia QUADRATICA = new LinhaDeTendencia.Builder(Tipo.POLINOMIAL).setGrau(2).build();
    /**
     * Exponencial
     */
    public static LinhaDeTendencia EXPONENCIAL = new LinhaDeTendencia.Builder(Tipo.EXPONENCIAL).build();
    /**
     * Logarítmica
     */
    public static LinhaDeTendencia LOGARITMICA = new LinhaDeTendencia.Builder(Tipo.LOGARITMICA).build();

    private final Tipo tipo;
    private int grau = 1;
    private String titulo = null;
    private double b0 = Double.MAX_VALUE;

    private boolean exibirEquacao = true;
    private boolean exibirR2 = false;
    private boolean exibirSigma2 = false;

    private LinhaDeTendencia(Tipo tipo) {
        this.tipo = Objects.requireNonNull(tipo, "O tipo da linha de tendência não pode ser nulo.");
    }

    /**
     * Obter o tipo da linha de tendência
     *
     * @return tipo
     */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Obter o grau do polinômio
     *
     * @return grau
     */
    public int getGrau() {
        return grau;
    }

    /**
     * Definir o grau do polinômio
     *
     * @param grau grau
     */
    public void setGrau(int grau) {
        if (grau < 0) {
            throw new IllegalArgumentException("O grau da linha de tendência não pode ser negativo.");
        }
        if (tipo != Tipo.POLINOMIAL) {
            throw new IllegalArgumentException("Só é possível alterar o grau de linhas de tendência polinomiais.");
        }
        this.grau = grau;
    }

    /**
     * Obter o título da linha de tendência
     *
     * @return título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Definir o título da linha de tendência
     *
     * @param titulo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obter o ponto de intersecção com o eixo Y
     *
     * @return valor
     */
    public double getB0() {
        return b0;
    }

    /**
     * Definir o ponto de intersecção com o eixo Y
     *
     * @param b0 valor
     */
    public void setB0(double b0) {
        if (tipo == Tipo.LOGARITMICA) {
            throw new IllegalArgumentException("Não é possível alterar o ponto de "
                    + "intersecção com o eixo Y de linhas de tendência logarítmicas.");
        }
        this.b0 = b0;
    }

    /**
     * Exibir ou não a equação da linha de tendência
     *
     * @return true or false
     */
    public boolean exibirEquacao() {
        return exibirEquacao;
    }

    /**
     * Exibir ou não a equação da linha de tendência (padrão é true)
     *
     * @param exibirEquacao true or false
     */
    public void setExibirEquacao(boolean exibirEquacao) {
        this.exibirEquacao = exibirEquacao;
    }

    /**
     * Exibir ou não o coeficiente de determinação
     *
     * @return true or false
     */
    public boolean exibirR2() {
        return exibirR2;
    }

    /**
     * Exibir ou não o coeficiente de determinação (padrão é false)
     *
     * @param exibirR2 true or false
     */
    public void setExibirR2(boolean exibirR2) {
        this.exibirR2 = exibirR2;
    }

    /**
     * Exibir ou não a variância residual
     *
     * @return true or false
     */
    public boolean exibirSigma2() {
        return exibirSigma2;
    }

    /**
     * Exibir ou não a variância residual (padrão é false)
     *
     * @param exibirSigma2 true or false
     */
    public void setExibirSigma2(boolean exibirSigma2) {
        this.exibirSigma2 = exibirSigma2;
    }

    /**
     * Tipos de linha de tendência
     */
    public static enum Tipo {
        /**
         * Linha de tendência polinomial
         */
        POLINOMIAL,
        /**
         * Linha de tendência exponencial
         */
        EXPONENCIAL,
        /**
         * Linha de tendência logarítmica
         */
        LOGARITMICA
    }

    /**
     * Builder para construir uma linha de tendência com os parâmetros desejados
     */
    public static class Builder {

        private final LinhaDeTendencia linhaDeTendencia;

        /**
         * Para iniciar construção da linha de tendência, é necessário informar
         * o tipo
         *
         * @param tipo tipo da linha de tendência
         */
        public Builder(Tipo tipo) {
            this.linhaDeTendencia = new LinhaDeTendencia(tipo);
        }

        /**
         * Definir o grau do polinômio
         *
         * @param grau grau
         * @return a própria instância do Builder
         */
        public Builder setGrau(int grau) {
            this.linhaDeTendencia.setGrau(grau);
            return this;
        }

        /**
         * Definir o título da linha de tendência
         *
         * @param titulo título
         * @return a própria instância do Builder
         */
        public Builder setTitulo(String titulo) {
            this.linhaDeTendencia.setTitulo(titulo);
            return this;
        }

        /**
         * Definir o ponto de intersecção com o eixo Y
         *
         * @param b0 valor
         * @return a própria instância do Builder
         */
        public Builder setB0(double b0) {
            this.linhaDeTendencia.setB0(b0);
            return this;
        }

        /**
         * Exibir ou não a equação da linha de tendência (padrão é true)
         *
         * @param exibirEquacao true or false
         * @return a própria instância do Builder
         */
        public Builder setExibirEquacao(boolean exibirEquacao) {
            this.linhaDeTendencia.setExibirEquacao(exibirEquacao);
            return this;
        }

        /**
         * Exibir ou não o coeficiente de determinação (padrão é false)
         *
         * @param exibirR2 true or false
         * @return a própria instância do Builder
         */
        public Builder setExibirR2(boolean exibirR2) {
            this.linhaDeTendencia.setExibirR2(exibirR2);
            return this;
        }

        /**
         * Exibir ou não a variância residual (padrão é false)
         *
         * @param exibirSigma2 true or false
         * @return a própria instância do Builder
         */
        public Builder setExibirSigma2(boolean exibirSigma2) {
            this.linhaDeTendencia.setExibirSigma2(exibirSigma2);
            return this;
        }

        /**
         * Constrói o objeto
         *
         * @return linha de tendência com os parâmetros desejados
         */
        public LinhaDeTendencia build() {
            return this.linhaDeTendencia;
        }
    }
}
