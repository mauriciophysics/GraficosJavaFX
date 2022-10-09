# Gráficos JavaFX

Biblioteca baseada na API JavaFX para construção de gráficos de funções matemáticas e conjuntos de pontos.

Dependência maven:
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;io.github.mauriciophysics&lt;/groupId&gt;
    &lt;artifactId&gt;Graficos&lt;/artifactId&gt;
    &lt;version&gt;1.2&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

Exemplo de gráfico de função:
<pre><code>Funcao f = x -> Math.sin(x);
Grafico g = new Grafico();
g.plotFuncao(f, 0, 2*Math.PI, "Seno");
g.show(stage);</pre></code>

![Gráfico de função](https://github.com/mauriciophysics/GraficosJavaFX/blob/master/GraficoDeFuncao.png)

Exemplo de gráfico de pontos:
<pre><code>Double[] x = {1.0, 2.2, 3.84, 4.9};
Double[] y = {2.24, 3.71, 4.5, 5.96};
Grafico g = new Grafico();
g.plotPontos(x, y, "Pontos", Estilo.LINHA_E_MARCADOR);
g.show(stage);</code></pre>

![Gráfico de pontos](https://github.com/mauriciophysics/GraficosJavaFX/blob/master/GraficoDePontos.png)
