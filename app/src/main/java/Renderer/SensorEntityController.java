package Renderer;

import android.util.Log;
import android.widget.Toast;

import skyart.skyffti.MainActivity;
import skyart.skyffti.SensorControl;

/**
 * Created by Coltan on 2/8/17.
 */

public class SensorEntityController extends EntityController{
    public void apply(Entity entity) {
        try {
            entity.translate(SensorControl.getInstance().getSumPos());
            entity.rotate(SensorControl.getInstance().getSumHPR());

            MainActivity.makeToast(entity.getPosition()[0]+"");
        }catch(Exception e){
            Log.d("Sensor", "Error in SensorEntityController... Continuing");
        }


    }
}