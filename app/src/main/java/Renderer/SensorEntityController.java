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
//            entity.translate(SensorControl.getInstance().getSumPos());

            if (SensorControl.getInstance().hprDirty()) {
                float [] hpr = SensorControl.getInstance().getHPR();
                entity.rotate(hpr);
            }
        }catch(Exception e){
            Log.e("Sensor", "Error in SensorEntityController... " + e);
        }


    }
}