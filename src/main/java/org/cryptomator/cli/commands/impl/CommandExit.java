package org.cryptomator.cli.commands.impl;

import de.skymatic.autobindings.BindIntoSet;
import org.cryptomator.cli.CallContext;
import org.cryptomator.cli.commands.Command;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandExit implements NoArgsInteractiveCommand {

	@Inject
	@BindIntoSet(module = "org.cryptomator.cli.commands.CommandModule", bindTo = Command.class)
	public CommandExit() {

	}

	@Override
	public String interactiveCommandName() {
		return "exit";
	}

	@Override
	public void interactiveExecute(CallContext context) {
		System.out.println("Exit without args");
		//CommandHandler.getInstance().stop();
		//TODO
		System.exit(0);
	}
}
