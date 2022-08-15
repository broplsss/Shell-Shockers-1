package com.prox.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import static org.lwjgl.opengl.GL30.glGenerateMipmap;


public class Texture {
    private String filepath;
    private int texID;

    public static boolean transparent;

    public Texture(String filepath) {
        this.filepath = filepath;
        
        // Generate the texture on GPU and bind it
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters
            // Repeat the image in both directions (S, T) or {GL_MIRRORED_REPEAT, GL_CLAMP_TO_EDGE, and GL_CLAMP_TO_BORDER}
            if (transparent) {
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);	
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);  
            } else {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); 
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            }

            // Texture filtering - when stretching the image, use the nearest values (pixelate) or GL_LINEAR (blur)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR); // scaling down (Mipmap edges are filtered linearly)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // scaling up (magnifications do not use Mipmaps)
    
        // We must make IntBuffer values of the width, height, and channels to pass in as memory objects to the stbi_load() method
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        // Generate the texture 
        if (image != null) {
            if (channels.get(0) == 3) { // RGB image (.JPEG)
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), // target, mipmap level (if we want to do it manually), color format, width, height, border (always 0), format, datatype, image data
                             0, GL_RGB, GL_UNSIGNED_BYTE, image);
                glGenerateMipmap(GL_TEXTURE_2D); // Automatically generate the mipmaps!
            } else if (channels.get(0) == 4) { // RGBA image (.PNG)
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                             0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                glGenerateMipmap(GL_TEXTURE_2D); // Automatically generate the mipmaps!
            } else {
                assert false : "Error: (Texture) Unkown number of channels'" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }

        // Free up memory (no more leaks)
        stbi_image_free(image);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void setTransparency(boolean transparent) {
        Texture.transparent = transparent; 
    }
}
