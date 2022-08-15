package com.prox.renderer.models.primitives;

import com.prox.display.camera.Camera;
import com.prox.entities.BBType;
import com.prox.entities.BoundingBox;
import com.prox.entities.Entity;
import com.prox.renderer.Shader;
import com.prox.renderer.Texture;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.*;

public class Cube extends Entity {

    private Texture texture;
    private Shader shader;

    private Matrix4f transform;

    private Vector3f direction;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    private boolean isBoundingBox;

    private int eboID, vboID, vaoID;
    
    private float vertexArray[] = {
            // positions         // normals           // texture coords
            // Back face
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // Bottom-left
             0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f, // top-right
             0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, // bottom-right         
             0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f, // top-right
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // bottom-left
            -0.5f,  0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, // top-left
            // Front face
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 0.0f, // bottom-left
             0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 0.0f, // bottom-right
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 1.0f, // top-right
             0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f, 1.0f, // top-right
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 1.0f, // top-left
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f, 0.0f, // bottom-left
            // Left face
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f, // top-right
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f, // top-left
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f, // bottom-left
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f, // bottom-left
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f, // bottom-right
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f, // top-right
            // Right face
             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f, // top-left
             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f, // bottom-right
             0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f, // top-right         
             0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f, // bottom-right
             0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f, // top-left
             0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f, // bottom-left     
            // Bottom face
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f, // top-right
             0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f, // top-left
             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f, // bottom-left
             0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f, // bottom-left
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f, // bottom-right
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f, // top-right
            // Top face
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f, // top-left
             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f, // bottom-right
             0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f, // top-right     
             0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f, // bottom-right
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f, // top-left
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f  // bottom-left        
    };

    public Cube(Shader shader) {
        this("prox/assets/textures/white.jpeg", shader);
    }

    public Cube(String texture, Shader shader) {
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
        instantiateBB(BBType.RECTANGLE);
        getBB().setDimensions(this.getWidth(), this.getHeight(), this.getDepth());
        init();
    }

    public Cube(String texture, Shader shader, boolean isBoundingBox) {
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

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        int positionSize = 3; // XYZ
        int colorSize = 0; // RGBA
        int normalVec = 3; // XYZ
        int texSize = 2; // ST / UV
        int vertexSizeBytes = (positionSize + colorSize + normalVec + texSize) * Float.BYTES; // Stride

        // Position attribute
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0); // index (from (layout = n)), size, type, normalized?, stride, offset
        glEnableVertexAttribArray(0); // index to enable ^
                
        // // Color attribute
        // glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES); // offset = previous stride in Bytes
        // glEnableVertexAttribArray(1);
        
        // Normal attribute
        glVertexAttribPointer(2, normalVec, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES); // offset = previous stride in Bytes
        glEnableVertexAttribArray(2);
        
        // Texture attribute
        glVertexAttribPointer(1, texSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize + normalVec) * Float.BYTES); // offset = previous stride in Bytes
        glEnableVertexAttribArray(1);


    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
        if (!isBoundingBox) {
            this.getBB().setDimensions(scale, scale, scale);
        }
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
        if (!isBoundingBox) {
            this.getBB().setDimensions(scale.x, scale.y, scale.z);
        }
    }

    public Shader getShader() {
        return this.shader;
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

    public Vector3f getPosition() {
        return this.position;
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

        transform.identity();
        transform.translate(this.position);
        transform.rotate(rotation.x, 1.0f, 0.0f, 0.0f);
        transform.rotate(rotation.y, 0.0f, 1.0f, 0.0f);
        transform.rotate(rotation.z, 0.0f, 0.0f, 1.0f);
        transform.scale(this.scale);

        rShader.uploadMat4f("model", transform);
        rShader.uploadFloat("material.shininess", 32.0f);
        rShader.uploadTexture("material.diffuse", 0);
        rShader.uploadTexture("material.specular", 1);
        
        glBindVertexArray(vaoID);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        glActiveTexture(GL_TEXTURE1);
        texture.bind();
        
        glDrawArrays(GL_TRIANGLES, 0, vertexArray.length);

        texture.unbind();
        rShader.detach();


        if (!isBoundingBox && renderBoundingBox) {
            this.getBB().render();
        }

    }
}
