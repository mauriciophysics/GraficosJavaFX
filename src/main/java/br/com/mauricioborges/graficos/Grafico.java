package br.com.mauricioborges.graficos;

import br.com.mauricioborges.graficos.gui.CenaGraficoController;
import br.com.mauricioborges.graficos.math.Funcao;
import static br.com.mauricioborges.graficos.utils.FXUtils.findResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;

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
    private String tituloEixoX = "Eixo X";
    private String tituloEixoY = "Eixo Y";
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

    /**
     * Definir o título da janela
     *
     * @param tituloJanela título da janela
     */
    public void setTituloJanela(String tituloJanela) {
        this.tituloJanela = tituloJanela;
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
     */
    public void plotPontos(Double[] x, Double[] y, String titulo, Estilo estilo) {
        if (x.length != y.length) {
            throw new UnsupportedOperationException("Os arrays de X e Y devem ter o mesmo tamanho.");
        }
        this.x.add(x);
        this.y.add(y);
        this.titulosPontos.add(titulo);
        this.estilo.add(estilo);

        if (controle != null) {
            controle.plotPontos(x, y, titulo, estilo);
        }
    }

    /**
     * Plotar um conjunto de pontos com o estilo padrão (com linha e marcador)
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gráfico
     */
    public void plotPontos(Double[] x, Double[] y, String titulo) {
        this.plotPontos(x, y, titulo, Estilo.LINHA_E_MARCADOR);
    }

    private double refatorarConstantes(String num) {
        String[] encontrar = new String[6];
        encontrar[0] = "infinito";
        encontrar[1] = "-infinito";
        encontrar[2] = "pi";
        encontrar[3] = "-pi";
        encontrar[4] = "e";
        encontrar[5] = "-e";

        Double[] substituir = new Double[6];
        substituir[0] = 1e6;
        substituir[1] = -1e6;
        substituir[2] = Math.PI;
        substituir[3] = -Math.PI;
        substituir[4] = Math.E;
        substituir[5] = -Math.E;

        StringBuffer sv = new StringBuffer();
        sv.append(num);
        StringBuffer sn = new StringBuffer();

        Pattern p;
        Matcher m;
        for (int i = 0; i < encontrar.length; i++) {
            if (i > 0) {
                sv = sn;
            }
            sn = new StringBuffer();
            p = Pattern.compile(encontrar[i]);
            m = p.matcher(sv);
            while (m.find()) {
                m.appendReplacement(sn, String.valueOf(substituir[i]));
            }
            m.appendTail(sn);
        }
        //return Double.parseDouble(sn.toString());
        //Expression e = new ExpressionBuilder(sn.toString()).build();
        //return e.evaluate();
        return 0;
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
        for (int i = 0; i < funcoes.size(); i++) {
            controle.plotFuncao(funcoes.get(i), inicio.get(i), fim.get(i), titulosFuncoes.get(i));
        }
        for (int i = 0; i < x.size(); i++) {
            controle.plotPontos(x.get(i), y.get(i), titulosPontos.get(i), estilo.get(i));
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
            //System.err.println("ERRO: " + ex.getMessage());
        }

        Image icone = new Image(findResource("gui/img/iconeJava.png").toString());
        janela.getIcons().add(icone);

        controle.setTituloEixos(tituloEixoX, tituloEixoY);

        janela.show();

        this.show(janela);
    }

    /**
     * Estilos para gráficos
     */
    public enum Estilo {
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

}
