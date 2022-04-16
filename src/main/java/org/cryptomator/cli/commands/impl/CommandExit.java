package org.cryptomator.cli.commands.impl;

import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

public class CommandExit implements NoArgsInteractiveCommand {

	@Override
	public String interactiveCommandName() {
		return "exit";
	}

	@Override
	public void interactiveExecute() {
		System.out.println("Exit without args");
		//CommandHandler.getInstance().stop();
		//TODO
		System.exit(0);
	}
}
