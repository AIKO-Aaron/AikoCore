package ch.aiko.engine.command;

import ch.aiko.engine.graphics.Screen;

public abstract class Command {

	public void register() {
		CommandHandler.registerCommand(this);
	}
	
	public abstract String getName();
	public abstract boolean onExecute(String[] args, Screen sender);
	public abstract int requiredPermissions();
	public abstract String usage();
	
}
