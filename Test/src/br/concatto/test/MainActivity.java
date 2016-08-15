package br.concatto.test;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends Activity {
	private ViewGroup container;
	private ArrayList<EditText> fields = new ArrayList<EditText>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		container = (ViewGroup) findViewById(R.id.fieldContainer);
		createField();
		createField();
	}
	
	private void createField() {
		final EditText field = (EditText) LayoutInflater.from(MainActivity.this).inflate(R.layout.field, null);
		field.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && field.getText().length() == 0) {
					for (EditText otherField : fields) {
						if (otherField.getText().length() == 0 && !otherField.equals(v)) return;
					}
					createField();
				}
			}
		});
		
		fields.add(field);
		container.addView(field);
	}
}
