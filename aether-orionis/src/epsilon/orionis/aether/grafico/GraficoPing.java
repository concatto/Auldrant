package epsilon.orionis.aether.grafico;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraficoPing extends Grafico {
	private XYSeries series;
	
	public GraficoPing(int limite) {
		series = new XYSeries("Ping");
		collection = new XYSeriesCollection();
		collection.addSeries(series);
		
		construirGrafico(limite, "Ping");
	}
	
	public void atualizarGrafico(int valor) {
		if (valor > plot.getRangeAxis().getUpperBound()) {
			plot.getRangeAxis().setAutoRange(true);
		}
		series.add(index++, valor);
	}
	
	@Override
	public void limparGrafico() {
		plot.getRangeAxis().setRange(1, plot.getRangeAxis().getUpperBound());
		series.clear();
		index = 0;
	}
}
