package com.prox.display.scenes;

import com.prox.display.camera.*;
import com.prox.entities.BBType;
import com.prox.entities.BoundingBox;
import com.prox.entities.Collision;
import com.prox.entities.Entity;
import com.prox.entities.GameObject;
import com.prox.entities.Player;
import com.prox.renderer.*;

import com.prox.renderer.models.primitives.*;
import com.prox.util.*;
import com.prox.util.math.Ray;

import org.joml.*;
import org.joml.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Map.Entry;

import static org.lwjgl.opengl.GL30.*;

public class BasicScene extends Scene {

    private long glfwWindow;

    // Entities
    public static ArrayList<Entity> entities = new ArrayList<Entity>();

    public static Ray pRay;
    public static Vector3f rayEnd;

    // Shaders
    public static ArrayList<Shader> shaders = new ArrayList<Shader>();
    public static Shader phongShader;
    public static Shader outlineShader;
    public static Shader baseShader;
    public static Shader UIShader;

    // Lighting
    private Lighting lighting;
 
    public BasicScene() {
        System.out.println("Inside basic scene"); 
    }

    public void init(long glfwWindow) {

        this.glfwWindow = glfwWindow;

        // =============================================================
        //                  Textures & Models & Shaders
        // =============================================================

        // Shaders
            phongShader = new Shader("prox/assets/shaders/phongLighting.glsl");
            phongShader.compile();

            outlineShader = new Shader("prox/assets/shaders/outline.glsl");
            outlineShader.compile();

            baseShader = new Shader("prox/assets/shaders/object.glsl");
            baseShader.compile();

            UIShader = new Shader("prox/assets/shaders/UI.glsl");
            UIShader.compile();

        // Entities
            Entity egg = new Player("prox/assets/models/egg.obj", "prox/assets/textures/lightBrown.png", phongShader, BBType.RECTANGLE);
                egg.setOutlineShader(outlineShader);
                // egg.enableOutline();
                egg.setTag(0);
                entities.add(egg); // Player always at index 0
            Entity shotgun = new GameObject("prox/assets/models/shotgun.obj", "prox/assets/textures/carbon.jpeg", baseShader, BBType.RECTANGLE);
                shotgun.enableBB(false);
                shotgun.setPosition(egg.getPosition());
                shotgun.setScale(1.0f);
                shotgun.setTag(5);
                entities.add(shotgun);
            Entity crosshair = new Square("prox/assets/textures/crosshair2.png", UIShader, true);
                crosshair.enableBB(false);
                crosshair.setScale(new Vector3f(0.07f, 0.12f, 0.07f));
                // crosshair.setTag(4);
                crosshair.enableRender(true);
                entities.add(crosshair);
            Entity map = new GameObject("prox/assets/models/hillMap.obj", "prox/assets/textures/stone.jpeg", phongShader, true);
                map.getPosition().y -= 2.5f;
                entities.add(map);
            Entity enemy = new Player("prox/assets/models/egg.obj", "prox/assets/textures/lightBrown.png", phongShader, BBType.RECTANGLE);
                enemy.setOutlineShader(outlineShader);
                // enemy.enableOutline();
                enemy.setTag(0);
                entities.add(enemy);
            Entity sphere = new Sphere( "prox/assets/textures/white.jpeg", phongShader);
                sphere.setTag(2);
                sphere.setPosition(80.0f, 30.0f, 40.0f);
                sphere.setScale(5.0f);
                sphere.enableRender(true);
                sphere.enableBB(true);
                entities.add(sphere);
            Entity sphere2 = new Sphere( "prox/assets/textures/white.jpeg", phongShader);
                sphere2.setTag(1);
                sphere2.setPosition(-80.0f, 70.0f, -60.0f);
                sphere2.setScale(5.0f);
                sphere2.enableRender(true);
                entities.add(sphere2);
            
                
            // for (Entry<Vector2i, Float> ent : BasicScene.entities.get(3).heightMap.entrySet()) {
            //     System.out.println((int)ent.getKey().x + "  " + (int)ent.getKey().y + "  " + ent.getValue());
            // }

                // for (int row = -5; row <= 5; row++) {
            //     for (int col = -5; col <= 5; col++) {
            //         Cube block = new Cube("prox/assets/textures/scifi.jpeg", phongShader);
            //         block.setScale(5.0f);
            //         block.setPosition(row * block.getWidth(), 0.0f, col * block.getDepth());
            //         entities.add(block);
            //     }
            // }

            // Cube block1 = new Cube("prox/assets/textures/scifi.jpeg", phongShader);
            // block1.setScale(5.0f);
            // block1.setPosition(3 * block1.getWidth(), block1.getHeight(), 3 * block1.getDepth());
            // entities.add(block1);

            // Cube block2 = new Cube("prox/assets/textures/scifi.jpeg", phongShader);
            // block2.setScale(5.0f);
            // block2.setPosition(-4 * block2.getWidth(), block2.getHeight(), -5 * block2.getDepth());
            // entities.add(block2);
            

        for (Entity e : entities) {
            e.setOutlineShader(outlineShader);    
        }            
        // =============================================================
        //              Miscellaneous Settings + Uniforms + Camera
        // =============================================================

        //  Tells OpenGl to use Depth Testing
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        // Tells OpenGl to use Stencil Testing
        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE); 

        // Tells OpenGl to only render front-facing triangles
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        // Hide the cursor and keep its focus in the window
        // glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Camera
        camera = new Camera(new Vector3f(25.0f, 25.0f, 25.0f), new Vector3f(0.0f, 0.0f, 0.0f));

        // Lighting
        lighting = new Lighting(phongShader); // uploads all the uniforms and creates the meshes

        pRay = new Ray(new Vector3f(camera.position), new Vector3f(camera.cameraFront));

    }

    private Matrix4f view;
    private Matrix4f projection; 

    @Override
    public void update(float deltaTime) {

        // Take in user input (keyboard)
        processInput(glfwWindow, deltaTime);

        // ... Shader stuff goes here ... 

        
        // ... update everything ...

        pRay = new Ray(new Vector3f(camera.position), new Vector3f(camera.cameraFront));
        float tempT = 300.0f;

        Entity select = null;

        for (Entity e : entities) {
            e.update(deltaTime, camera);
            // Blocks only
            if (e == entities.get(0) || e == entities.get(1) || e == entities.get(2) || e.hasBB() == false) {
                continue;
            }
            if (e.getBB().bbType() == BBType.RECTANGLE) {
                if (Collision.RayBox(e, pRay)) {
                    if (pRay.t < tempT) {
                        tempT = pRay.t;
                        select = e;
                    }
                }
            }
            if (e.getBB().bbType() == BBType.SPHERE) {
                if (Collision.RaySphere(e, pRay)) {
                    if (pRay.t < tempT) {
                        tempT = pRay.t;
                        select = e;
                    }
                }
            }
        }

        Vector3f direction = pRay.getDirection();
        direction.mul(tempT);
        rayEnd = pRay.getOrigin().add(direction, new Vector3f());
        // entities.get(3).setPosition(rayEnd);


        // ... camera stuff goes here ...

        switch(cameraPerspective) {
            case 1: camera.FirstPerson(entities.get(0), deltaTime); break;
            case 2: camera.FlyAround(deltaTime); break;
            case 3: camera.ThirdPerson(entities.get(0), deltaTime); break;
        }        

        view  = camera.getViewMatrix();
        projection = camera.getProjectionMatrix();

        lighting.update(deltaTime, camera);

        // ... render everything ...

            baseShader.uploadMat4f("projection", projection);
            baseShader.uploadMat4f("view", view);
            baseShader.detach();
            
            phongShader.uploadMat4f("projection", projection);
            phongShader.uploadMat4f("view", view);
            phongShader.detach();

            // lighting.render();

            // if (select != null) {
            //     select.enableOutline();
            //     select.setOutlineShader(outlineShader);
            // }
            
            // Draw the Models
          
                for (Entity e : entities) {
                    if (e.hasOutline()) {
                        glStencilFunc(GL_ALWAYS, 1, 0xFF); 
                        glStencilMask(0xFF); // draw to the stencil buffer
                            e.render();
                        glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
                        glStencilMask(0x00); 
                        // glDisable(GL_DEPTH_TEST);
                            e.outline(view, projection);
                        glStencilMask(0xFF);
                        glStencilFunc(GL_ALWAYS, 0, 0xFF);   
                        // glEnable(GL_DEPTH_TEST);
                    } else {
                        glClear(GL_STENCIL_BUFFER_BIT);
                        glStencilMask(0x00);
                        e.render();
                    }
                   
                }

            // if (select != null) {
            //     select.disableOutline();
            // }
                
        // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

        MouseListener.endFrame(); // re-calculate mouse position attributes

        // Unbind the VAO and detach the shader programs
        glBindVertexArray(0);
        
    }

    private int cameraPerspective = 3;

    public static boolean wireframe = false;
    private float timeSinceWire = Time.getTime();

    public static boolean boundaries = false;
    private float timeSinceBounds = Time.getTime();

    // Take in keyboard input (per frame)
    public void processInput(long window, float deltaTime) {

        // Closing the Window
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true);
        }
        // Enable the Cursor
        if (KeyListener.isKeyPressed(GLFW_KEY_X)) {
            glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        // Changing the camera perspective
        if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
            camera = new Camera(new Vector3f(25.0f, 25.0f, 25.0f), new Vector3f(0.0f, 0.0f, 0.0f));
            cameraPerspective = 1;
            entities.get(0).enableRender(false);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
            camera = new Camera(new Vector3f(25.0f, 25.0f, 25.0f), new Vector3f(0.0f, 0.0f, 0.0f));
            cameraPerspective = 2;
            entities.get(0).enableRender(true);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
            camera = new Camera(new Vector3f(25.0f, 25.0f, 25.0f), new Vector3f(0.0f, 0.0f, 0.0f));
            entities.get(0).enableRender(true);
            cameraPerspective = 3;
        }

        // Specifies how we want to rasterize objects 
        if (KeyListener.isKeyPressed(GLFW_KEY_F) && wireframe == false && Time.getTime() - timeSinceWire > 0.3f) {
            wireframe = true;
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // face (front, back, both), type (fill, line, point)
            timeSinceWire = Time.getTime();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_F) && wireframe == true && Time.getTime() - timeSinceWire > 0.3f) {
            wireframe = false;
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            timeSinceWire = Time.getTime();
        }

        // Render bounding boxes
        if (KeyListener.isKeyPressed(GLFW_KEY_B) && boundaries == false && Time.getTime() - timeSinceBounds > 0.3f) {
            boundaries = true;
            for (Entity e : entities) {
                if (e.hasBB()) {
                    e.renderBB(true);
                }
            }
            timeSinceBounds = Time.getTime();
        } else if (KeyListener.isKeyPressed(GLFW_KEY_B) && boundaries == true && Time.getTime() - timeSinceBounds > 0.3f) {
            boundaries = false;
            for (Entity e : entities) {
                if (e.hasBB()) {
                    e.renderBB(false);
                }
            }            
            timeSinceBounds = Time.getTime();
        }

    }

}
 