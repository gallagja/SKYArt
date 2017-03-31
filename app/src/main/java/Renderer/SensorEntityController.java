package Renderer;

import android.util.Log;

import skyart.skyffti.Utils.SensorControl;

/**
 * Created by Coltan on 2/8/17.
 */

public class SensorEntityController extends EntityController{
    public static  boolean initDone = false;
    static boolean initDone2 = false;
    static Entity ent;
    public void apply(Entity entity) {
        try {
            entity.translate(SensorControl.getInstance().getSumPos());
            if (SensorControl.getInstance().hprDirty() && initDone) {
                float [] hpr = SensorControl.getInstance().getHPR();
                entity.rotate(hpr);
            }
            if (SensorControl.getInstance().hprGravDirty() && !initDone) {

                float [] hpr = SensorControl.getInstance().getINITHPR();
                Log.d("SensorEntityController", "apply: INIT: " + hpr[0] +"," + hpr[1] + "," + hpr[2]);
                entity.setRotation(hpr);

                initDone = true;
                ent = entity;
            }

        }catch(Exception e){
            Log.e("Sensor", "Error in SensorEntityController... " + e);
        }


    }

    public static void setRot() {
        float [] hpr = SensorControl.getInstance().getINITHPR();
        Log.d("SensorEntityController", "apply: rot: " + hpr[0] +"," + hpr[1] + "," + hpr[2]);
        ent.setRotation(hpr);
        initDone2 = true;
    }
}