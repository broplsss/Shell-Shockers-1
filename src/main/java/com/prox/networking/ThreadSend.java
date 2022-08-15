package com.prox.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.prox.display.Window;
import com.prox.display.scenes.BasicScene;

public class ThreadSend extends Thread {

	ObjectOutputStream out;
	Window window;
	
	public ThreadSend(Window w, ObjectOutputStream o){
		window = w;
		out = o;
		start();
	}
	
	public void run(){
		while(true){
			try {
				out.writeObject(""+BasicScene.entities.get(0).getPosition().x);
				out.writeObject(""+BasicScene.entities.get(0).getPosition().y);
				out.writeObject(""+BasicScene.entities.get(0).getPosition().z);
				out.flush();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}
}