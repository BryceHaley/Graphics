package client;

	// You may add files to the windowing package, but you must leave all files
	// that are already present unchanged, except for:
	// 		Main.java (this file)
	//		drawable/Drawables.java

	// Also, do not instantiate Image361 yourself.

import javafx.stage.*;
import windowing.Window361;
import windowing.drawable.Drawable;

import java.util.List;
import java.util.Map;

import javafx.application.Application;

public class Main extends Application {
public static boolean hasArgument = false;
public static String arg;
	public static void main(String[] args) {
        launch(args);
	}
	@Override
	public void start(Stage primaryStage) {
		Parameters p = getParameters();

		
	    Map<String, String> namedParams = p.getNamed();
	    List<String> unnamedParams = p.getUnnamed();
	    List<String> rawParams = p.getRaw();
	    
	    String paramStr = "Named Parameters: " + namedParams + "\n" +
	      "Unnamed Parameters: " + unnamedParams + "\n" +
	      "Raw Parameters: " + rawParams;		
		
	    System.out.println("paramater " + paramStr);
	    
	    if(rawParams.size() != 0) {
	    	System.out.println("extracted raw " + rawParams.get(0));
			hasArgument = true;
			arg = rawParams.get(0);
			
			Window361 window = new Window361(primaryStage);
			Drawable drawable = window.getDrawable();
			System.out.println(arg);
			Client client = new Client(drawable);
			window.setPageTurner(client);
			client.nextPage(arg);
			
			primaryStage.show();
	    }
	    
	   
		Window361 window = new Window361(primaryStage);
		Drawable drawable = window.getDrawable();
		
		Client client = new Client(drawable);
		window.setPageTurner(client);
		client.nextPage();
		
		
		
		primaryStage.show();
	}

}
