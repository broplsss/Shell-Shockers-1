package com.prox.display.scenes;

import com.prox.display.camera.Camera;

public abstract class Scene {

    protected Camera camera;

    public Scene() {

    }

    public void init(long glfwWindow) {
        
    }

    public abstract void update(float dt);
    
}