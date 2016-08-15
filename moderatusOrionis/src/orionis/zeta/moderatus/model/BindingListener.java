package orionis.zeta.moderatus.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class BindingListener implements ChangeListener<String> {
	private DoubleProperty target;
	private DoubleProperty base;
	private DoubleProperty multiplier;
	private DoubleProperty divisor;
	
	public BindingListener(DoubleProperty target, DoubleProperty base, DoubleProperty multiplier, DoubleProperty divisor) {
		this.target = target;
		this.base = base;
		this.multiplier = multiplier;
		this.divisor = divisor;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		double value = base.get() * multiplier.get() / divisor.get();
		if (!Double.isNaN(value)) target.set(value);
	}
}
