package com.prox.entities;

import com.prox.renderer.models.primitives.Sphere;
import com.prox.util.math.Maths;
import com.prox.util.math.Ray;

import org.joml.*;
import org.joml.Math;

// AABB Collision Class
public class Collision {
    
    // Box-Box
    public static boolean RectRect(Entity one, Entity two) {
        BoundingBox a = one.getBB();
        BoundingBox b = two.getBB();
        return ( 
                (a.min.x <= b.max.x && a.max.x >= b.min.x) &&
                (a.min.y <= b.max.y && a.max.y >= b.min.y) &&
                (a.min.z <= b.max.z && a.max.z >= b.min.z) 
               );
    }

    // Point-Box
    public static boolean PointRect(Vector3f point, Entity one) {
        BoundingBox a = one.getBB();
        return (
                (point.x >= a.min.x && point.x <= a.max.x) &&
                (point.y >= a.min.y && point.y <= a.max.y) &&
                (point.z >= a.min.z && point.z <= a.max.z)
               );
    }

    // Point-Sphere
    public static boolean PointSphere(Vector3f point, Sphere one) {
        Vector3f sphere = one.getBB().getPosition();
        float distance = Math.sqrt((point.x - sphere.x) * (point.x - sphere.x) +
                                   (point.y - sphere.y) * (point.y - sphere.y) +
                                   (point.z - sphere.z) * (point.z - sphere.z));
        return distance < one.getBB().getRadius();
    }

    // Sphere-sphere
    public static boolean SphereSphere(Sphere one, Sphere two) {
        Vector3f sphere = one.getBB().getPosition();
        Vector3f other = two.getBB().getPosition();
        float distance = Math.sqrt((sphere.x - other.x) * (sphere.x - other.x) +
                           (sphere.y - other.y) * (sphere.y - other.y) +
                           (sphere.z - other.z) * (sphere.z - other.z));
        return distance < (one.getBB().getRadius() + two.getBB().getRadius());
    }

    // Box-Sphere
    public static boolean RectSphere(Entity one, Sphere two) {
        BoundingBox box = one.getBB();
        Vector3f sphere = two.getBB().getPosition();
        float x = Math.max(box.min.x, Math.min(sphere.x, box.max.x));
        float y = Math.max(box.min.y, Math.min(sphere.y, box.max.y));
        float z = Math.max(box.min.z, Math.min(sphere.z, box.max.z));
        float distance = Math.sqrt((x - sphere.x) * (x - sphere.x) +
                                   (y - sphere.y) * (y - sphere.y) +
                                   (z - sphere.z) * (z - sphere.z));
        return distance < two.getBB().getRadius();
    }

    // Ray-Box
    public static boolean RayBox(Entity one, Ray ray) {
        Vector3f lb = one.getBB().min;
        Vector3f rt = one.getBB().max;
        Vector3f org = ray.getOrigin();
        Vector3f dir = ray.getDirection();

        // r.dir is unit direction vector of ray
        float dirfracX = 1.0f / dir.x;
        float dirfracY = 1.0f / dir.y;
        float dirfracZ = 1.0f / dir.z;
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        float t1 = (lb.x - org.x)*dirfracX;
        float t2 = (rt.x - org.x)*dirfracX;
        float t3 = (lb.y - org.y)*dirfracY;
        float t4 = (rt.y - org.y)*dirfracY;
        float t5 = (lb.z - org.z)*dirfracZ;
        float t6 = (rt.z - org.z)*dirfracZ;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
        if (tmax < 0)
        {
            ray.t = tmax;
            return false;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax)
        {
            ray.t = tmax;
            return false;
        }

        ray.t = tmin;
        return true;
    }

    // Ray-Sphere
    public static boolean RaySphere(Entity one, Ray ray) {

        float t0, t1; // solutions for t if the ray intersects 

        float radius2 = one.getBB().getRadius() * one.getBB().getRadius();
        // geometric solution
        Vector3f L = one.getPosition().sub(ray.getOrigin(), new Vector3f()); 
        float tca = L.dot(ray.getDirection()); 
        // if (tca < 0) return false;
        float d2 = L.dot(L) - tca * tca; 
        if (d2 > radius2) return false; 
        float thc = Math.sqrt(radius2 - d2); 
        t0 = tca - thc; 
        t1 = tca + thc; 

        ray.t = t0;
        return true;

    }
  
}
