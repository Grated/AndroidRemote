package com.aremote.connection;

/**
 * Created with IntelliJ IDEA.
 * User: sgreenman
 * Date: 6/9/12
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionListener
{
   public void onConnect();
   public void onDisconnect();
}
