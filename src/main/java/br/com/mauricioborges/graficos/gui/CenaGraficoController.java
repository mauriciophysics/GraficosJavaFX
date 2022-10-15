package br.com.mauricioborges.graficos.gui;

import br.com.mauricioborges.graficos.Grafico.Estilo;
import static br.com.mauricioborges.graficos.Grafico.Estilo.LINHA;
import static br.com.mauricioborges.graficos.Grafico.Estilo.LINHA_E_MARCADOR;
import static br.com.mauricioborges.graficos.Grafico.Estilo.MARCADOR;
import br.com.mauricioborges.graficos.Grafico.LinhaDeTendencia;
import br.com.mauricioborges.graficos.math.Funcao;
import br.com.mauricioborges.graficos.math.metodosnumericos.RegressaoLinearMultipla;
import br.com.mauricioborges.graficos.utils.FileUtils;
import br.com.mauricioborges.graficos.utils.FileUtils.DiretorioInicial;
import br.com.mauricioborges.graficos.utils.FileUtils.Tipo;
import static br.com.mauricioborges.graficos.utils.TextUtils.sup;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.pow;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;

/**
 * Controlador do FXML.
 *
 * @author Mauricio Borges
 * @since 2018
 */
public class CenaGraficoController implements Initializable {

    @FXML
    private NumberAxis eixoX;
    @FXML
    private NumberAxis eixoY;
    @FXML
    private LineChart<Number, Number> graficoLinhas;
    @FXML
    private StackPane stackPane;

    /**
     * Definir o título do gráfico
     *
     * @param tituloGrafico título do gráfico
     */
    public void setTituloGrafico(String tituloGrafico) {
        this.graficoLinhas.setTitle(tituloGrafico);
    }

    /**
     * Definir o título dos eixos X e Y
     *
     * @param tituloEixoX título do eixo X
     * @param tituloEixoY título do eixo Y
     */
    public void setTituloEixos(String tituloEixoX, String tituloEixoY) {
        eixoX.setLabel(tituloEixoX);
        eixoY.setLabel(tituloEixoY);
    }

    /**
     * Salvar uma imagem do gráfico
     *
     * @param destino local onde a imagem deve ser salva
     */
    private void saveSnapshot(File destino) {
        try {
            ImageIO.write(fromFXImage(this.stackPane.snapshot(null, null), null), "png", destino);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao gerar a imagem do gráfico.", ex);
        }
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
        new Thread(() -> {
            XYChart.Series<Number, Number> dados = new XYChart.Series<>();
            dados.setName(titulo);
            double dx = (fim - inicio) / 1400; // número de pontos a ser calculados e adicionados
            double fxAnt = funcao.f(inicio - dx);
            boolean erro;
            for (double i = inicio; i <= fim; i += dx) {
                erro = false;
                double fx;
                try {
                    fx = funcao.f(i);
                    if (Double.isInfinite(fx) || Double.isNaN(fx) || Math.abs(fx - fxAnt) > .1) {
                        // assíntota vertical na função
                        erro = true;
                    }
                    fxAnt = fx;
                } catch (Exception e) {
                    continue;
                }
                if (!erro) {
                    dados.getData().add(new XYChart.Data<>(i, fx));
                }
            }

            Platform.runLater(() -> {
                this.graficoLinhas.getData().add(dados);
                // diminui o tamanho das bolinhas do gráfico para 1px
                for (int i = 0; i < dados.getData().size(); i++) {
                    dados.getData().get(i).getNode().lookup(".chart-line-symbol").setStyle("-fx-padding: 1px;");
                }
                // tira a linha que liga as bolinhas
                this.graficoLinhas.lookup(".default-color" + (this.graficoLinhas.getData().size() - 1) + ".chart-series-line").setStyle("-fx-stroke: rgba(" + 0 + ", " + 0 + ", " + 0 + ", 0.0);");
            });
        }).start();
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
        new Thread(() -> {
            // adicionando os pontos no gráfico
            XYChart.Series<Number, Number> dados = new XYChart.Series<>();
            dados.setName(titulo);
            for (int i = 0; i < x.length; i++) {
                dados.getData().add(new XYChart.Data<>(x[i], y[i]));
            }

            Platform.runLater(() -> {
                this.graficoLinhas.getData().add(dados);

                // estilo do gráfico
                switch (estilo) {
                    case LINHA:
                        for (int i = 0; i < dados.getData().size(); i++) {
                            dados.getData().get(i).setNode(null); // tira as bolinhas do gráfico
                        }
                        break;
                    case LINHA_E_MARCADOR:
                        break;
                    case MARCADOR:
                        // tira a linha que liga as bolinhas
                        this.graficoLinhas.lookup(".default-color" + (this.graficoLinhas.getData().size() - 1) + ".chart-series-line").setStyle("-fx-stroke: rgba(" + 0 + ", " + 0 + ", " + 0 + ", 0.0);");
                        break;
                }
            });

            // gerando as linhas de tendência
            if (linhasDeTendencia != null) {
                for (LinhaDeTendencia linhaDeTendencia : linhasDeTendencia) {
                    if (linhaDeTendencia == null) {
                        continue;
                    }
                    // criando novos arrays de X e Y, pois o algoritmo da
                    // Regressão Linear Múltipla requer que os índices comecem em 1
                    Double[][] xn = new Double[x.length + 1][linhaDeTendencia.getGrau() + 2];
                    Double[] yn = new Double[y.length + 1];
                    for (int i = 0; i < x.length; i++) {
                        xn[i + 1][1] = x[i];
                        yn[i + 1] = y[i];
                    }
                    // método da Regressão Linear Múltipla retorna um array com os
                    // parâmetros da equação de linha de tendência
                    RegressaoLinearMultipla rlm;
                    Double[] b;
                    try {
                        rlm = new RegressaoLinearMultipla(x.length, 1, linhaDeTendencia.getGrau() + 1, xn, yn);
                        b = rlm.solve();
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao gerar a linha de tendência.", e);
                    }

                    // exibindo as informações
                    boolean exibirEquacao = linhaDeTendencia.exibirEquacao();
                    boolean exibirR2 = linhaDeTendencia.exibirR2();
                    boolean exibirSigma2 = linhaDeTendencia.exibirSigma2();
                    if (exibirEquacao || exibirR2 || exibirSigma2) {
                        StringBuilder info = new StringBuilder();
                        Label l = new Label();
                        l.setStyle(l.getStyle() + "-fx-font-size:13px;");
                        l.setCursor(Cursor.MOVE);
                        Platform.runLater(() -> {
                            // posição inicial da label na tela
                            // é necessário uma implementação melhor desta parte!
                            this.stackPane.getChildren().add(l);
                            double h = this.stackPane.getHeight();
                            double w = this.stackPane.getWidth();
                            StackPane.setMargin(l, new Insets(h * 0.15 * stackPane.getChildren().size() - 1, 0, 0, w * 0.4));
                        });
                        l.setOnMouseDragged(event -> Platform.runLater(() -> StackPane.setMargin(l, new Insets(event.getSceneY(), 0, 0, event.getSceneX()))));
                        DecimalFormat df = new DecimalFormat("#.####");
                        if (exibirEquacao) {
                            StringBuilder sb = new StringBuilder("y = ");
                            for (int i = b.length - 1; i >= 1; i--) {
                                boolean mudarB0 = i == 1 && linhaDeTendencia.getB0() != Double.MAX_VALUE;
                                double bi = mudarB0 ? linhaDeTendencia.getB0() : (b[i] == null ? 0 : b[i]);
                                if (i != b.length - 1 && bi > 0) {
                                    sb.append(" + ");
                                } else if (i != b.length - 1 && bi < 0) {
                                    sb.append(" - ");
                                } else if (bi == 0) {
                                    continue;
                                }
                                sb.append(df.format(i != b.length - 1 ? Math.abs(bi) : bi));
                                switch (i) {
                                    case 2:
                                        sb.append("x");
                                        break;
                                    case 1:
                                        break;
                                    default:
                                        sb.append("x").append(sup(i - 1));
                                        break;
                                }
                            }
                            info.append(info.isEmpty() ? "" : "\n").append(sb.toString());
                        }
                        if (exibirR2) {
                            info.append(info.isEmpty() ? "" : "\n").append("r² = ").append(df.format(rlm.getR2()));
                        }
                        if (exibirSigma2) {
                            info.append(info.isEmpty() ? "" : "\n").append("σ² = ").append(df.format(rlm.getSigma2()));
                        }
                        Platform.runLater(() -> l.setText(info.toString()));
                    }

                    // plotando a linha de tendência
                    Funcao f = xf -> {
                        double resultado = 0;
                        for (int i = 1; i < b.length; i++) {
                            if (i == 1 && linhaDeTendencia.getB0() != Double.MAX_VALUE) {
                                resultado += linhaDeTendencia.getB0();
                                continue;
                            }
                            resultado += pow(xf, i - 1) * b[i];
                        }
                        return resultado;
                    };
                    String tituloLinha = linhaDeTendencia.getTitulo() == null ? "Linha de tendência (" + titulo + ")" : linhaDeTendencia.getTitulo();
                    plotFuncao(f, x[0], x[x.length - 1], tituloLinha);
                }
            }
        }).start();
    }

    /**
     * Inicializador do controller
     *
     * @param url url
     * @param rb rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.graficoLinhas.setCreateSymbols(true);

        // adicionando o menu de contexto ao gráfico
        ContextMenu cm = new ContextMenu();
        MenuItem mi = new MenuItem("Salvar imagem");
        mi.setOnAction(event -> {
            File destino = FileUtils.escolherArquivoNoArmazenamento(FileUtils.getDiretorioAtual(), Tipo.SAVE);
            if (destino != null) {
                saveSnapshot(destino);
            }
        });
        cm.getItems().add(mi);
        this.graficoLinhas.setOnContextMenuRequested(event -> cm.show(graficoLinhas, event.getScreenX(), event.getScreenY()));

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
