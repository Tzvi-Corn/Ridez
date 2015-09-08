package il.ac.huji.ridez;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Tzvi on 20/08/2015.
 */
public class UIHelper {
    public static void buttonEffect(View button){

        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Object a = v.getBackground();
                        v.getBackground().setColorFilter(Color.parseColor("#990099"), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public static void buttonEffect2(View button){

        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Object a = v.getBackground();
                        v.getBackground().setColorFilter(Color.parseColor("#990099"), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }

                    case MotionEvent.ACTION_OUTSIDE: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_HOVER_MOVE: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }

                    case MotionEvent.ACTION_HOVER_EXIT: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
