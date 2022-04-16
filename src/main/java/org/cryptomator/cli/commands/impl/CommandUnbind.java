package org.cryptomator.cli.commands.impl;

import org.apache.commons.cli.CommandLine;
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
	public void interactiveExecute() {
		execute();
	}

	@Override
	public void consoleExecute(CommandLine consoleCmdLine) {
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