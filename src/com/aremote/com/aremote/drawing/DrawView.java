package com.aremote.com.aremote.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

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

   public DrawView(Context context)
   {
      super(context);
      setFocusable(true);
      setFocusableInTouchMode(true);
      this.setOnTouchListener(this);
      paint.setColor(Color.WHITE);
      paint.setAntiAlias(true);
   }

   public void onDraw(Canvas canvas)
   {
      for (Point point : points)
      {
         canvas.drawCircle(point.x, point.y, 5, paint);
      }
   }

   @Override
   public boolean onTouch(View view, MotionEvent event)
   {
      Point point = new Point();
      point.x = (int)event.getX();
      point.y = (int)event.getY();
      points.add(point);
      invalidate();
      return true;
   }
}
