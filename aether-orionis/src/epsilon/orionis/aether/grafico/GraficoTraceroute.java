package epsilon.orionis.aether.grafico;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraficoTraceroute extends Grafico {
	private List<String> informacoes = new ArrayList<>();
	private XYSeries primeiro;
	private XYSeries segundo;
	private XYSeries terceiro;
	
	public GraficoTraceroute(int limite) {
		primeiro = new XYSeries("Primeiro teste ");
		segundo = new XYSeries("Segundo teste");
		terceiro = new XYSeries("Terceiro teste");
		collection = new XYSeriesCollection();
		collection.addSeries(primeiro);
		collection.addSeries(segundo);
		collection.addSeries(terceiro);
		
		construirGrafico(limite, "Traceroute");
	}
	
	public void atualizarGrafico(int primeiroValor, int segundoValor, int terceiroValor, String info) {
		int limite = (int) plot.getRangeAxis().getUpperBound();
		if (primeiroValor > limite || segundoValor > limite || terceiroValor > limite) {
			plot.getRangeAxis().setAutoRange(true);
		}
		primeiro.add(index, primeiroValor);
		segundo.add(index, segundoValor);
		terceiro.add(index, terceiroValor);
		index++;
		informacoes.add(info);
	}
	
	@Override
	public void limparGrafico() {
		plot.getRangeAxis().setRange(1, plot.getRangeAxis().getUpperBound());
		primeiro.clear();
		index = 0;
	}

	public void atualizarGrafico(String primeiroValor, String segundoValor, String terceiroValor, String info) {
		atualizarGrafico(Integer.parseInt(primeiroValor), Integer.parseInt(segundoValor), Integer.parseInt(terceiroValor), info);
	}
	
	public String getInformacao(int index) {
		return informacoes.get(index);
	}
}
