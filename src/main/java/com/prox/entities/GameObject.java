package com.prox.entities;

import com.prox.display.camera.Camera;
import com.prox.display.scenes.BasicScene;
import com.prox.renderer.Shader;
import com.prox.util.Time;

import org.joml.Vector3f;


public class GameObject extends Entity {


    public GameObject(String mesh, String texture, Shader shader, BBType bb) {
        super(mesh, texture, shader, bb);
    }

    public GameObject(String mesh, String texture, Shader shader, boolean isTerrain) {
        super(mesh, texture, shader, isTerrain);
    }

    public GameObject(String mesh, String texture, Shader shader, BBType bb, Vector3f pos) {
        super(mesh, texture, shader, bb, pos);
    }

    public GameObject(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, float scale) {
        super(mesh, texture, shader, bb, pos, scale);
    }

    public GameObject(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, Vector3f scale) {
        super(mesh, texture, shader, bb, pos, scale);
    }

    public GameObject(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, Vector3f rot, float scale) {
        super(mesh, texture, shader, bb, pos, rot, scale);
    }

    private boolean firstFrameCollision = true;

    public void processCollisions(float deltaTime) {
        if (firstFrameCollision) {
            firstFrameCollision = false;
            return;
        }
    }

    @Override
    public void update(float deltaTime, Camera camera) {
        if (hasBB()) {
            getBB().update(deltaTime);
            processCollisions(deltaTime);
        }
    }
 
    @Override
    public void render() {
        super.render();
    }
 
}
