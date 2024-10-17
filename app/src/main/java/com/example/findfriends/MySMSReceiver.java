package com.example.findfriends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MySMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String messageBody,phoneNumber;
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        { Bundle bundle =intent.getExtras();
            if (bundle != null)
            { Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++)
                { messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]); }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();
                    if (messageBody.contains(("FINDFRIENDS: Envoyer moi votre position"))){
                        //lancer un service pour recuperer position et repondre
                        Intent i=new Intent(context,MyLocationService.class);
                        i.putExtra("PHONE",phoneNumber);
                        context.startService(i);

                    }
                }
            }
        }
    }
}