package br.com.mauricioborges.graficos.gui;

import br.com.mauricioborges.graficos.gui.Grafico.Estilo;
import static br.com.mauricioborges.graficos.gui.Grafico.Estilo.LINHA;
import static br.com.mauricioborges.graficos.gui.Grafico.Estilo.LINHA_E_MARCADOR;
import static br.com.mauricioborges.graficos.gui.Grafico.Estilo.MARCADOR;
import br.com.mauricioborges.graficos.math.Funcao;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

/**
 * Controlador do FXML.
 *
 * @author Mauricio Borges
 * @since 2018
 */
class CenaGraficoController implements Initializable {

    @FXML
    private NumberAxis eixoX;
    @FXML
    private NumberAxis eixoY;
    @FXML
    private LineChart<Number, Number> graficoLinhas;

    public void setTituloEixos(String tituloEixoX, String tituloEixoY) {
        eixoX.setLabel(tituloEixoX);
        eixoY.setLabel(tituloEixoY);
    }

    public void plotarFuncoes(Funcao funcao, String nome, double inicio, double fim) {
        XYChart.Series<Number, Number> dados = new XYChart.Series<>();
        dados.setName(nome);
        //Expression exFuncao = new ExpressionBuilder(funcao).variables("x").build();
        double dx = (fim - inicio) / 8e2;
        double fxAnt = funcao.f(inicio - dx);
        boolean erro;
        for (double i = inicio; i <= fim; i += dx) {
            //double fx = exFuncao.setVariable("x", i).evaluate();
            erro = false;
            double fx;
            try {
                fx = funcao.f(i);
                if (Double.isInfinite(fx) || Double.isNaN(fx) || Math.abs(fx - fxAnt) > 1e2) {
                    //fxAnt = fx;
                    //continue;
                    erro = true;
                }
                fxAnt = fx;
            } catch (Exception e) {
                //System.err.println(e.getMessage());
                continue;
            }
            if (!erro) {
                dados.getData().add(new XYChart.Data<>(i, fx));
            }
        }
        this.graficoLinhas.getData().add(dados);
        // diminui o tamanho das bolinhas do gráfico para 1px
        for (int i = 0; i < dados.getData().size(); i++) {
            //dados.getData().get(i).setNode(null); -> tira as bolinhas do gráfico
            dados.getData().get(i).getNode().lookup(".chart-line-symbol").setStyle("-fx-padding: 1px;");
        }
        // tira a linha que liga as bolinhas
        this.graficoLinhas.lookup(".default-color" + (this.graficoLinhas.getData().size() - 1) + ".chart-series-line").setStyle("-fx-stroke: rgba(" + 0 + ", " + 0 + ", " + 0 + ", 0.0);");
    }

    public void plotarPontos(Double[] x, Double[] y, String nome, Estilo estilo) {
        XYChart.Series<Number, Number> dados = new XYChart.Series<>();
        dados.setName(nome);
        for (int i = 0; i < x.length; i++) {
            dados.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        this.graficoLinhas.getData().add(dados);

        // estilo do gráfico
        switch (estilo) {
            case LINHA:
                for (int i = 0; i < dados.getData().size(); i++) {
                    dados.getData().get(i).setNode(null); // tira as bolinhas do gráfico
                    // diminui o tamanho das bolinhas do gráfico para 1px
                    // dados.getData().get(i).getNode().lookup(".chart-line-symbol").setStyle("-fx-padding: 1px;");
                }
                break;
            case LINHA_E_MARCADOR:
                break;
            case MARCADOR:
                // tira a linha que liga as bolinhas
                this.graficoLinhas.lookup(".default-color" + (this.graficoLinhas.getData().size() - 1) + ".chart-series-line").setStyle("-fx-stroke: rgba(" + 0 + ", " + 0 + ", " + 0 + ", 0.0);");
                break;
        }

        // ligar ou não as bolinhas de cada ponto no gráfico
        /*if (usarLinha.equals(Linha.NAO)) {
            this.graficoLinhas.lookup(".default-color" + (this.graficoLinhas.getData().size() - 1) + ".chart-series-line").setStyle("-fx-stroke: rgba(" + 0 + ", " + 0 + ", " + 0 + ", 0.0);");
        } else {
            // diminui o tamanho das bolinhas do gráfico para 1px
            for (int i = 0; i < dados.getData().size(); i++) {
                //dados.getData().get(i).setNode(null); -> tira as bolinhas do gráfico
                dados.getData().get(i).getNode().lookup(".chart-line-symbol").setStyle("-fx-padding: 1px;");
            }
        }*/
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.graficoLinhas.setCreateSymbols(true);

        // exibir valores nos eixos do gráfico em notação científica
        StringConverter<Number> sf = new StringConverter<Number>() {
            private DecimalFormat format;

            @Override
            public String toString(Number number) {
                if (Math.abs(number.doubleValue()) > 1e3) {
                    format = new DecimalFormat("0.##E0");
                } else {
                    format = new DecimalFormat("0.##");
                }
                return format.format(number.doubleValue()).replaceAll("E0", "");
            }

            @Override
            public Number fromString(String string) {
                try {
                    return format.parse(string);
                } catch (ParseException e) {
                    return 0;
                }
            }

        };
        this.eixoX.setTickLabelFormatter(sf);
        this.eixoY.setTickLabelFormatter(sf);
    }

}
