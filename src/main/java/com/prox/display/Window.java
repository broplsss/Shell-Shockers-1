package com.prox.display;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.prox.util.*;
import com.prox.display.scenes.*;

public class Window {
    
    private int width, height;
    private String title;
    private long glfwWindow;

    private static Window window = null;

    private static Scene currentScene;

    private static MouseListener mouse;
    private static KeyListener key;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Test";
    }

    // Singleton
    public static Window get() {

        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public long getHandle() {
        return this.glfwWindow;
    }
    
    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new BasicScene();
                currentScene.init(get().getHandle()); 
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init(get().getHandle()); 
                break;
            default:
                assert false : "Unknown Scene '" + newScene + "'";
        }
    }

    public void run() {

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void init() {

        // Setup an error callboack
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW 
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // * Must have this line for macOS *

        // Create the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Assigns the GLFW callbacks to the respective methods we created in our MouseListener and KeyListener classes
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        /* 
            This line is critical for LWJGL's interoperation with GLFW's
            OpenGL context, or any context that is managed externally.
            LWJGL detects the context that is current in the current thread,
            creates the GLCapabilities instance and makes the OpenGL
            bindings available for use. All other GL functions have to be after this.
        */
        GL.createCapabilities();

        //Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
		glfwShowWindow(glfwWindow);

        // Set the rendering dimensions within the physical window 
        glViewport(0, 0, this.width, this.height);

        // Adjust the viewport when the window size is adjusted so we don't render to unseen pixels
        GLFW.glfwSetFramebufferSizeCallback(Window.get().getHandle(), (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        // Create the MouseListener and KeyListener singletons
        mouse = MouseListener.get();
        key = KeyListener.get();

        // Set the current scene
        Window.changeScene(0);

    }  
 
    public void loop() {
   
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Check and call events
            glfwPollEvents();
            // Set the color to clear the screen with (RGBA)
            glClearColor(0.19f, 0.6f, 0.8f, 1.0f);
            // Clear the screen's current color buffer (and GL_DEPTH_BUFFER_BIT / GL_STENCIL_BUFFER_BIT)
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }

            // Double buffer (front shown, back rendering)
            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;

        }

    }




    

}
