package com.clonefish.smstoemail;

import java.util.Date;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.widget.Toast;


public class SmsService extends Service {
	
	@Override
	public void onCreate() {
//		ACRA.init(this.getApplication()); 
//	    ErrorReporter.getInstance().checkReportsOnApplicationStart();
		super.onCreate();
	}
	//	private class SmsData {
	//		public int hh;
	//		public int mm;
	//		public String description;
	//	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	//	private void showNotification(String text) {
	//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
	//		Context context = getApplicationContext();
	//		Notification.Builder builder = new Notification.Builder(context)
	//			.setContentTitle("Новое сообщение")
	//			.setContentText(text)
	//			.setContentIntent(contentIntent)
	//			.setSmallIcon(R.drawable.ic_launcher)
	//			.setAutoCancel(true);
	//		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	//		Notification notification = builder.getNotification();
	//		notificationManager.notify(R.drawable.ic_launcher, notification);
	//	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String sms_body = intent.getExtras().getString("sms_body");
		//		showNotification(sms_body);
		saveSms(sms_body);
		//		sendSms(sms_body);
		new Connection().execute(sms_body);
		return START_STICKY;
	}

	/*
	 * Здесь отправляем сообщение на мыло
	 */
	private void sendSms(String sms_body){
		try {   
			GmailSender sender = new GmailSender(MainActivity.sp.getString("username", ""), MainActivity.sp.getString("password", ""));
			if(!sender.sendMail(MainActivity.sp.getString("phone", ""),   
					sms_body,   
					MainActivity.sp.getString("username", ""),   
					MainActivity.sp.getString("username", "")))
				Toast.makeText(getApplicationContext(), "Can't send Email to server.", Toast.LENGTH_SHORT);
		} catch (Exception e) { 
			
		} 
	}

	class Connection extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			for (String body : params){
				sendSms(body);
			}
			return null;
		}

	}

	private void saveSms(String sms_body) {
		Date now = new Date();
		long now_long = now.getTime();

		ContentValues values = new ContentValues();
		values.put(SmsTable.COLUMN_DATE, now_long);
		values.put(SmsTable.COLUMN_TEXT, sms_body);
//		values.put(SmsTable.COLUMN_RECIEVED, false);

		getContentResolver().insert(SmsContentProvider.CONTENT_URI, values);
	}
}
