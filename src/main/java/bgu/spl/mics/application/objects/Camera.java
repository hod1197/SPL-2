package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    private final int id;  // Unique ID for the camera
    private final int frequency;  // Frequency of operation (ticks)
    private Status status;  // Current status of the camera
    private final List<StampedDetectedObjects> detectedObjectsList;  // Objects detected by the camera

    
    public enum Status {
        UP, DOWN, ERROR
    }


    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        this.status = Status.UP;  // Default status is UP
        this.detectedObjectsList = new ArrayList<>();
    }

    /**
     * Simulates detecting objects and adds them to the detectedObjectsList.
     *
     * @param detectedObjects The objects detected with a timestamp.
     */
    public void detectObjects(StampedDetectedObjects detectedObjects) {
        if (status == Status.UP) {
            detectedObjectsList.add(detectedObjects);
        } else {
            throw new IllegalStateException("Camera is not operational (Status: " + status + ")");
        }
    }

    
    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return new ArrayList<>(detectedObjectsList);  // Returns a copy for safety
    }

    
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }


    public Status getStatus() {
        return status;
    }


    public int getFrequency() {
        return frequency;
    }

    
    public int getId() {
        return id;
    }

    @Override
    public String toString() {     
        return "Camera{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", detectedObjectsList=" + detectedObjectsList +
                '}';
    }
}
