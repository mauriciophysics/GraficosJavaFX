package br.com.mauricioborges.graficos;

import br.com.mauricioborges.graficos.gui.CenaGraficoController;
import br.com.mauricioborges.graficos.math.Funcao;
import static br.com.mauricioborges.graficos.utils.FXUtils.findResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Representa um gráfico. Exemplo de uso:
 *
 * <pre><code>
 * Funcao f = x -> Math.sin(x);
 * Grafico g = new Grafico();
 * g.plotFuncao(f, 0, 2*Math.PI, "Seno");
 * g.show(stage);
 * </code></pre>
 *
 * @author Mauricio Borges
 * @since 2018
 */
public final class Grafico extends Application {

    private CenaGraficoController controle;
    private String tituloJanela = "Gráficos JavaFX";
    private String tituloGrafico = null;
    private String tituloEixoX = null;
    private String tituloEixoY = null;
    // Funcoes
    private final List<Double> inicio = new ArrayList<>();
    private final List<Double> fim = new ArrayList<>();
    private final List<String> titulosFuncoes = new ArrayList<>();
    private final List<Funcao> funcoes = new ArrayList<>();
    // Pontos
    private final List<Double[]> x = new ArrayList<>();
    private final List<Double[]> y = new ArrayList<>();
    private final List<String> titulosPontos = new ArrayList<>();
    private final List<Estilo> estilo = new ArrayList<>();
    private final List<LinhaDeTendencia[]> linhasDeTendencia = new ArrayList<>();

    /**
     * Definir o título da janela
     *
     * @param tituloJanela título da janela
     */
    public void setTituloJanela(String tituloJanela) {
        this.tituloJanela = tituloJanela;
    }

    /**
     * Definir o título do gráfico
     *
     * @param tituloGrafico título do gráfico
     */
    public void setTituloGrafico(String tituloGrafico) {
        this.tituloGrafico = tituloGrafico;
    }

    /**
     * Definir o título do eixo X
     *
     * @param tituloEixoX título do eixo X
     */
    public void setTituloEixoX(String tituloEixoX) {
        this.tituloEixoX = tituloEixoX;
    }

    /**
     * Definir o título do eixo Y
     *
     * @param tituloEixoY título do eixo Y
     */
    public void setTituloEixoY(String tituloEixoY) {
        this.tituloEixoY = tituloEixoY;
    }

    /**
     * Plotar função em determinado intervalo
     *
     * @param funcao função
     * @param inicio início do intervalo
     * @param fim fim do intervalo
     * @param titulo legenda do gráfico
     */
    public void plotFuncao(Funcao funcao, double inicio, double fim, String titulo) {
        Objects.requireNonNull(funcao, "A função não pode ser nula.");
        this.funcoes.add(funcao);
        this.titulosFuncoes.add(titulo);
        this.inicio.add(inicio);
        this.fim.add(fim);

        if (controle != null) {
            controle.plotFuncao(funcao, inicio, fim, titulo);
        }
    }

    /**
     * Plotar um conjunto de pontos
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gráfico
     * @param estilo opções de estilo
     * @param linhasDeTendencia linhas de tendência
     */
    public void plotPontos(Double[] x, Double[] y, String titulo, Estilo estilo, LinhaDeTendencia... linhasDeTendencia) {
        if (x.length != y.length) {
            throw new UnsupportedOperationException("Os arrays de X e Y devem ter o mesmo tamanho.");
        }
        Objects.requireNonNull(estilo, "O estilo não pode ser nulo.");
        this.x.add(x);
        this.y.add(y);
        this.titulosPontos.add(titulo);
        this.linhasDeTendencia.add(linhasDeTendencia);
        this.estilo.add(estilo);

        if (controle != null) {
            controle.plotPontos(x, y, titulo, estilo, linhasDeTendencia);
        }
    }

    /**
     * Plotar um conjunto de pontos com o estilo padrão (com linha e marcador) e
     * sem linha de tendência
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gráfico
     */
    public void plotPontos(Double[] x, Double[] y, String titulo) {
        this.plotPontos(x, y, titulo, Estilo.LINHA_E_MARCADOR);
    }

    /**
     * Plotar um conjunto de pontos sem linha de tendência
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gráfico
     * @param estilo opções de estilo
     */
    public void plotPontos(Double[] x, Double[] y, String titulo, Estilo estilo) {
        this.plotPontos(x, y, titulo, estilo, (LinhaDeTendencia) null);
    }

    /**
     * Plotar um conjunto de pontos com o estilo padrão (com linha e marcador)
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gráfico
     * @param linhasDeTendencia linhas de tendência
     */
    public void plotPontos(Double[] x, Double[] y, String titulo, LinhaDeTendencia... linhasDeTendencia) {
        this.plotPontos(x, y, titulo, Estilo.LINHA_E_MARCADOR, linhasDeTendencia);
    }

    /**
     * Exibir o gráfico.<br>
     * Mesmo que chamar o método <code>start(Stage janela)</code>
     *
     * @param janela janela onde o gráfico será exibido
     */
    public final void show(Stage janela) {
        if (controle == null) {
            this.start(janela);
            return;
        }
        // plotando os gráficos
        for (int i = 0; i < funcoes.size(); i++) {
            controle.plotFuncao(funcoes.get(i), inicio.get(i), fim.get(i), titulosFuncoes.get(i));
        }
        for (int i = 0; i < x.size(); i++) {
            controle.plotPontos(x.get(i), y.get(i), titulosPontos.get(i), estilo.get(i), linhasDeTendencia.get(i));
        }
    }

    /**
     * Exibir o gráfico.<br>
     * Mesmo que chamar o método <code>show(Stage janela)</code>
     *
     * @param janela janela onde o gráfico será exibido
     */
    @Override
    public final void start(Stage janela) {
        Objects.requireNonNull(janela, "A janela não pode ser nula.");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(findResource("gui/CenaGrafico.fxml"));
        Parent root;
        try {
            root = loader.load();
            controle = loader.getController();
            Scene scene = new Scene(root);
            janela.setTitle(tituloJanela);
            janela.setScene(scene);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao tentar iniciar a janela do gráfico.", ex);
        }

        // adicionando o icone da janela
        Image icone = new Image(findResource("gui/img/iconeJava.png").toString());
        janela.getIcons().add(icone);

        // titulo do gráfico no controller
        controle.setTituloGrafico(tituloGrafico);
        // titulo dos eixos no controller
        controle.setTituloEixos(tituloEixoX, tituloEixoY);

        // configurando e exibindo a janela
        janela.setOnCloseRequest(event -> System.exit(0));
        janela.show();

        // plotando os gráficos
        this.show(janela);
    }

    /**
     * Estilos para gráficos
     */
    public static enum Estilo {
        /**
         * Apenas linha
         */
        LINHA,
        /**
         * Apenas marcador
         */
        MARCADOR,
        /**
         * Linha e marcador
         */
        LINHA_E_MARCADOR
    }

    /**
     * Linha de tendência para gráficos de conjuntos de pontos
     */
    public static class LinhaDeTendencia {

        /**
         * Linear (polinomial de grau 1)
         */
        public static LinhaDeTendencia LINEAR = new LinhaDeTendencia(1);
        /**
         * Quadrática (polinomial de grau 2)
         */
        public static LinhaDeTendencia QUADRATICA = new LinhaDeTendencia(2);

        private int grau = 1;
        private String titulo = null;
        private double b0 = Double.MAX_VALUE;

        private boolean exibirEquacao = true;
        private boolean exibirR2 = false;
        private boolean exibirSigma2 = false;

        /**
         * Construtor
         *
         * @param grau grau do polinômio
         */
        public LinhaDeTendencia(int grau) {
            this.setGrau(grau);
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
            if (grau >= 0) {
                this.grau = grau;
            }
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
         * Builder para construir uma linha de tendência com os parâmetros
         * desejados
         */
        public static class Builder {

            private final LinhaDeTendencia linhaDeTendencia = new LinhaDeTendencia(1);

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

}
