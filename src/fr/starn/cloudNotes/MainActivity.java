package fr.starn.cloudNotes;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

//	private static Map<Integer,String> texts;

	private Button b1;
	private EditText editText;
	private int widgetID;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlayout);

//		if (texts==null) texts = new HashMap<>();
		widgetID=getIntent().getIntExtra("widgetID",-1);
		System.out.println("widget id reçu dans le onCreate de l'activity: "+widgetID);
		if (widgetID==-1) this.finish();
		editText = (EditText) findViewById(R.id.text);

		editText.setText(ContactMgt.getContactAddress(MainActivity.this));
		Button b1 = (Button) findViewById(R.id.save);
		b1.setOnClickListener(myhandler);

	}


	View.OnClickListener myhandler = new View.OnClickListener() {
		public void onClick(View v) {

			//save to address book
			ContactMgt.updateAddress(MainActivity.this, editText.getText().toString());


			//ContactMgt.createContact(MainActivity.this);
//
//			String add= ContactMgt.getContactAddress(MainActivity.this);
//			System.out.println(add);
//
//			texts.put(widgetID,editText.getText().toString());
//			texts.put(-1,editText.getText().toString());

			Intent intent = new Intent(MainActivity.this,NotesWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			int[] ids = {widgetID};
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
			intent.putExtra("widgetID",new Integer(widgetID));
			sendBroadcast(intent);
			MainActivity.this.finish();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("resume !!");
		//editText.setText(getWidgetText(widgetID));
		editText.setText(ContactMgt.getContactAddress(MainActivity.this));
	}


}
