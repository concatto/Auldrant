package epsilon.orionis.aether.grafico;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.ui.RectangleEdge;

@SuppressWarnings("serial")
public class AxisOrionis extends NumberAxis {
	private Map<Rectangle2D, ValueTick> pontos = new HashMap<>();
	
	public AxisOrionis() {
		setLabel("Índice do pedido");
	}
	
	@Override
	protected int calculateVisibleTickCount() {
		pontos.clear();
		return super.calculateVisibleTickCount();
	}
	
	@Override
	protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
		float[] array = super.calculateAnchorPoint(tick, cursor, dataArea, edge);
		float w = 25;
		float h = 25;
		Rectangle2D r = new Rectangle2D.Float(array[0] - 12, array[1] - 5, w, h);
		if (tick.getValue() == (int) tick.getValue()) {
			pontos.put(r, tick);
		}
		return array;
	}
	
	public Map<Rectangle2D, ValueTick> getPontos() {
		return pontos;
	}
}
