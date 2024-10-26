package com.example.findfriends;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySMSReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
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
                    if (messageBody.contains(("FINDFRIENDS: Envoyez moi votre position"))){
                        //lancer un service pour recuperer position et repondre
                        Intent i=new Intent(context,MyLocationService.class);
                        i.putExtra("PHONE",phoneNumber);
                        context.startService(i);

                    }
                    if (messageBody.contains(("FINDFRIENDS: Ma position est"))){
                        String[] t= messageBody.split("#");
                        String longitude=t[1];
                        String latitude=t[2];
                        NotificationCompat.Builder mynotif=new NotificationCompat.Builder(context,"channel");
                        mynotif.setContentTitle("position recu");
                        mynotif.setContentText("appuiyer pour voir");
                        mynotif.setSmallIcon(android.R.drawable.ic_dialog_map);
                        mynotif.setAutoCancel(true);
                        Intent i2 =new Intent(context, MapsActivity.class);
                        PendingIntent pi=PendingIntent.getActivity(
                                context,0,
                                i2,
                                PendingIntent.FLAG_IMMUTABLE
                        );
                        i2.putExtra("longitude",longitude);
                        i2.putExtra("latitude",latitude);
                        mynotif.setContentIntent(pi);


                        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(context);
                        NotificationChannel canal= null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            canal = new NotificationChannel("channel","canal pour notre app",
                                    NotificationManager.IMPORTANCE_DEFAULT);
                            managerCompat.createNotificationChannel(canal);

                        }
                        managerCompat.notify(1,mynotif.build());

                    }
                    Toast.makeText(context, "Message : "+messageBody +"Recu de la part de ;"+phoneNumber,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}