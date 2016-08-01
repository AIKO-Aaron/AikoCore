package ch.aiko.engine.command;

import java.util.ArrayList;

import ch.aiko.engine.graphics.Screen;

public class CommandHandler {

	public static ArrayList<Command> commands = new ArrayList<Command>();

	public static void registerCommand(Command c) {
		commands.add(c);
	}

	public static void performAction(String command, int perms, Screen sender) {
		String com = command.split(" ")[0];
		String arg = command.substring(com.length() + (com.length() < command.length() ? 1 : 0));
		String[] args;
		
		if (arg.replace(" ", "").trim().equalsIgnoreCase("")) args = new String[0];
		else args = arg.split(" ");
		
		for (Command c : commands) {
			if (c.getName().equalsIgnoreCase(command) && perms >= c.requiredPermissions()) {
				boolean b = c.onExecute(args, sender);
				if (!b) sender.ps.println(c.usage());
			} else if (c.getName().equalsIgnoreCase(command)) {
				sender.ps.println("You don't have the required Permissions to use this command");
			}
		}
	}
}
