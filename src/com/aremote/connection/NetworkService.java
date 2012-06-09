package com.aremote.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sgreenman
 * Date: 6/9/12
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends Service
{
   // Socket and socket writer.  Access only from the network thread.
   private Socket socket = null;
   private DataOutputStream writer = null;

   // Queue for passing commands from the UI thread to the network thread.
   // Sized to hold up to ten messages.
   private ArrayBlockingQueue<RemoteCommand> cmd_queue =
         new ArrayBlockingQueue<RemoteCommand>(10);

   // The network thread itself
   Thread net_thread;

   // Binder given to clients
   private final Binder binder = new LocalBinder();

   // Handler for making UI changes from the network thread
   private Handler handler = new Handler();

   // List of listeners
   private ArrayList<ConnectionListener> listeners;

   /**
    * Class used for the client binder.
    */
   public class LocalBinder extends Binder
   {
      public NetworkService getService()
      {
         return NetworkService.this;
      }

      public void registerListener(ConnectionListener listener)
      {
         listeners.add(listener);
      }

      public void unregisterListener(ConnectionListener listener)
      {
         listeners.remove(listener);
      }

   }

   private class NetworkThread implements Runnable
   {
      String address;
      Integer port;

      public NetworkThread(String address, Integer port)
      {
         this.address = address;
         this.port = port;
      }

      @Override
      public void run()
      {
         try
         {
            socket = new Socket(address, port);
            writer = new DataOutputStream(socket.getOutputStream());

            // Inform UI of connection success
            handler.post(new Runnable()
            {
               @Override
               public void run()
               {
                  NetworkService.this.connectionEstablished();
               }
            });

            // Loopin'
            while (socket != null && socket.isConnected())
            {
               RemoteCommand cmd = cmd_queue.poll(100, TimeUnit.MILLISECONDS);
               if (cmd != null)
               {
                  writer.writeInt(cmd.cmd);
                  writer.writeInt(cmd.x);
                  writer.writeInt(cmd.y);
               }
            }

            // Inform UI of connection termination
            handler.post(new Runnable()
            {
               @Override
               public void run()
               {
                  NetworkService.this.connectionTerminated();
               }
            });


         }
         catch (IOException e)
         {
            Log.e("Error connecting socket", e.getMessage());
            System.exit(-1);
         } catch (InterruptedException e)
         {
            Log.e("Interrupted pulling from queue", e.getMessage());
            e.printStackTrace();
         }
      }
   }

   @Override
   public void onCreate()
   {
      // Start the thread responsible for handling the network connection.
      listeners = new ArrayList<ConnectionListener>();
      net_thread = new Thread(new NetworkThread("192.168.1.30", 4444));
      net_thread.start();
   }

   @Override
   public IBinder onBind(Intent intent)
   {
      return this.binder;
   }

   @Override
   public void onDestroy()
   {
      // Stop the thread responsible for handling the network connection.
      // Also, close the connection!
      try
      {
         cmd_queue.add(new RemoteCommand(RemoteCommand.cmdClose, 0, 0));
         net_thread.join(500);
         writer.close();
         socket.close();
      } catch (InterruptedException e)
      {
         e.printStackTrace();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   // Lets listeners know that the connection has been established.
   public void connectionEstablished()
   {
      for (ConnectionListener l : listeners)
      {
         l.onConnect();
      }
   }

   // Lets listeners know that the connection has been terminated.
   public void connectionTerminated()
   {
      for (ConnectionListener l : listeners)
      {
         l.onDisconnect();
      }
   }

   /**
    * Function for sending a command over the network.
    * @param cmd
    * @return
    */
   public boolean sendCommand(RemoteCommand cmd)
   {
      boolean retval = false;

      // If socket is connected
      if (socket != null && socket.isConnected())
      {
         // Send command
         if (cmd_queue.remainingCapacity() > 0)
         {
            cmd_queue.add(cmd);
            retval = true;
         }
      }

      return retval;
   }
}
