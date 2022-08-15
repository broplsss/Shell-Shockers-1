package com.prox.renderer;

import java.util.ArrayList;

import com.prox.display.camera.Camera;
import com.prox.display.scenes.BasicScene;
import com.prox.renderer.models.primitives.Cube;

import org.joml.Vector3f;

public class Lighting {

    private Shader shader;

    // positions of the point lights
    private Vector3f[] pointLightPositions = {
        new Vector3f( -25.0f,  15.0f,  -25.0f),
        new Vector3f(-25.0f, 15.0f, 25.0f),
        new Vector3f(25.0f,  15.0f, -25.0f),
        new Vector3f(25.0f,  15.0f, 25.0f)
    };

    private ArrayList<Cube> cubes = new ArrayList<Cube>();
    
    public Lighting(Shader shader) {
        this.shader = shader;

        // Create the cube meshes
        for (Vector3f position : pointLightPositions) {
            Cube cube = new Cube(BasicScene.baseShader);
            cube.setScale(2.0f);
            cube.setPosition(position);
            cubes.add(cube);
        }

        /*
           Here we set all the uniforms for the 5/6 types of lights we have. We have to set them manually and index 
           the proper PointLight struct in the array to set each uniform variable. This can be done more code-friendly
           by defining light types as classes and set their values in there, or by using a more efficient uniform approach
           by using 'Uniform buffer objects', but that is something we'll discuss in the 'Advanced GLSL' tutorial.
        */
        // directional light
        shader.uploadVec3f("dirLight.direction", -0.5f, -1.0f, -0.4f);
        shader.uploadVec3f("dirLight.ambient", 0.3f, 0.3f, 0.3f);
        shader.uploadVec3f("dirLight.diffuse", 0.4f, 0.4f, 0.4f);
        shader.uploadVec3f("dirLight.specular", 0.3f, 0.3f, 0.3f);
        // point light 1
        shader.uploadVec3f("pointLights[0].position", pointLightPositions[0]);
        shader.uploadVec3f("pointLights[0].ambient", 0.05f, 0.05f, 0.05f);
        shader.uploadVec3f("pointLights[0].diffuse", 0.8f, 0.8f, 0.8f);
        shader.uploadVec3f("pointLights[0].specular", 1.0f, 1.0f, 1.0f);
        shader.uploadFloat("pointLights[0].constant", 1.0f);
        shader.uploadFloat("pointLights[0].linear", 0.09f);
        shader.uploadFloat("pointLights[0].quadratic", 0.032f);
        // point light 2
        shader.uploadVec3f("pointLights[1].position", pointLightPositions[1]);
        shader.uploadVec3f("pointLights[1].ambient", 0.05f, 0.05f, 0.05f);
        shader.uploadVec3f("pointLights[1].diffuse", 0.8f, 0.8f, 0.8f);
        shader.uploadVec3f("pointLights[1].specular", 1.0f, 1.0f, 1.0f);
        shader.uploadFloat("pointLights[1].constant", 1.0f);
        shader.uploadFloat("pointLights[1].linear", 0.09f);
        shader.uploadFloat("pointLights[1].quadratic", 0.032f);
        // point light 3
        shader.uploadVec3f("pointLights[2].position", pointLightPositions[2]);
        shader.uploadVec3f("pointLights[2].ambient", 0.05f, 0.05f, 0.05f);
        shader.uploadVec3f("pointLights[2].diffuse", 0.8f, 0.8f, 0.8f);
        shader.uploadVec3f("pointLights[2].specular", 1.0f, 1.0f, 1.0f);
        shader.uploadFloat("pointLights[2].constant", 1.0f);
        shader.uploadFloat("pointLights[2].linear", 0.09f);
        shader.uploadFloat("pointLights[2].quadratic", 0.032f);
        // point light 4
        shader.uploadVec3f("pointLights[3].position", pointLightPositions[3]);
        shader.uploadVec3f("pointLights[3].ambient", 0.05f, 0.05f, 0.05f);
        shader.uploadVec3f("pointLights[3].diffuse", 0.8f, 0.8f, 0.8f);
        shader.uploadVec3f("pointLights[3].specular", 1.0f, 1.0f, 1.0f);
        shader.uploadFloat("pointLights[3].constant", 1.0f);
        shader.uploadFloat("pointLights[3].linear", 0.09f);
        shader.uploadFloat("pointLights[3].quadratic", 0.032f);
        
    }

    public void update(float deltaTime, Camera camera) {
        // spotLight
            // shader.uploadVec3f("spotLight.position", camera.position);
            // shader.uploadVec3f("spotLight.direction", camera.cameraFront);
            // shader.uploadVec3f("spotLight.ambient", 0.0f, 0.0f, 0.0f);
            // shader.uploadVec3f("spotLight.diffuse", 1.0f, 1.0f, 1.0f);
            // shader.uploadVec3f("spotLight.specular", 1.0f, 1.0f, 1.0f);
            // shader.uploadFloat("spotLight.constant", 1.0f);
            // shader.uploadFloat("spotLight.linear", 0.09f);
            // shader.uploadFloat("spotLight.quadratic", 0.032f);
            // shader.uploadFloat("spotLight.cutOff", (float)Math.cos(Math.toRadians(12.5f)));
            // shader.uploadFloat("spotLight.outerCutOff", (float)Math.cos(Math.toRadians(15.0f)));
    }

    public void render() {
        for (Cube cube : cubes) {
            cube.render();
        }
    }
}


        