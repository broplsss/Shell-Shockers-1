package com.prox.entities;

import com.prox.display.camera.Camera;
import com.prox.display.scenes.BasicScene;
import com.prox.renderer.Shader;
import com.prox.renderer.models.primitives.Sphere;
import com.prox.util.KeyListener;
import com.prox.util.MouseListener;
import com.prox.util.Time;
import com.prox.util.math.Interpolation;
import com.prox.util.math.Ray;

import java.util.Map.Entry;

import org.joml.*;
import org.joml.Math;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;

public class Player extends Entity {

    public boolean movingForward, movingBackward, movingLeft, movingRight, spinRight, spinLeft, jumping, inAir, collidingUp, shooting;

    public final float MAX_SPEED = 20.0f;
    private float SPEED_F;
    private float SPEED_B;
    private float SPEED_L;
    private float SPEED_R;
    private float SPIN = 5.0f;

    private float upwardsSpeed;
    private float GRAVITY = -80.0f;

    private float TERRAIN_HEIGHT = 50.f;
    private float lastHeight = 50.0f;
    private float timeofLastHeight;
    private float timeSinceLastHeight;

    private HashMap<Vector2i, Float> heightMap;

    private Vector3f WORLD_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    public Player(String mesh, String texture, Shader shader, BBType bb) {
        super(mesh, texture, shader, bb);
        setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
        setRotation(new Vector3f(0.0f, 0.0f, 0.0f));
        getRotation().y = Math.toRadians(-90); // Make the face the front

        if (bb == BBType.RECTANGLE) {
            getBB().setDimensions(3.0f, 5.0f, 3.0f);
        } else if (bb == BBType.SPHERE) {
            getBB().setRadius(5.0f);
        }
    }

    public Sphere bullet;
    public boolean hit;
    public Vector3f bulletDir;

    @Override
    public void update(float deltaTime, Camera camera) {
        movingForward = false;
        movingBackward = false;
        movingLeft = false;
        movingRight = false;
        spinRight = false;
        spinLeft = false;
        jumping = false;

        processInput();
        movePlayer(deltaTime);

        if (shooting) {
            hit = false;
            bullet = new Sphere(BasicScene.baseShader);
            bullet.setPosition(this.position);
            bulletDir = BasicScene.rayEnd.sub(this.position, new Vector3f()).normalize();
            for (Entity e : BasicScene.entities) {
                e.disableOutline();
            }
        }
        
        if (bullet != null) {
            if (!hit) {
                updateBullet(bullet, deltaTime);
            }
        }

        // prob don't need
        updateAccesories(deltaTime, camera);

        getBB().update(deltaTime);
        // System.out.println(getPosition().x + "  " + getPosition().z + "  " + getPosition().y);
    }

    public void updateBullet(Sphere bullet, float deltaTime) {
        float velocity = 150.0f * deltaTime;
        Vector3f movement = bulletDir.mul(velocity, new Vector3f());
        bullet.getPosition().add(movement);
        bullet.getBB().update(deltaTime);
        for (Entity e : BasicScene.entities) {
            if (e == BasicScene.entities.get(0) || e.hasBB() == false) {
                continue;
            }
            if (e.getBB().bbType() == BBType.RECTANGLE) {
                if (Collision.RectSphere(e, bullet)) {
                    bullet.getPosition().sub(movement.mul(0.6f));
                    e.enableOutline();
                    hit = true;
                    bullet.getBB().update(deltaTime);
                }
            }
            if (e.getBB().bbType() == BBType.SPHERE) {
                if (Collision.SphereSphere((Sphere) e, bullet)) {
                    bullet.getPosition().sub(movement.mul(0.6f));
                    e.enableOutline();
                    hit = true;
                    bullet.getBB().update(deltaTime);
                }
            }
        }

        if (bullet.getPosition().y < getElevation(bullet.getPosition().x, bullet.getPosition().z)) {
            bullet.getPosition().sub(movement.mul(0.6f));
            hit = true;
            bullet.getBB().update(deltaTime);
        }
       
    }

    @Override
    public void render() {
        super.render();
        if (bullet != null)
            bullet.render();
    }

    public void movePlayer(float deltaTime) {
        if (movingForward) {
            float velocity = MAX_SPEED * deltaTime;
            Vector3f movement = this.getDirection().mul(velocity, new Vector3f());
            getPosition().add(movement);
            getBB().update(deltaTime);
            if (processCollisions(deltaTime, movement)) {
                getPosition().sub(movement);
                // Vector3f lateral = this.getDirection().cross(WORLD_UP, new Vector3f());
                // Vector3f projMovement = lateral.mul(this.getDirection().dot(lateral)/((lateral.length() * lateral.length())), new Vector3f());
                // getPosition().add(projMovement);
                // if (processCollisions(deltaTime, projMovement)) {
                //     getPosition().sub(projMovement);
                // }
            }
        }
        if (movingBackward) {
            float velocity = MAX_SPEED * deltaTime;
            Vector3f movement = this.getDirection().mul(velocity, new Vector3f()).negate();
            getPosition().add(movement);
            getBB().update(deltaTime);
            if (processCollisions(deltaTime, movement)) {
                getPosition().sub(movement);
            }
        } 
        if (movingLeft) {
            float velocity = MAX_SPEED * deltaTime;
            Vector3f movement = this.getDirection().cross(WORLD_UP, new Vector3f()).normalize().mul(velocity, new Vector3f()).negate();
            getPosition().add(movement);
            getBB().update(deltaTime);
            if (processCollisions(deltaTime, movement)) {
                getPosition().sub(movement);
            }
        }
        if (movingRight) {
            float velocity = MAX_SPEED * deltaTime;
            Vector3f movement = this.getDirection().cross(WORLD_UP, new Vector3f()).normalize().mul(velocity, new Vector3f());
            getPosition().add(movement);
            getBB().update(deltaTime);
            if (processCollisions(deltaTime, movement)) {
                getPosition().sub(movement);
            }
        }

        // getPosition().add(finalMove);
        // getBB().update(deltaTime);
        // if (processCollisions(deltaTime)) {
        //     getPosition().sub(finalMove);
        // }

        if (jumping)
        {
            if (!inAir) {
                upwardsSpeed = 40.0f;
                inAir = true;
            }
        }
        
                // if (spinLeft) {
                //     float spin = SPIN * deltaTime;
                //     getRotation().y += spin;
                // }
                // if (spinRight) {
                //     float spin = SPIN * deltaTime;
                //     getRotation().y -= spin;
                // }

        collidingUp = false;
        upwardsSpeed += GRAVITY * deltaTime;
        float movementU = upwardsSpeed * deltaTime;
        this.getPosition().y += movementU;
        getBB().update(deltaTime);
        if (processCollisions(deltaTime, new Vector3f(0.0f, movementU, 0.0f))) {
            getPosition().y -= movementU;
            upwardsSpeed = 0.0f;
            jumping = false;
            inAir = collidingUp;
        }

        // try {

        //     float xPos = getPosition().x;
        //     float zPos = getPosition().z;

        //     int minX = (int) Math.floor(getPosition().x);
        //     int minZ = (int) Math.floor(getPosition().z);
        //     int maxX = (int) Math.ceil(getPosition().x);
        //     int maxZ = (int) Math.ceil(getPosition().z);

        //     Vector3f bl = new Vector3f(minX, getElevation(minX, minZ), minZ); // bot-left
        //     Vector3f br = new Vector3f(maxX, getElevation(maxX, minZ), minZ); // bot-right
        //     Vector3f tl = new Vector3f(minX, getElevation(minX, maxZ), maxZ); // top-left
        //     Vector3f tr = new Vector3f(maxX, getElevation(maxX, maxZ), maxZ); // top-right

        //     if (xPos <= (1-zPos)) {
        //         TERRAIN_HEIGHT = Interpolation.barryCentric(tl, bl, tr, new Vector2f(xPos, zPos));
        //     } else {
        //         TERRAIN_HEIGHT = Interpolation.barryCentric(tr, br, bl, new Vector2f(xPos, zPos));
        //     }
            
        // } catch (Exception e) {
        //     TERRAIN_HEIGHT = getElevation(getPosition().x, getPosition().z);
        //     System.out.println((int)getPosition().x + "  " + (int)getPosition().z);
        // }

        
        if (getPosition().y < TERRAIN_HEIGHT) {
            upwardsSpeed = 0;
            jumping = false;
            inAir = false;
            getPosition().y = TERRAIN_HEIGHT;
        }

    }

    private boolean firstFrameCollision = true;

    public float getElevation(int x, int y) {
        try {
            return BasicScene.entities.get(3).heightMap.get(new Vector2i(x, y));
        } catch (Exception e) {
            return TERRAIN_HEIGHT;
        }
    }

    public float getElevation(float x, float y) {
        try {
            return BasicScene.entities.get(3).heightMap.get(new Vector2i(Math.round(x), Math.round(y)));
        } catch (Exception e) {
            return TERRAIN_HEIGHT;
        }  
    }

    public boolean processCollisions(float deltaTime, Vector3f movement) {
        if (firstFrameCollision) {
            firstFrameCollision = false;
            return false;
        }
        
        for (Entity e: BasicScene.entities) {
            if (e == BasicScene.entities.get(0) || e.hasBB() == false) { // if its the player, ignore.
                continue;
            }

            if (e.getPosition().y > this.getPosition().y) {
                collidingUp = true;
            } else {collidingUp = false;}

            if (e.getBB().bbType() == BBType.RECTANGLE) {
                if (Collision.RectRect(this, e)) {
                    if (e.getTag() == 1) { // Rigid Objects
                        return true;
                    }
                    if (e.getTag() == 2) { // Movable Objects
                        if (Math.abs(e.getPosition().x - this.getPosition().x) > Math.abs(e.getPosition().z - this.getPosition().z)) {
                            e.getPosition().add(movement.x, 0.0f, 0.0f);
                            this.getPosition().add(movement.x, 0.0f, movement.z);
                        } else {
                            e.getPosition().add(0.0f, 0.0f, movement.z);
                            this.getPosition().add(movement.x, 0.0f, movement.z);
                        }
                        return true;
                    }
                } 
            }

            if (e.getBB().bbType() == BBType.SPHERE) { 
                if (Collision.RectSphere(this, (Sphere) e)) {
                    if (e.getTag() == 1) { // Rigid Objects
                        return true;
                    }
                    if (e.getTag() == 2) { // Movable Objects
                        e.getPosition().add(movement.x, 0.0f, movement.z);
                        this.getPosition().add(movement.x, 0.0f, movement.z);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateAccesories(float deltaTime, Camera camera) {
        BasicScene.entities.get(1).setPosition(this.getPosition());
        BasicScene.entities.get(1).getRotation().y = this.getRotation().y;
    }

    private float timeSinceShot = Time.getTime();

    public void processInput() {
        if (MouseListener.mouseButtonDown(0) && shooting == false && Time.getTime() - timeSinceShot > 0.3f) { // left
            shooting = true;
            timeSinceShot = Time.getTime();
        } else {
            shooting = false;
        }
        if (MouseListener.mouseButtonDown(1)) { // right
            
        }
        if (MouseListener.mouseButtonDown(2)) { // middle

        } else {
            
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_O)) {
            // this.enableOutline();
        } else {            
            // this.disableOutline();
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            movingForward = true;
        } 
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            movingBackward = true;
        } 
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            movingLeft = true;
            // spinLeft = true;
        } 
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            movingRight = true;
            // spinRight = true;
        } 
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            jumping = true;
        } 
    }

    public boolean isMovingRight() {return this.movingRight;}
    public boolean isMovingLeft() {return this.movingLeft;}
    public boolean isMovingForward() {return this.movingForward;}
    public boolean isMovingBackward() {return this.movingBackward;}
    public boolean isMoving() {return this.movingRight || this.movingLeft || this.movingForward || this.movingBackward;}
    public boolean isJumping() {return this.jumping;}
    public boolean isInAir() {return this.inAir;}
    
}
