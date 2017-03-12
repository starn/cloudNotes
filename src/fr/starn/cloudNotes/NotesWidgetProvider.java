package fr.starn.cloudNotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;


public class NotesWidgetProvider extends AppWidgetProvider  {
	//private static int myWidgetID;
	private static final String WIDGET_CLICKED    = "WIDGET_CLICKED";
	private static List<Integer> widgetIDList = new ArrayList<>();

	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		System.out.println("onAppWidgetOptionsChanged");
	}

	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		ContactMgt.getAccounts(context);



		int myWidgetID = -1;
		if (appWidgetIds.length>=1) myWidgetID = appWidgetIds[0];
		System.out.println("onUpdate "+myWidgetID);

		if (!widgetIDList.contains(myWidgetID)){
			widgetIDList.add(myWidgetID);
		}

		//disable multi instance for now. so refresh all isntance with the last typed text.
		//for (int i: appWidgetIds) {
		for (int i: widgetIDList) {
			updateValue(context,i,ContactMgt.getContactAddress(context));
		}




	}
	
	public static RemoteViews buildViews(Context context, int myWidgetID, String text){
		System.out.println("buildViews "+myWidgetID);
		RemoteViews rv= new RemoteViews(context.getPackageName(), R.layout.temp_layout);


		rv.setOnClickPendingIntent(R.id.widgetlayout, getPendingSelfIntent(context,myWidgetID, WIDGET_CLICKED));
		
		if (text!=null) rv.setTextViewText(R.id.widgetText, text);


//		String deviceConf = props.getDevice();
//		String deviceConf2 = "";
//		if (deviceConf.indexOf(" ") != -1){
//			deviceConf2=deviceConf.substring(deviceConf.indexOf(" ")+1);
//			deviceConf=deviceConf.substring(0,deviceConf.indexOf(" "));
//
//		}
//		if (deviceConf.length()>9){
//			deviceConf=deviceConf.substring(0,6)+"...";
//		}
//		if (deviceConf2.length()>9){
//			deviceConf2=deviceConf2.substring(0,6)+"...";
//		}
//
//		rv.setTextViewText(R.id.deviceName, deviceConf);
//		rv.setTextViewText(R.id.deviceName2, deviceConf2);
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//		rv.setTextViewText(R.id.refreshTime, sdf.format(new Date()));
//		rv.setTextColor(R.id.refreshTime, color);
//
//		if (device==null) {
//			rv.setOnClickPendingIntent(R.id.widgetlayout, getPendingSelfIntent(context, SYNC_CLICKED,props));
//			return rv;//to avoid some strange crash....
//		}
//
//		if (props.isDemoMode()){
//			rv.setTextViewText(R.id.deviceName, "Demo");
//			rv.setTextViewText(R.id.deviceName2, "");
//			rv.setTextViewText(R.id.value, "--");
//		}
//
//		else if (device.isTemp()){
//
//			if (value!=null){
//				rv.setTextViewText(R.id.value, value);
//			}
//			rv.setOnClickPendingIntent(R.id.widgetlayout, getPendingSelfIntent(context, SYNC_CLICKED,props));
//		}
//
//		else if (device.isLight()){
//			rv.setOnClickPendingIntent(R.id.widgetlayout, getPendingSelfIntent(context, SWITCH_LIGHT,props));
//
//			if ("Off".equals(device.getStatus())){
//				rv.setInt(R.id.icon, "setBackgroundResource", R.drawable.lightoff);
//			} else {
//				rv.setInt(R.id.icon, "setBackgroundResource", R.drawable.lighton);
//			}
//
//			rv.setInt(R.id.icon, "setVisibility", View.VISIBLE) ;
//			rv.setInt(R.id.value, "setVisibility", View.GONE) ;
//		}
		
		return rv;
	}
	


    protected static PendingIntent getPendingSelfIntent(Context context, int myWidgetID, String action) {
		System.out.println("getPendingSelfIntent "+myWidgetID);

        Intent intent = new Intent(context, NotesWidgetProvider.class);
        System.out.println("********* create intent for "+myWidgetID);
        intent.setAction(action);
		intent.putExtra("widgetID",new Integer(myWidgetID));
		intent.setAction("widgetID" + myWidgetID);
        //be carrefull: requestCode must be unique for intents, else the last intent set override the old 
        //(do a formula to be unique for couple widgetid/action
        return PendingIntent.getBroadcast(context, myWidgetID+(100*action.length()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    @Override
	/**
	 * receive event broadcasted from rv.setOnClickPendingIntent (built in method getPendingSelfIntent
	 * and called from buildviews)
	 *
	 * or from event broadcasted by editor activity after saving new text
	 */
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        int myWidgetID=-1;
        if (intent.getSerializableExtra("widgetID")!=null)
			myWidgetID = ((Integer)intent.getSerializableExtra("widgetID")).intValue();
		System.out.println("xxx"+intent.getAction());
		System.out.println("onReceive: "+myWidgetID);


        //display a temp value during refresh
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //appWidgetManager.updateAppWidget(myWidgetID,buildViews(context, myWidgetID, MainActivity.text));

        if (!AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
			Intent modif = new Intent(context, MainActivity.class);
			modif.putExtra("widgetID",myWidgetID);
			context.startActivity(modif);
		}
//        if (props.getHost()!=null && SYNC_CLICKED.equals(intent.getAction())) {
//        	   Bundle extras = intent.getExtras();
//        	   if(extras!=null) {
//        	    	System.out.println("refreshWidget pour "+props.getDevice());
//        	    	Thread t = new Thread(new RestRequestHandler(context, this,props,RestRequestHandler.LIST,false));
//        			t.start();
//        	   }
//        }
//        if (props.getHost()!=null && SWITCH_LIGHT.equals(intent.getAction())) {
//     	   Bundle extras = intent.getExtras();
//     	   if(extras!=null) {
//     	    	Thread t = new Thread(new RestRequestHandler(context, this,props,RestRequestHandler.COMMAND));
//     			t.start();
//     	   }
//     }
    }




	public void updateValue(Context context, int myWidgetID,  String text){
		System.out.println("updateValue "+myWidgetID);


		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews rv = buildViews(context, myWidgetID,  text);
		appWidgetManager.updateAppWidget(myWidgetID, rv);
	}


	
	


	 @Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		System.out.println("onDeleted ");
		super.onDeleted(context, appWidgetIds);
		for (int id:appWidgetIds){
			System.out.println("delete widget "+id);
		}
	}


}