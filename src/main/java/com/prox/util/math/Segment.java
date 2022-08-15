package com.prox.util.math;

import org.joml.Vector2f;

public class Segment {
    
        
    public Vector2f pointA;
    public Vector2f pointB;

    public Segment(Vector2f pointA, Vector2f pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public void setPointA(Vector2f A) {
        this.pointA = A;
    }

    public void setPointB(Vector2f B) {
        this.pointB = B;
    }

    public Vector2f checkIntersection(Segment lin) {

          // (xi, yj) values for first line
          float x1 = lin.pointA.x;
          float y1 = lin.pointA.y;
          float x2 = lin.pointB.x;
          float y2 = lin.pointB.y;
  
          // (xi, yj) values for second line
          float x3 = this.pointA.x;
          float y3 = this.pointA.y;
          float x4 = this.pointB.x;
          float y4 = this.pointB.y;
         
          float den = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
          if (den == 0) {
              return null;
          }
  
          float t = (x1 - x3)*(y3 - y4) - (y1 - y3)*(x3 - x4) / den;
          float u = (x1 - x3)*(y1 - y2) - (y1 - y3)*(x1 - x2) / den;
  
          if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
              float Px = x1 + t*(x2 - x1);
              float Py = y1 + t*(y2 - y1);
              return ( new Vector2f(Px, Py) );
          }
          else { return null; }
    }

    // public Ray asRay() {
    //     return new Ray(pointA, pointB.sub(pointA, new Vector2f()).normalize());
    // }
    
}
