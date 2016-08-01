package ch.aiko.engine.command;

import ch.aiko.engine.graphics.Screen;

public class StopCommand extends Command {

	public String getName() {
		return "Stop";
	}

	public boolean onExecute(String[] args, Screen sender) {
		if(args.length != 0) return false;
		sender.stopThreads();
		System.exit(0);
		return true;
	}

	public int requiredPermissions() {
		return 4;
	}

	public String usage() {
		return "stop";
	}
}
