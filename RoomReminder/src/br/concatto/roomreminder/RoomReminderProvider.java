package br.concatto.roomreminder;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class RoomReminderProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i("room", "Hello world");
	}
}
