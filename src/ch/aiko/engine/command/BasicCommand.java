package ch.aiko.engine.command;

import ch.aiko.engine.graphics.Screen;

public class BasicCommand extends Command {

	protected String name, usage;
	protected int reqPerm;
	protected ExecuteMethod method;
	
	public BasicCommand(String name, String usage, int reqPerm, ExecuteMethod method) {
		this.name = name;
		this.usage = usage;
		this.reqPerm = reqPerm;
		this.method = method;
		register();
	}
	
	public String getName() {
		return name;
	}

	public boolean onExecute(String[] args, Screen sender) {return method.onExecute(args, sender);}

	public int requiredPermissions() {
		return reqPerm;
	}

	public String usage() {
		return usage;
	}

}
