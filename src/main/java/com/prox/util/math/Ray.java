package com.prox.util.math;

import org.joml.*;
import org.joml.Math;

public class Ray {

    private Vector3f origin;
    private Vector3f direction;
    public float t;
    
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;

        if (direction.length() != 1) {
            direction = direction.normalize();
        }
        this.direction = direction;
    }

    // public Segment asLine(float length) {
    //     return new Segment(origin, origin.add(direction.mul(length)));
    // }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getDirection() {
        return direction;
    }
    
}
