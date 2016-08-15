package epsilon.orionis.aether.grafico;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;

import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeriesCollection;

public abstract class Grafico {
	protected AxisOrionis axis;
	protected XYSeriesCollection collection;
	protected XYLineAndShapeRenderer renderer;
	protected JFreeChart chart;
	protected XYPlot plot;
	protected int index = 0;
	
	public void construirGrafico(int limite, String nome) {
		axis = new AxisOrionis();
		chart = ChartFactory.createXYLineChart(nome, "Índice do pedido", "Tempo (ms)", collection, PlotOrientation.VERTICAL, false, true, false);
		
		StandardChartTheme tema = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
		Font fonte = new Font("Lucida", Font.PLAIN, 12);
		tema.setChartBackgroundPaint(UIManager.getColor("Panel.background"));
		tema.setExtraLargeFont(fonte.deriveFont(Font.BOLD, 26));
		tema.setLargeFont(fonte.deriveFont(Font.BOLD, 16));
		tema.setRegularFont(fonte);
		tema.setSmallFont(fonte);
		
		plot = (XYPlot) chart.getPlot();
		plot.setDomainAxis(axis);
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		plot.getRangeAxis().setRange(1, limite);
		
		LegendTitle legenda = new LegendTitle(plot);
		chart.addLegend(legenda);
		
		tema.apply(chart);
		
		renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesPaint(2, new Color(0, 180, 0));
		
		for (int i = 0; i < plot.getSeriesCount(); i++) {
			renderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
			renderer.setSeriesShapesVisible(i, true);
		}
	}
	
	public abstract void limparGrafico();
	
	public JFreeChart getChart() {
		return chart;
	}
	
	public AxisOrionis getAxis() {
		return axis;
	}
}
