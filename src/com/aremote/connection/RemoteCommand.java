package com.aremote.connection;

/**
 * Created with IntelliJ IDEA.
 * User: sgreenman
 * Date: 6/9/12
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteCommand
{
   public static final int cmdShutdown = -2;
   public static final int cmdClose = -1;
   public static final int cmdMove = 0;
   public static final int cmdClick = 1;
   public static final int cmdReset = 2;

   public final int cmd;
   public final int x;
   public final int y;

   public RemoteCommand(int c, int x, int y)
   {
      this.cmd = c;
      this.x = x;
      this.y = y;
   }
}
