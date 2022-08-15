package com.prox.entities;

import com.prox.display.scenes.BasicScene;

import com.prox.renderer.*;

import com.prox.renderer.models.primitives.*;

import org.joml.*;

import static org.lwjgl.opengl.GL11.*;




public class BoundingBox {

    private Entity entity;
    
    private Vector3f position;
    
    private Shader shader = BasicScene.baseShader;

    private BBType bbType;

    // Rectangle
    private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);

    public Vector3f min = new Vector3f();
    public Vector3f max  = new Vector3f();

    private Cube rBox;

    // Sphere
    private float radius;
    private Sphere rSphere;

    public BoundingBox() {

    }
    
    public BoundingBox(Entity entity, BBType type) {
        this.entity = entity;
        this.position = entity.getPosition();

        if (type == BBType.RECTANGLE) {
            setDimensions(5.0f, 5.0f, 5.0f);
        }
        if (type == BBType.SPHERE) {
            setRadius(5.0f);
        }
    }

    public void setDimensions(float width, float height, float depth) {
        bbType = BBType.RECTANGLE;
        this.scale.x = width;
        this.scale.y = height;
        this.scale.z = depth;
        rBox = new Cube("prox/assets/textures/white.jpeg", shader, true);
        rBox.setPosition(this.position);
        rBox.setScale(this.scale);
        calculateMin();
        calculateMax();
    }

    public void setRadius(float radius) {
        bbType = BBType.SPHERE;
        this.radius = radius / 2;
        rSphere = new Sphere("prox/assets/textures/white.jpeg", shader, true);
        rSphere.setRadius(radius);
    }

    public BBType bbType() {
        return this.bbType;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getWidth() {
        return this.scale.x;
    }

    public float getHeight() {
        return this.scale.y;
    }

    public float getDepth() {
        return this.scale.z;
    }

    public float getRadius() {
        return this.radius;
    }

    public void calculateMin() {
        this.min.x = position.x - getWidth() / 2;
        this.min.y = position.y - getHeight() / 2;
        this.min.z = position.z - getDepth() / 2;
    }

    public void calculateMax() {
        this.max.x = position.x + getWidth()  / 2;
        this.max.y = position.y + getHeight() / 2;
        this.max.z = position.z + getDepth()  / 2;
    }

    public void update(float deltaTime) {
        if (this.entity.hasBoundingBox == false) {
            return;
        }
        this.position = entity.getPosition();
        if (bbType == BBType.RECTANGLE) {
            rBox.setPosition(this.position);
            calculateMin();
            calculateMax();
        }
        if (bbType == BBType.SPHERE) {
            rSphere.setPosition(this.position);
        }
        // rBox.setRotation(entity.getRotation()); // Axis-aligned necessary for AABB
    }

    public void render() {
        if (this.entity.hasBoundingBox == false) {
            return;
        }
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        if (bbType == BBType.RECTANGLE) {
            rBox.render();
        }
        if (bbType == BBType.SPHERE) {
            rSphere.render();
        }
        if (BasicScene.wireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);}
    }
}
