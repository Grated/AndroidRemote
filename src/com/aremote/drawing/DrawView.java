package com.aremote.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import com.aremote.connection.NetworkService;
import com.aremote.connection.RemoteCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sgreenman
 * Date: 6/7/12
 * Time: 7:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DrawView extends View implements View.OnTouchListener
{
   List<Point> points = new ArrayList<Point>();
   Paint paint = new Paint();
   NetworkService service;

   // Previous action
   int prev_action = 0;

   public DrawView(Context context, NetworkService service)
   {
      super(context);
      setFocusable(true);
      setFocusableInTouchMode(true);
      this.setOnTouchListener(this);
      paint.setColor(Color.WHITE);
      paint.setAntiAlias(true);
      this.service = service;
   }

   public void onDraw(Canvas canvas)
   {
      for (Point point : points)
      {
         canvas.drawCircle(point.x, point.y, 5, paint);
      }
      points.clear();
   }

   @Override
   public boolean onTouch(View view, MotionEvent event)
   {
      Point point = new Point();
      point.x = (int)event.getX();
      point.y = (int)event.getY();

      if (event.getAction() == MotionEvent.ACTION_MOVE)
      {
         service.sendCommand(new RemoteCommand(RemoteCommand.cmdMove,
               point.x, point.y));

         points.add(point);
         invalidate();
      }
      else if (event.getAction() == MotionEvent.ACTION_DOWN)
      {
         service.sendCommand(new RemoteCommand(RemoteCommand.cmdReset,
               point.x, point.y));
      }
      else if (event.getAction() == MotionEvent.ACTION_UP)
      {
         // If we have an action down followed immediately by an action up
         // then report a click.
         if (prev_action == MotionEvent.ACTION_DOWN)
         {
            service.sendCommand(new RemoteCommand(RemoteCommand.cmdClick,
                  point.x, point.y));

            points.add(point);
            invalidate();
         }
      }

      // Store this action for the next time around
      this.prev_action = event.getAction();

      return true;
   }
}
