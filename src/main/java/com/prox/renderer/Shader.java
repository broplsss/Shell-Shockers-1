package com.prox.renderer;

import java.io.IOException;
import java.nio.file.*;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;


public class Shader {
    
    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {

        // Parse the shader file
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-z])+"); // regex expression

            // Find the first pattern afer #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int endOfLine = source.indexOf("\n", index);
            String firstPattern = source.substring(index, endOfLine).trim();

            // Find the second pattern afer #type 'pattern'
            index = source.indexOf("#type", endOfLine) + 6;
            endOfLine = source.indexOf("\n", index);
            String secondPattern = source.substring(index, endOfLine).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }

        // DEBUG
            // System.out.println(vertexSource);
            // System.out.println(fragmentSource);
    }

    public void compile() {

        // =============================================================
        //                  Compile and Link the Shaders
        // =============================================================

        int vertexID, fragmentID;

        // Create a new shader object of the specified type (Compute, Vertex, Geometry, Fragment...)
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Attach the shader source code to the shader object and compile to the GPU. Takes the form (shaderObj, shaderSource)
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        
        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Second, load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        
        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Create a shader program, attach the two shaders and link the vertex and fragment shaders together to use it for rendering objects.
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Delete the individual shaders after we link them to free up memory
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
 
         // Check for linking errors
         success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
         if (success == GL_FALSE) {
             int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
             System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
             System.out.println(glGetProgramInfoLog(shaderProgramID, len));
             assert false : "";
         }
    }

    // every following render call will use this program as well as its attached shaders    
    public void use() { 
        if (!beingUsed) {
            // Bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }        
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName); // returns -1 if it could not find the uniform variable
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer); // [1, 1, 1, 1, 1, ...] (flattening matrix into 1-D array of length 16)
        glUniformMatrix4fv(varLocation, false, matBuffer); // location, transpose?, values - we must actively be using the shader program.
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer); 
        glUniformMatrix3fv(varLocation, false, matBuffer);
    } 

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    public void uploadVec4f(String varName, float x, float y, float z, float w) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, x, y, z, w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec3f(String varName, float x, float y, float z) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, x, y, z);
    }


    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
