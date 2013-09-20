package com.clonefish.smstoemail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSMonitor extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null &&
				ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage[] messages = new SmsMessage[pduArray.length];
			for (int i = 0; i < pduArray.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
			}
			String sms_from = messages[0].getDisplayOriginatingAddress();
			//			if (sms_from.equalsIgnoreCase("+358468121970")) {
			if (sms_from.equalsIgnoreCase(MainActivity.sp.getString("phone", "")) || MainActivity.sp.getBoolean("notif", false)) {
				StringBuilder bodyText = new StringBuilder();
				for (int i = 0; i < messages.length; i++) {
					bodyText.append(messages[i].getMessageBody());
				}
				String body = bodyText.toString();
				Intent mIntent = new Intent(context, SmsService.class);

				mIntent.putExtra("sms_body", body);
				context.startService(mIntent);
				if(!MainActivity.sp.getBoolean("inbox", false)){
					abortBroadcast();
				}
			}
		}
	}
}
