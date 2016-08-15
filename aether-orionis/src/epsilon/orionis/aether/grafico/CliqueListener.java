package epsilon.orionis.aether.grafico;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

public class CliqueListener implements ChartMouseListener {
	private Grafico grafico;
	private AxisOrionis axis;
	
	public CliqueListener(Grafico grafico) {
		this.grafico = grafico;
		axis = this.grafico.getAxis();
	}
	
	@Override
	public void chartMouseClicked(ChartMouseEvent e) {
		Rectangle2D chaveFinal = null;
		
		for (Rectangle2D chave : axis.getPontos().keySet()) {
			if (chave.contains(e.getTrigger().getPoint())) {
				chaveFinal = chave;
				break;
			}
		}
		
		System.out.println(((GraficoTraceroute) grafico).getInformacao((int) axis.getPontos().get(chaveFinal).getValue()));
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent e) {
		/* Em branco */
	}

}
