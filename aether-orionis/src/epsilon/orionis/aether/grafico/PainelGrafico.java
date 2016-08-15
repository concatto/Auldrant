package epsilon.orionis.aether.grafico;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartPanel;

@SuppressWarnings("serial")
public class PainelGrafico extends ChartPanel {
	private JLabel label = new JLabel("Clique em um índice para ver informações adicionais.", SwingConstants.CENTER);
	
	public PainelGrafico(Grafico grafico) {
		super(grafico.getChart());
		setLayout(new BorderLayout(5, 5));
		setMouseZoomable(false);
		setInitialDelay(0);
		
		addChartMouseListener(new CliqueListener(grafico));
		add(label, BorderLayout.SOUTH);
	}
}
