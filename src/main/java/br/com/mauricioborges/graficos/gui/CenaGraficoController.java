package br.com.mauricioborges.graficos.gui;

import br.com.mauricioborges.graficos.LinhaDeTendencia;
import br.com.mauricioborges.graficos.Estilo;
import static br.com.mauricioborges.graficos.LinhaDeTendencia.Tipo.EXPONENCIAL;
import static br.com.mauricioborges.graficos.LinhaDeTendencia.Tipo.LOGARITMICA;
import static br.com.mauricioborges.graficos.LinhaDeTendencia.Tipo.MEDIA_MOVEL;
import static br.com.mauricioborges.graficos.LinhaDeTendencia.Tipo.POLINOMIAL;
import static br.com.mauricioborges.graficos.LinhaDeTendencia.Tipo.POTENCIA;
import br.com.mauricioborges.graficos.math.Funcao;
import br.com.mauricioborges.graficos.math.metodosnumericos.RegressaoLinearMultipla;
import br.com.mauricioborges.graficos.utils.ChartUtils;
import br.com.mauricioborges.graficos.utils.FileUtils;
import br.com.mauricioborges.graficos.utils.FileUtils.Tipo;
import static br.com.mauricioborges.graficos.utils.TextUtils.sup;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import static java.util.Arrays.copyOfRange;
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
     * Definir o t??tulo do gr??fico
     *
     * @param tituloGrafico t??tulo do gr??fico
     */
    public void setTituloGrafico(String tituloGrafico) {
        this.graficoLinhas.setTitle(tituloGrafico);
    }

    /**
     * Definir o t??tulo dos eixos X e Y
     *
     * @param tituloEixoX t??tulo do eixo X
     * @param tituloEixoY t??tulo do eixo Y
     */
    public void setTituloEixos(String tituloEixoX, String tituloEixoY) {
        eixoX.setLabel(tituloEixoX);
        eixoY.setLabel(tituloEixoY);
    }

    /**
     * Salvar uma imagem do gr??fico
     *
     * @param destino local onde a imagem deve ser salva
     */
    private void saveSnapshot(File destino) {
        try {
            ImageIO.write(fromFXImage(this.stackPane.snapshot(null, null), null), "png", destino);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao gerar a imagem do gr??fico.", ex);
        }
    }

    /**
     * Plotar fun????o em determinado intervalo
     *
     * @param funcao fun????o
     * @param inicio in??cio do intervalo
     * @param fim fim do intervalo
     * @param titulo legenda do gr??fico
     * @param estilo op????es de estilo
     */
    public void plotFuncao(Funcao funcao, double inicio, double fim, String titulo, Estilo estilo) {
        new Thread(() -> {
            XYChart.Series<Number, Number> dados = new XYChart.Series<>();
            dados.setName(titulo);
            double dx = (fim - inicio) / 1400; // n??mero de pontos a ser calculados e adicionados
            double fxAnt = funcao.apply(inicio - dx);
            boolean erro;
            for (double i = inicio; i <= fim; i += dx) {
                erro = false;
                double fx;
                try {
                    fx = funcao.apply(i);
                    if (Double.isInfinite(fx) || Double.isNaN(fx) || Math.abs(fx - fxAnt) > .1) {
                        // ass??ntota vertical na fun????o
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
                // adicionando a fun????o no gr??fico
                this.graficoLinhas.getData().add(dados);
                // diminui o tamanho das bolinhas do gr??fico
                ChartUtils.setTamanhoMarcador(dados, 1.2);
                // tira a linha que liga as bolinhas
                ChartUtils.setLarguraLinha(dados, 0);
                // configura????es de cor
                if (estilo == null) {
                    return;
                }
                if (estilo.getCor() != null) {
                    // muda a cor do gr??fico
                    ChartUtils.setCor(dados, estilo.getCor());
                }
                // define o estilo da linha
                ChartUtils.setEstiloLinha(dados, estilo.getEstiloLinha());
            });
        }).start();
    }

    /**
     * Plotar um conjunto de pontos
     *
     * @param x array com os valores de X
     * @param y array com os valores de Y
     * @param titulo legenda do gr??fico
     * @param estilo op????es de estilo
     * @param linhasDeTendencia linhas de tend??ncia
     */
    public void plotPontos(Double[] x, Double[] y, String titulo, Estilo estilo, LinhaDeTendencia... linhasDeTendencia) {
        new Thread(() -> {
            // adicionando os pontos no gr??fico
            XYChart.Series<Number, Number> dados = new XYChart.Series<>();
            dados.setName(titulo);
            for (int i = 0; i < x.length; i++) {
                dados.getData().add(new XYChart.Data<>(x[i], y[i]));
            }

            Platform.runLater(() -> {
                // adicionando os pontos no gr??fico
                this.graficoLinhas.getData().add(dados);
                // estilo do gr??fico
                if (estilo == null) {
                    return;
                }
                if (!estilo.exibirMarcador()) {
                    // tira as bolinhas do gr??fico
                    ChartUtils.setTamanhoMarcador(dados, 0);
                }
                if (!estilo.exibirLinha()) {
                    // tira a linha que liga as bolinhas                  
                    ChartUtils.setLarguraLinha(dados, 0);
                }
                if (estilo.getCor() != null) {
                    // muda a cor do gr??fico
                    ChartUtils.setCor(dados, estilo.getCor());
                }
                // define o estilo da linha
                ChartUtils.setEstiloLinha(dados, estilo.getEstiloLinha());
            });

            // gerando as linhas de tend??ncia
            if (linhasDeTendencia != null) {
                for (LinhaDeTendencia linhaDeTendencia : linhasDeTendencia) {
                    if (linhaDeTendencia == null) {
                        continue;
                    }
                    if ((linhaDeTendencia.getTipo() == MEDIA_MOVEL && y.length < 3)
                            || (linhaDeTendencia.getTipo() == MEDIA_MOVEL && linhaDeTendencia.getNumeroDePontos() > y.length - 1)) {
                        // n??o ?? poss??vel gerar a linha de tend??ncia
                        continue;
                    }
                    Funcao f = linhaDeTendencia.getTipo() != MEDIA_MOVEL
                            ? gerarLinhaDeTendencia(x, y, linhaDeTendencia) : null;
                    // gerando o t??tulo com base no tipo da linha de tend??ncia
                    StringBuilder tituloLinha = new StringBuilder();
                    if (linhaDeTendencia.getTitulo() == null && linhaDeTendencia.getTipo() == POLINOMIAL) {
                        switch (linhaDeTendencia.getGrau()) {
                            case 1 ->
                                tituloLinha.append("Linear");
                            case 2 ->
                                tituloLinha.append("Quadr??tica");
                            case 3 ->
                                tituloLinha.append("C??bica");
                            default ->
                                tituloLinha.append(linhaDeTendencia.getTipo().toString())
                                        .append(" grau ").append(linhaDeTendencia.getGrau());
                        }
                        tituloLinha.append(" (").append(titulo).append(")");
                    } else if (linhaDeTendencia.getTitulo() == null && linhaDeTendencia.getTipo() == MEDIA_MOVEL) {
                        tituloLinha.append(linhaDeTendencia.getTipo().toString()).append(" de ")
                                .append(linhaDeTendencia.getNumeroDePontos()).append(" pontos (")
                                .append(titulo).append(")");
                    } else if (linhaDeTendencia.getTitulo() == null) {
                        tituloLinha.append(linhaDeTendencia.getTipo().toString()).append(" (").append(titulo).append(")");
                    } else {
                        tituloLinha.append(linhaDeTendencia.getTitulo());
                    }
                    // definindo o in??cio e o fim do intervalo
                    double inicio = (linhaDeTendencia.getInicio() != MAX_VALUE
                            && linhaDeTendencia.getInicio() < x[0]) ? linhaDeTendencia.getInicio() : x[0];
                    double fim = (linhaDeTendencia.getFim() != MAX_VALUE
                            && linhaDeTendencia.getFim() > x[x.length - 1]) ? linhaDeTendencia.getFim() : x[x.length - 1];
                    // plotando a linha de tend??ncia
                    if ((linhaDeTendencia.getTipo() == POLINOMIAL && linhaDeTendencia.getGrau() <= 1)
                            || linhaDeTendencia.getTipo() == MEDIA_MOVEL) {
                        Double[] xn;
                        Double[] yn;
                        if (linhaDeTendencia.getTipo() == MEDIA_MOVEL) {
                            int nPontos = linhaDeTendencia.getNumeroDePontos();
                            xn = copyOfRange(x, nPontos - 1, x.length);
                            yn = new Double[y.length + 1 - nPontos];
                            for (int i = 0; i < yn.length; i++) {
                                yn[i] = 0.0;
                                for (int n = 0; n < nPontos; n++) {
                                    yn[i] += y[n + i];
                                }
                                yn[i] /= nPontos;
                            }
                        } else {
                            xn = new Double[]{inicio, fim};
                            yn = new Double[]{f.apply(inicio), f.apply(fim)};
                        }
                        Estilo estiloLinhaDeTendencia = new Estilo.Builder()
                                .setExibirLinha(true)
                                .setExibirMarcador(false)
                                .setCor(linhaDeTendencia.getEstilo().getCor())
                                .setEstiloLinha(linhaDeTendencia.getEstilo().getEstiloLinha())
                                .build();
                        plotPontos(xn, yn, tituloLinha.toString(), estiloLinhaDeTendencia);
                    } else {
                        plotFuncao(f, inicio, fim, tituloLinha.toString(), linhaDeTendencia.getEstilo());
                    }
                }
                System.gc();
            }
        }).start();
    }

    /**
     * Gerar a linha de tend??ncia associada ao gr??fico de pontos
     *
     * @param x array com os valores de X do gr??fico de pontos de origem
     * @param y array com os valores de Y do gr??fico de pontos de origem
     * @param linhaDeTendencia linha de tend??ncia
     * @return linha de tend??ncia
     */
    private Funcao gerarLinhaDeTendencia(Double[] x, Double[] y, LinhaDeTendencia linhaDeTendencia) {
        // criando novos arrays de X e Y, pois o algoritmo da
        // Regress??o Linear M??ltipla requer que os ??ndices comecem em 1
        Double[][] xn = new Double[x.length + 1][linhaDeTendencia.getGrau() + 3];
        Double[] yn = new Double[y.length + 1];
        for (int i = 0; i < x.length; i++) {
            if (linhaDeTendencia.getTipo() == LOGARITMICA || linhaDeTendencia.getTipo() == POTENCIA) {
                xn[i + 1][1] = log(x[i]);
            } else {
                xn[i + 1][1] = x[i];
            }
            if (linhaDeTendencia.getTipo() == EXPONENCIAL || linhaDeTendencia.getTipo() == POTENCIA) {
                yn[i + 1] = log(y[i]);
            } else {
                yn[i + 1] = y[i];
            }
        }

        // m??todo da Regress??o Linear M??ltipla retorna um array com os
        // par??metros da equa????o de linha de tend??ncia
        RegressaoLinearMultipla rlm;
        Double[] b;
        try {
            rlm = new RegressaoLinearMultipla(x.length, 1, linhaDeTendencia.getGrau() + 1, xn, yn, linhaDeTendencia.getB0());
            b = rlm.solve();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar a linha de tend??ncia.", e);
        }

        // exibindo as informa????es
        boolean exibirEquacao = linhaDeTendencia.exibirEquacao();
        boolean exibirR2 = linhaDeTendencia.exibirR2();
        boolean exibirSigma2 = linhaDeTendencia.exibirSigma2();
        if (exibirEquacao || exibirR2 || exibirSigma2) {
            StringBuilder info = new StringBuilder();
            Label l = new Label();
            l.setStyle(l.getStyle() + "-fx-font-size:14px;");
            l.setCursor(Cursor.MOVE);
            Platform.runLater(() -> {
                // posi????o inicial da label na tela
                // ?? necess??rio uma implementa????o melhor desta parte!
                this.stackPane.getChildren().add(l);
                double h = this.stackPane.getHeight();
                double w = this.stackPane.getWidth();
                StackPane.setMargin(l, new Insets(h * 0.12 * stackPane.getChildren().size() - 1, 0, 0, w * 0.4));
            });
            l.setOnMouseDragged(event -> Platform.runLater(() -> StackPane.setMargin(l, new Insets(event.getSceneY(), 0, 0, event.getSceneX()))));
            DecimalFormat df = new DecimalFormat("#.####");
            if (exibirEquacao) {
                StringBuilder sb = new StringBuilder("y = ");
                switch (linhaDeTendencia.getTipo()) {
                    case EXPONENCIAL -> {
                        String a1 = df.format(exp(b[1]));
                        String a2 = df.format(b[2]);
                        sb.append(a1.equals("1") ? (a2.equals("0") ? "1" : "") : a1);
                        if (!a2.equals("0")) {
                            sb.append("e").append(a2.equals("1") ? "" : sup(a2)).append(sup("x"));
                        }
                    }
                    case LOGARITMICA -> {
                        String a1 = df.format(b[2]);
                        if (!a1.equals("0")) {
                            sb.append(a1.equals("1") ? "" : a1).append("ln(x)");
                        }
                        if (a1.equals("0")) {
                            sb.append(df.format(b[1]));
                        } else {
                            if (b[1] > 0) {
                                sb.append(" + ").append(df.format(b[1]));
                            } else if (b[1] < 0) {
                                sb.append(" - ").append(df.format(Math.abs(b[1])));
                            }
                        }
                    }
                    case POTENCIA -> {
                        String a1 = df.format(exp(b[1]));
                        String a2 = df.format(b[2]);
                        sb.append(a1.equals("1") ? (a2.equals("0") ? "1" : "") : a1);
                        if (!a2.equals("0")) {
                            sb.append("x").append(a2.equals("1") ? "" : sup(a2));
                        }
                    }
                    default -> {
                        for (int i = b.length - 1; i >= 1; i--) {
                            if (i != b.length - 1 && b[i] > 0) {
                                sb.append(" + ");
                            } else if (i != b.length - 1 && b[i] < 0) {
                                sb.append(" - ");
                            } else if (b[i] == 0) {
                                continue;
                            }
                            if (!df.format(b[i]).equals("1") || i == 1) {
                                sb.append(df.format(i != b.length - 1 ? Math.abs(b[i]) : b[i]));
                            }
                            sb.append(i - 1 > 0 ? "x" : "");
                            sb.append(i - 1 >= 2 ? sup(i - 1) : "");
                        }
                    }
                }
                info.append(info.isEmpty() ? "" : "\n").append(sb.toString());
            }
            if (exibirR2) {
                info.append(info.isEmpty() ? "" : "\n").append("r?? = ").append(df.format(rlm.getR2()));
            }
            if (exibirSigma2) {
                info.append(info.isEmpty() ? "" : "\n").append("???? = ").append(df.format(rlm.getSigma2()));
            }
            Platform.runLater(() -> l.setText(info.toString()));
        }

        // gerando a fun????o da linha de tend??ncia
        Funcao f = xf -> {
            double resultado = 0;
            switch (linhaDeTendencia.getTipo()) {
                case EXPONENCIAL ->
                    resultado += exp(b[1]) * exp(xf * b[2]);
                case LOGARITMICA ->
                    resultado += b[2] * log(xf) + b[1];
                case POTENCIA ->
                    resultado += exp(b[1]) * pow(xf, b[2]);
                default -> {
                    for (int i = 1; i < b.length; i++) {
                        resultado += pow(xf, i - 1) * b[i];
                    }
                }
            }
            return resultado;
        };
        return f;
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

        // adicionando o menu de contexto ao gr??fico
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

        // exibir valores nos eixos do gr??fico em nota????o cient??fica
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
