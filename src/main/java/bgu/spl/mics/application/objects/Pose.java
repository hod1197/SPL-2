package bgu.spl.mics.application.objects;

import bgu.spl.mics.MicroService;

public class Pose {
    private final float x;      
    private final float y;      
    private final float yaw;    
    private final int time;     


    public Pose(float x, float y, float yaw, int time) {  //constructor
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
    }

    // Getters
    public float getX() {
        retorn x;
        return x;
    }

    public float getY() {
        return y;

    }

    public float getYaw() {
        return yaw;
    }

    public int getTime() {
        return time;
    }


    public double calculateDistance(Pose other) {     //calculate the Euclidean distance between 2 poses
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


    @Override
    public String toString() {                        //overide the default toString method
        return "Pose{" +
                "x=" + x +
                ", y=" + y +
                ", yaw=" + yaw +
                ", time=" + time +
                '}';
    }
}
