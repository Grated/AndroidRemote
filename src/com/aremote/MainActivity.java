package com.aremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.aremote.com.aremote.drawing.DrawView;

public class MainActivity extends Activity
{
   DrawView drawing;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
       //setContentView(R.layout.main);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       drawing = new DrawView(this);
       setContentView(drawing);
       drawing.requestFocus();
    }
}
