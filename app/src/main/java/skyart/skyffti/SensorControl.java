package skyart.skyffti;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.concurrent.Semaphore;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Coltan on 2/20/2017.
 */

public class SensorControl  implements SensorEventListener {

    private MainActivity activity;
    SensorManager sensorManager;
    private static SensorControl instance;

    public float scale = 0.0f; //scales the output
    public int offset = 500;

    float xPos = 10.0f;
    float yPos = 10.0f;
    float zPos = 10.0f;

    private long prev_time = 0;
    private float prev_accelX = 0;
    private float prev_accelY = 0;
    private float prev_accelZ = 0;
    private float prev_velcX = 0;
    private float prev_velcY = 0;
    private float prev_velcZ = 0;
    private float prev_posX = 1;
    private float prev_posY = 1;
    private float prev_posZ = 1;

    private float posXSum = 0.0f;
    private float posYSum = 0.0f;
    private float posZSum = 0.0f;

    private float roll;
    private float yaw;
    private float pitch;

    Semaphore PosSem = new Semaphore(1);
    Semaphore HDRSem = new Semaphore(1);

    public SensorControl(MainActivity ma) {
        activity = ma;
        sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public SensorControl() {
        sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public static void initInstance(MainActivity ma) {
        if (instance == null) {
            instance = new SensorControl(ma);
            instance.mHPRDirtied = false;
        }

    }

    public static SensorControl getInstance() {
        return instance;
    }

    private boolean mHPRDirtied;
    private long mLastTime;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            yaw   += (float) (1.0/1000000000.0 * (double)(event.timestamp - mLastTime)) * Math.toDegrees(event.values[1]);
            pitch += (float) (1.0/1000000000.0 * (double)(event.timestamp - mLastTime)) * Math.toDegrees(event.values[0]);
            roll  += (float) (1.0/1000000000.0 * (double)(event.timestamp - mLastTime)) * Math.toDegrees(event.values[2]);

            mLastTime = event.timestamp;

            mHPRDirtied = true;
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {


//            double ysqr = q.y() * q.y();
//
//            // roll (x-axis rotation)
//            double t0 = +2.0 * (q.w() * q.x() + q.y() * q.z());
//            double t1 = +1.0 - 2.0 * (q.x() * q.x() + ysqr);
//            roll = std::atan2(t0, t1);
//
//            // pitch (y-axis rotation)
//            double t2 = +2.0 * (q.w() * q.y() - q.z() * q.x());
//            t2 = t2 > 1.0 ? 1.0 : t2;
//            t2 = t2 < -1.0 ? -1.0 : t2;
//            pitch = std::asin(t2);
//
//            // yaw (z-axis rotation)
//            double t3 = +2.0 * (q.w() * q.z() + q.x() * q.y());
//            double t4 = +1.0 - 2.0 * (ysqr + q.z() * q.z());
//            yaw = std::atan2(t3, t4);


            float ysqr = event.values[1] * event.values[1];

            // roll (x-axis rotation)
            float t0 = (float) 2.0f * (1.0f * event.values[0] + event.values[1] * event.values[2]);
            float t1 = (float) (1.0f - 2.0f * (event.values[0] * event.values[0] + ysqr));
            roll = (float) Math.toDegrees(Math.atan2(t0, t1));

            // pitch (y-axis rotation)
            float t2 = (float) (2.0f * (1.0f * event.values[1] - event.values[2] * event.values[0]));
            t2 = (float) (t2 > 1.0f ? 1.0f : t2);
            t2 = (float) (t2 < -1.0f ? -1.0f : t2);
            pitch = (float) Math.toDegrees(Math.asin(t2));

            // yaw (z-axis rotation)
            float t3 = (float) (2.0f * (1.0f * event.values[2] + event.values[0] * event.values[1]));
            float t4 = (float) (1.0f - 2.0f * (ysqr + event.values[2] * event.values[2]));
            yaw = (float) Math.toDegrees(Math.atan2(t3, t4));

            mHPRDirtied = true;
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            //Get Time Difference
            long time_diff = event.timestamp - prev_time;
            //Convert to seconds
            float dt = (float) (time_diff / 1000000000.0);

            //Find Velocity
            float velvX = prev_velcX + event.values[0] * dt * scale;
            float velvY = prev_velcY + event.values[1] * dt * scale;
            float velvZ = prev_velcZ + event.values[2] * dt * scale;

            //Find Posisiton
            try {
                PosSem.acquire();

                xPos = (float) (prev_posX + .5 * (prev_velcX - velvX) * dt);
                yPos = (float) (prev_posY + .5 * (prev_velcY - velvY) * dt);
                zPos = (float) (prev_posZ + .5 * (prev_velcZ - velvZ) * dt);
                PosSem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //You no worry bout this
            if (xPos <= 0)
                xPos = 0;
            if (xPos >= 1080)
                xPos = 1080;
            if (yPos <= 0)
                yPos = 0;
            if (yPos >= 1920)
                yPos = 1920;

            //Calls the function in main activity
            // activity.view1.touch_move(xPos, yPos, zPos);

            //Set current to prevs
            prev_accelZ = event.values[2];
            prev_accelY = event.values[1];
            prev_accelX = event.values[0];
            prev_velcX = velvX;
            prev_velcY = velvY;
            prev_velcZ = velvZ;


            prev_time = event.timestamp;

//            Log.d("TimeShit", "########################################");
//            Log.d("TimeShit", "Time diff:" + dt);
//            Log.d("TimeShit", "accelX:" + prev_accelX + " accelY:" + prev_accelY + " accelZ:" + prev_accelZ);
//            Log.d("TimeShit", "velX:" + prev_velcX + " velY:" + prev_velcY + " velZ:" + prev_velcZ);
//            Log.d("TimeShit", "posX:" + prev_posX + " posY:" + prev_posY + " posZ:" + prev_posZ);
//            Log.d("TimeShit", "PosX Diff:" + (xPos - prev_posX) + " PosY Diff:" + (yPos - prev_posY) + "PosZ Diff:" + (zPos - prev_posZ));
            prev_posX = xPos;
            prev_posY = yPos;
            prev_posZ = zPos;
        }
    }

    public float[] getSumPos() {
        float[] poss = {0.0f, 0.0f, 0.0f};
        try {
            PosSem.acquire();
            poss = new float[]{posXSum, posYSum, posZSum};
            posXSum = posYSum = posYSum = 0.0f;
            PosSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return poss;

    }

    public boolean hprDirty() {
        return mHPRDirtied;
    }
    public float[] getHPR() {
        float[] rot = {0.0f, 0.0f, 0.0f};

        try {
            HDRSem.acquire();
            rot = new float[]{yaw, pitch, roll};
            pitch = yaw = roll = 0.0f;
            HDRSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mHPRDirtied = false;
        return rot;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
