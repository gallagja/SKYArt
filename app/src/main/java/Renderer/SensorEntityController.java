package Renderer;

import android.util.Log;

import skyart.skyffti.SensorControl;

/**
 * Created by Coltan on 2/8/17.
 */

public class SensorEntityController {
    public void apply(Entity entity) {
        try {
            entity.translate(SensorControl.getInstance().getSumPos());
            entity.rotate(SensorControl.getInstance().getSumHPR());
        }catch(Exception e){
            Log.d("Sensor", "Error in SensorEntityController... Continuing");
        }


    }
}