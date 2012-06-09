package com.aremote.drawing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import com.aremote.connection.ConnectionListener;
import com.aremote.connection.NetworkService;

/**
 * Created with IntelliJ IDEA.
 * User: sgreenman
 * Date: 6/9/12
 * Time: 7:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteActivity extends Activity
{
   DrawView drawing;

   // Hook to the network service
   NetworkService net_service;

   // bound or not...
   boolean bound = false;

   // Connection listener callback
   ConnectionListener connection_listener;

   // Startup
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
      requestWindowFeature(Window.FEATURE_NO_TITLE);

   }

   public void onStart()
   {
      super.onStart();
      Intent intent = new Intent(this, NetworkService.class);
      bindService(intent, connection, Context.BIND_AUTO_CREATE);
   }

   // Shutdown
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
            }

            @Override
            public void onDisconnect()
            {
               RemoteActivity.this.finish();
            }
         };
         binder.registerListener(connection_listener);
         bound = true;

         drawing = new DrawView(RemoteActivity.this, net_service);
         RemoteActivity.this.setContentView(drawing);
         drawing.requestFocus();
      }

      @Override
      public void onServiceDisconnected(ComponentName componentName)
      {
         bound = false;
      }
   };
}
