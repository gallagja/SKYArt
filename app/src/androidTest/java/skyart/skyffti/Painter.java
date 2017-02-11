package skyart.skyffti;



import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;

/**
 * Created by jt10g_000 on 2/11/2017.
 */

public class Painter {

    int width=100;
    int height=100;
    Bitmap b = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
    Canvas canvas= new Canvas(b);
    Paint paint= new Paint();
    CurrentLocation painter;
    //LocationManager locationManager= (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Painter paint= new Painter();
        Location toDraw= new Location("test");
        toDraw.setLongitude(40);
        toDraw.setLatitude(40);
        paint.draw(toDraw,10);

    }

    public Painter(){
        int x=0;
        int y=0;
        int z=0;
        // this should eventually be tied to some kind of location class
        //onLocationChanged(painter);
        String location="locale";
        // painter object should eventually be of type currentLocation
        painter= new CurrentLocation();



    }

    public void draw(Location x, int theta){

        float distance= painter.getCurrent().distanceTo(x);

        // create circle of radius distane tantheta
        canvas.drawCircle((float)x.getLatitude(),(float)x.getLongitude(),(float)Math.tan(theta)*distance,paint);





    }


}
