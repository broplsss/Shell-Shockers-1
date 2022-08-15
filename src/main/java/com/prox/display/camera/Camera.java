package com.prox.display.camera;

import static org.lwjgl.glfw.GLFW.*;

import com.prox.display.scenes.BasicScene;
import com.prox.entities.Entity;
import com.prox.util.*;

import org.joml.*;
import org.joml.Math;


public class Camera {

    // Mostly First-Person camera system
    public float Yaw = -90.0f; // Initial Yaw
    public float Pitch = 20.0f; // Initial Pitch
    public float roll;
    public float Zoom = 60.0f; // Initial Zoom

    // Third-Person camera system
    public float distanceFromPlayer = 15.0f; // Initial
    public float angleAroundPlayer = 15.0f; // Initial

    private float SPEED = 30.0f; // Movement Speed
    private float SENSITIVITY = 0.1f; // Mouse Sensitivity
    private Vector3f WORLDUP = new Vector3f(0.0f, 1.0f, 0.0f);

    private Matrix4f projectionMatrix, viewMatrix;
    public Vector3f position; // x, y, z
    public Vector3f target; // x, y, z
    public Vector3f cameraFront; // calculated from ^
    public Vector3f cameraRight = new Vector3f(); // 'right' axis, calculated from the cross product of the 'up' vector and the cameraFront.
    public Vector3f cameraUp = new Vector3f(); // 'up' axis, calculated from the cross product of the cameraFront and 'right' axis.

    public Camera(Vector3f position, Vector3f target) {
        this.position = new Vector3f(position);
        this.target = new Vector3f(target);
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        setTarget(target);
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity(); // sets the projection matrix to an identity matrix
        projectionMatrix.perspective(Math.toRadians(Zoom), 1920.0f / 1080.0f, 0.1f, 1000.0f); // FOV, aspect ratio, near plane, far plane
        // float xOrtho = 1920.0f / 30.0f;
        // float yOrtho = 1080.0f / 30.0f;
        // projectionMatrix.ortho(-xOrtho, xOrtho, -yOrtho, yOrtho, 0.0f, 1000.0f); // left, right, bottom, top, front, back (creates a cubic frustum)
    }

    // Custom view matrix
    public Matrix4f getViewMatrix(Vector3f position, Vector3f target, Vector3f cameraUp) {
        this.viewMatrix.identity();
        viewMatrix = viewMatrix.lookAt(position, target, cameraUp); // Creates a lookAt 'view' matrix from our position, target, and 'up' axis
        return this.viewMatrix;
    }

    // Current camera view matrix
    public Matrix4f getViewMatrix() {
        this.viewMatrix.identity();
        viewMatrix = viewMatrix.lookAt(this.position, this.position.add(this.cameraFront, this.target), this.cameraUp); 
        return this.viewMatrix;
    }

    public void ProcessKeyboard(Camera_Movement movement, float deltaTime) {
        float velocity = SPEED * deltaTime;
        if (movement == Camera_Movement.FORWARD)
            position.add(this.cameraFront.mul(velocity, new Vector3f()));
        if (movement == Camera_Movement.BACKWARD)
            position.sub(this.cameraFront.mul(velocity, new Vector3f()));
        if (movement == Camera_Movement.LEFT)
            position.sub(this.cameraRight.mul(velocity, new Vector3f()));
        if (movement == Camera_Movement.RIGHT)
            position.add(this.cameraRight.mul(velocity, new Vector3f()));
    }

    public void ProcessMouseMovement(float xOffset, float yOffset, boolean constrainPitch) {
        xOffset *= SENSITIVITY;
        yOffset *= SENSITIVITY;

        Yaw += xOffset;
        Pitch -= yOffset;

        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
        {
            if (Pitch > 89.0f)
                Pitch = 89.0f;
            if (Pitch < -89.0f)
                Pitch = -89.0f;
        }

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    // Fly-Around
    public void ProcessMouseScroll(float yOffset)
    {
        // yOffset *= 0.05f;
        Zoom -= (float) yOffset;
        if (Zoom < 5.0f)
            Zoom = 5.0f;
        if (Zoom > 60.0f)
            Zoom = 60.0f; 
    }

    public void updateCameraVectors() {
        // Calculate the new direction vector
        Vector3f direction = new Vector3f();
        direction.x = (float) (Math.cos(Math.toRadians((float)Yaw)) * Math.cos(Math.toRadians((float)Pitch)));
        direction.y = (float) (Math.sin(Math.toRadians((float)Pitch)));
        direction.z = (float) (Math.sin(Math.toRadians((float)Yaw)) * Math.cos(Math.toRadians((float)Pitch)));
        this.cameraFront = direction.normalize();
        // Re-calculate the Right and Up vector
        direction.cross(WORLDUP, cameraRight).normalize();
        cameraRight.cross(direction, cameraUp).normalize();
    }


    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setPosition(Vector3f pos) {
        this.position = new Vector3f(pos);
    }

    public void setTarget(Vector3f target) {
        this.target = new Vector3f(target);
        cameraFront = this.position.sub(target, new Vector3f());
        cameraFront.negate(); // idk bruh, camera is backwards?
        this.cameraFront.normalize();
        // Re-calculate the Right and Up vector
        cameraFront.cross(WORLDUP, cameraRight).normalize();
        cameraRight.cross(cameraFront, cameraUp).normalize();
    }

    // Third-Person Camera
    public void calculateZoom() {
        float zoomLevel = MouseListener.getScrollY() * 0.6f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer < 5.0f) {
            distanceFromPlayer = 5.0f;
        }
        if (distanceFromPlayer > 50.0f) {
            distanceFromPlayer = 50.0f;
        }
    }

    public void calculatePitch() {
        // if (MouseListener.mouseButtonDown(1)) {
            float pitchChange = MouseListener.getDy() * 0.1f;
            Pitch += pitchChange;
            if (Pitch > 65.0f)
                Pitch = 65.0f;
            if (Pitch < -20.0f)
                Pitch = -20.0f;
        // }
    }

    public void calculateAngleAroundPlayer() {
        // if (MouseListener.mouseButtonDown(0)) {
            float angleChange = MouseListener.getDx() * 0.2f;
            angleAroundPlayer -= angleChange;
        // }
    }

    private float calculateHorizontalDistance() {
        return distanceFromPlayer * Math.cos(Math.toRadians(Pitch));
    }

    private float calculateVerticalDistance() {
        return distanceFromPlayer * Math.sin(Math.toRadians(Pitch));
    }


    // Third Person
    private void calculateCameraPositionThird(Entity player, float horizDistance, float verticDistance) {
        float theta = player.getRotation().y + angleAroundPlayer;
        float offsetX = horizDistance * Math.sin(Math.toRadians(theta));
        float offsetZ = horizDistance * Math.cos(Math.toRadians(theta));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticDistance;
    }

    public void updateBoundEntities(float deltaTime) {
        for (Entity e : BasicScene.entities) {
            if (e.getTag() == 4) {
                
            }
        }
    }

    public void ThirdPerson(Entity player, float deltaTime) {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPositionThird(player, horizontalDistance, verticalDistance);
        // this.Yaw = 180 - (player.getRotation().y + angleAroundPlayer);
        float offsetXExtra = 4.0f * Math.sin(Math.toRadians(angleAroundPlayer + 90.0f));
        float offsetZExtra = 4.0f * Math.cos(Math.toRadians(angleAroundPlayer + 90.0f));
        setTarget(player.getPosition().sub(offsetXExtra, -3.0f, offsetZExtra, new Vector3f()));
        player.getRotation().y = Math.toRadians(angleAroundPlayer - 105.0f);  // Jerry Rigged
        updateBoundEntities(deltaTime);
    }

    // Fix
    public void FirstPerson(Entity player, float deltaTime) {
        setPosition(player.getPosition().x, player.getPosition().y + 4.0f, player.getPosition().z);
        ProcessMouseMovement(MouseListener.getDx(), MouseListener.getDy(), true);
        ProcessMouseScroll(MouseListener.getScrollY());
        player.getRotation().y = Math.toRadians(-Yaw);

    }

    public void FlyAround(float deltaTime) {
        ProcessMouseMovement(MouseListener.getDx(), MouseListener.getDy(), true);
        ProcessMouseScroll(MouseListener.getScrollY());
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            this.ProcessKeyboard(Camera_Movement.FORWARD, deltaTime);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            this.ProcessKeyboard(Camera_Movement.BACKWARD, deltaTime);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            this.ProcessKeyboard(Camera_Movement.LEFT, deltaTime);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.ProcessKeyboard(Camera_Movement.RIGHT, deltaTime);
        }
    }

    
    public Matrix4f getProjectionMatrix() {
        adjustProjection();
        return this.projectionMatrix;
    }
}