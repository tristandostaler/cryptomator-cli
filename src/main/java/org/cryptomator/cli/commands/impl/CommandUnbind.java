package org.cryptomator.cli.commands.impl;

import de.skymatic.autobindings.BindIntoSet;
import org.apache.commons.cli.CommandLine;
import org.cryptomator.cli.CallContext;
import org.cryptomator.cli.commands.Command;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.commands.NoArgsInteractiveCommand;
import org.cryptomator.cli.frontend.WebDav;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class CommandUnbind implements NoArgsInteractiveCommand, ConsoleCommand {

	private static final String NAME = "unbind";

	private final AtomicReference<WebDav> webDav;

	@Inject
	@BindIntoSet(module = "org.cryptomator.cli.commands.CommandModule", bindTo = Command.class)
	public CommandUnbind(AtomicReference<WebDav> webDav) {
		this.webDav = webDav;
	}

	@Override
	public String interactiveCommandName() {
		return NAME;
	}

	@Override
	public String consoleCommandName() {
		return NAME;
	}

	@Override
	public void interactiveExecute(CallContext context) {
		execute();
	}

	@Override
	public void consoleExecute(CallContext context, CommandLine consoleCmdLine) {
		execute();
	}

	private void execute() {
		var webDav = this.webDav.get();
		if (webDav == null) {
			System.out.println("None bound!");
			return;
		}
		webDav.stop();
		this.webDav.set(null); //TODO Synchronization
	}
}