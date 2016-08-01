package ch.aiko.engine.command;

import ch.aiko.engine.graphics.Screen;

public interface ExecuteMethod {

	public boolean onExecute(String[] args, Screen sender);
	
}
