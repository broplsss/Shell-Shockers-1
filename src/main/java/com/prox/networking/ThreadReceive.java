package com.prox.networking;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.prox.display.Window;
import com.prox.display.scenes.BasicScene;

public class ThreadReceive extends Thread {

	ObjectInputStream in;
	Window window;
	
	public ThreadReceive(Window w, ObjectInputStream i){
		window = w;
		in = i;
		start();
	}
	
	public void run(){
		while(true){
			try {
				BasicScene.entities.get(4).getPosition().x = Float.parseFloat((String)in.readObject());
				BasicScene.entities.get(4).getPosition().y = Float.parseFloat((String)in.readObject());
				BasicScene.entities.get(4).getPosition().z = Float.parseFloat((String)in.readObject());
			} catch (Exception e) {
				// e.printStackTrace();
			// } catch (NumberFormatException e) {
			// 	e.printStackTrace();
			// } catch (ClassNotFoundException e) {
			// 	e.printStackTrace();
			}
		}
	}
}

