package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Pose class.
 */
public class PoseTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        float x = 10.5f;
        float y = 20.7f;
        float yaw = 45.0f;
        int time = 5;

        // Act
        Pose pose = new Pose(x, y, yaw, time);

        // Assert
        assertEquals(x, pose.getX(), 0.0001, "X coordinate should match");
        assertEquals(y, pose.getY(), 0.0001, "Y coordinate should match");
        assertEquals(yaw, pose.getYaw(), 0.0001, "Yaw should match");
        assertEquals(time, pose.getTime(), "Time should match");
    }

    @Test
    public void testCalculateDistance() {
        // Arrange
        Pose pose1 = new Pose(10.0f, 10.0f, 0.0f, 1);
        Pose pose2 = new Pose(13.0f, 14.0f, 0.0f, 2);

        // Act
        double distance = pose1.calculateDistance(pose2);

        // Assert
        assertEquals(5.0, distance, 0.0001, "Distance calculation should be correct");
    }

    @Test
    public void testToString() {
        // Arrange
        Pose pose = new Pose(5.0f, 15.0f, 90.0f, 3);

        // Act
        String poseString = pose.toString();

        // Assert
        assertTrue(poseString.contains("x=5.0"), "String should contain x value");
        assertTrue(poseString.contains("y=15.0"), "String should contain y value");
        assertTrue(poseString.contains("yaw=90.0"), "String should contain yaw value");
        assertTrue(poseString.contains("time=3"), "String should contain time value");
    }
}
