package com.aremote;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.aremote.connection.ConnectionListener;
import com.aremote.connection.NetworkService;
import com.aremote.drawing.RemoteActivity;

public class MainActivity extends Activity
{
   NetworkService net_service;
   boolean bound = false;
   ConnectionListener connection_listener;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);

       // Start with no layout, in the future this will have "connect" options.
       setContentView(R.layout.main);
    }

   @Override
   public void onStart()
   {
      super.onStart();
      Intent intent = new Intent(this, NetworkService.class);
      bindService(intent, connection, Context.BIND_AUTO_CREATE);
   }

   @Override
   public void onStop()
   {
      super.onStop();
      if (bound)
      {
         unbindService(connection);
         bound = false;
      }
   }

   public void onNetworkConnect()
   {
      // Switch to RemoteActivity
   }

   private ServiceConnection connection = new ServiceConnection()
   {
      @Override
      public void onServiceConnected(ComponentName componentName,
                                     IBinder iBinder)
      {
         NetworkService.LocalBinder binder =
               (NetworkService.LocalBinder)iBinder;
         net_service = binder.getService();
         connection_listener = new ConnectionListener()
         {
            @Override
            public void onConnect()
            {
               Intent intent = new Intent(MainActivity.this,
                     RemoteActivity.class);
               MainActivity.this.startActivity(intent);
            }

            @Override
            public void onDisconnect()
            {
            }
         };
         binder.registerListener(connection_listener);
         bound = true;
      }

      @Override
      public void onServiceDisconnected(ComponentName componentName)
      {
         bound = false;
      }
   };
}
