package com.prox.entities;

import com.prox.display.camera.Camera;
import com.prox.renderer.Shader;
import com.prox.renderer.Texture;
import com.prox.renderer.models.Loader;
import com.prox.renderer.models.OBJLoader;
import com.prox.renderer.models.RawModel;


import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.HashMap;

import org.joml.*;

public abstract class Entity {
    
    private RawModel mesh;
    private Texture texture;

    protected boolean render = true;

    public boolean isTerrain;
    public HashMap<Vector2i, Float> heightMap;

    protected Shader shader;
    protected Shader rShader;

    private Camera camera;

    private Matrix4f transform;

    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    protected Vector3f direction;

    protected BoundingBox bBox;
    protected boolean hasBoundingBox = true;
    public static boolean renderBoundingBox = false;

    private boolean outline;
    private Shader outlineShader;
    private float outlineScale = 1.1f;

    private int tag = 1; // 0 = Player / Null; 1 = Rigid Object; 2 = Movable Object; 3 = Non-Rigid Object; 4 = Camera-Bound; 5 = Player-Bound;
    private int shape; // 0 = Non-Primitive; 1 = Cube; 2 = Sphere; 3 = Square

    Loader loader = new Loader();

    // For Primitives
    public Entity() {

    }

    public Entity(String mesh, String texture, Shader shader, BBType bb) {
        this.isTerrain = false;
        this.mesh = OBJLoader.loadObjModel(mesh, loader, null);
        try {
            this.texture = new Texture(texture);
        } catch (Exception e) {this.texture = new Texture("prox/assets/textures/failedTex.jpeg");}
        this.shader = shader;
        setShader(shader);
        this.transform = new Matrix4f();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.bBox = new BoundingBox(this, bb);
    }

    // Terrain loading only
    public Entity(String mesh, String texture, Shader shader, boolean isTerrain) {
        this.heightMap = new HashMap<Vector2i, Float>();
        this.isTerrain = true;
        this.enableBB(false);
        this.mesh = OBJLoader.loadObjModel(mesh, loader, heightMap);
        try {
            this.texture = new Texture(texture);
        } catch (Exception e) {this.texture = new Texture("prox/assets/textures/failedTex.jpeg");}
        this.shader = shader;
        setShader(shader);
        this.transform = new Matrix4f();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
    }

    public Entity(String mesh, String texture, Shader shader, BBType bb, Vector3f pos) {
        this(mesh, texture, shader, bb);
        setPosition(pos);
    }

    public Entity(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, float scale) {
        this(mesh, texture, shader, bb);
        setPosition(pos);
        setScale(new Vector3f(scale, scale, scale));
    }

    public Entity(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, Vector3f scale) {
        this(mesh, texture, shader, bb);
        setPosition(pos);
        setScale(scale);
    }

    public Entity(String mesh, String texture, Shader shader, BBType bb, Vector3f pos, Vector3f rot, float scale) {
        this(mesh, texture, shader, bb);
        setPosition(pos);
        setRotation(rot);
        setScale(new Vector3f(scale, scale, scale));
    }

    public Entity get() {
        return this;
    }

    public void instantiateBB(BBType bb) {
        this.bBox = new BoundingBox(this, bb);
    }

    public BoundingBox getBB() {
        return this.bBox;
    }

    public void enableBB(boolean enable) {
        this.hasBoundingBox = enable;
    } 
    
    public boolean hasBB() {
        return this.hasBoundingBox;
    }

    public void renderBB(boolean render) {
        renderBoundingBox = render;
    }

    public Matrix4f getTransform() {
        return this.transform;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getDirection() {
        direction = new Vector3f(1.0f, 0.0f, 0.0f);
        direction.rotateAxis(rotation.x, 1.0f, 0.0f, 0.0f);
        direction.rotateAxis(rotation.y, 0.0f, 1.0f, 0.0f);
        direction.rotateAxis(rotation.z, 0.0f, 0.0f, 1.0f);
        return direction.normalize();
    }

    // Fix??
    public void setDirection(Vector3f dir) {

    }

    public Vector3f getScale() {
        return this.scale;
    }

    public void setRotation(Vector3f vec) {
        this.rotation = new Vector3f(vec);
    }

    public void setPosition(Vector3f vec) {
        this.position = new Vector3f(vec);
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public void setScale(Vector3f vec) {
        this.scale = new Vector3f(vec);
    }

    public void setScale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
    }

    public void enableOutline() {
        this.outline = true;
    }

    public void disableOutline() {
        this.outline = false;
        this.outlineShader.detach();
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        this.rShader = shader;
    }

    public void setOutlineShader(Shader outlineShader) {
        this.outlineShader = outlineShader;
    }

    public void setOutlineScale(float scale) {
        this.outlineScale = scale;
    }

    public boolean hasOutline() {
        return this.outline;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
    }

    public void enableRender(boolean render) {
        this.render = render;
    }

    public void outline(Matrix4f view, Matrix4f projection) {
        if (outline) {   
            outlineShader.uploadMat4f("projection", projection);
            outlineShader.uploadMat4f("view", view);
            outlineShader.uploadMat4f("outline", new Matrix4f().scale(outlineScale));
            this.rShader = outlineShader;
            this.render();
            this.rShader = this.shader;
        }
    }

    public abstract void update(float deltaTime, Camera camera);

    public void render() {

        if (!render) {
            return;
        }

        getTransform().identity();
        getTransform().translate(getPosition());
        getTransform().rotate(rotation.x, 1.0f, 0.0f, 0.0f);
        getTransform().rotate(rotation.y, 0.0f, 1.0f, 0.0f);
        getTransform().rotate(rotation.z, 0.0f, 0.0f, 1.0f);
        getTransform().scale(scale);

        rShader.uploadMat4f("model", transform);
        rShader.uploadFloat("material.shininess", 32.0f);
        rShader.uploadTexture("material.diffuse", 0);
        rShader.uploadTexture("material.specular", 1);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        glActiveTexture(GL_TEXTURE1);
        texture.bind();


        glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0); // position
        GL20.glEnableVertexAttribArray(1); // textureCoords
        GL20.glEnableVertexAttribArray(2); // normals

        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        texture.unbind();
        rShader.detach();

        if (renderBoundingBox && hasBB()) {
            getBB().render();
        }
    }
}
