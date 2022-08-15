package com.prox.renderer.models.primitives;

import com.prox.display.camera.Camera;
import com.prox.entities.BBType;
import com.prox.entities.BoundingBox;
import com.prox.entities.Entity;
import com.prox.renderer.*;
import com.prox.renderer.models.Loader;
import com.prox.renderer.models.OBJLoader;
import com.prox.renderer.models.RawModel;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


import org.joml.*;
import org.joml.Math;

import java.lang.Math.*;

public class Sphere extends Entity {
    
    private RawModel sphere;
    private Texture texture;

    private Matrix4f transform;

    private Vector3f direction;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    private boolean isBoundingBox;

    private int vaoID;

    public Sphere(Shader shader) {
        this("prox/assets/textures/white.jpeg", shader);
    }

    public Sphere(String texture, Shader shader) {
        try {
            this.texture = new Texture(texture);
        } catch (Exception e) {this.texture = new Texture("prox/assets/textures/failedTex.jpeg");}
        this.shader = shader;
        setShader(shader);
        this.transform = new Matrix4f();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.direction = new Vector3f();
        instantiateBB(BBType.SPHERE);
        init();
    }

    public Sphere(String texture, Shader shader, boolean isBoundingBox) {
        try {
            this.texture = new Texture(texture);
        } catch (Exception e) {this.texture = new Texture("prox/assets/textures/failedTex.jpeg");}
        this.shader = shader;
        setShader(shader);
        this.transform = new Matrix4f();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f, 1.0f, 1.0f);
        this.direction = new Vector3f();
        this.isBoundingBox = true;
        init();
    }

    public void init() {
        Loader loader = new Loader();
        this.sphere = OBJLoader.loadObjModel("prox/assets/models/Sphere.obj", loader, null);
        this.vaoID = sphere.getVaoID();
        if (!isBoundingBox) {
            float avg = (scale.x + scale.y + scale.z) / 3.0f;
            this.getBB().setRadius(avg);
        }
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public Vector3f getDirection() {
        direction = new Vector3f(1.0f, 0.0f, 0.0f);
        direction.rotateAxis(rotation.x, 1.0f, 0.0f, 0.0f);
        direction.rotateAxis(rotation.y, 0.0f, 1.0f, 0.0f);
        direction.rotateAxis(rotation.z, 0.0f, 0.0f, 1.0f);
        return direction.normalize();
    }

    // hmmm
    public float getRadius() {
        return this.scale.x;
    }

    public void setRadius(float radius) {
        setScale(radius);
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
        if (!isBoundingBox) {
            this.getBB().setRadius(scale);
        }
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
        if (!isBoundingBox) {
            float avg = (scale.x + scale.y + scale.z) / 3.0f;
            this.getBB().setRadius(avg);
        }
    }

    public Shader getShader() {
        return this.shader;
    }

    @Override
    public void update(float deltaTime, Camera camera) {
        if (!isBoundingBox) {
            this.getBB().update(deltaTime);
        }
    }

    @Override
    public void render() {

        if (!render && !isBoundingBox) {
            return;
        }

        this.transform.identity();
        this.transform.translate(getPosition());
        this.transform.rotate(rotation.x, 1.0f, 0.0f, 0.0f);
        this.transform.rotate(rotation.y, 0.0f, 1.0f, 0.0f);
        this.transform.rotate(rotation.z, 0.0f, 0.0f, 1.0f);
        this.transform.scale(scale);

        rShader.uploadMat4f("model", transform);
        rShader.uploadFloat("material.shininess", 32.0f);
        rShader.uploadTexture("material.diffuse", 0);
        rShader.uploadTexture("material.specular", 1);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        glActiveTexture(GL_TEXTURE1);
        texture.bind();

        glBindVertexArray(this.vaoID);
        GL20.glEnableVertexAttribArray(0); // position
        GL20.glEnableVertexAttribArray(1); // textureCoords
        GL20.glEnableVertexAttribArray(2); // normals

        glDrawElements(GL_TRIANGLES, sphere.getVertexCount(), GL_UNSIGNED_INT, 0);

        texture.unbind();
        rShader.detach();

        if (!isBoundingBox && renderBoundingBox) {
            this.getBB().render();
        }

    }


}
