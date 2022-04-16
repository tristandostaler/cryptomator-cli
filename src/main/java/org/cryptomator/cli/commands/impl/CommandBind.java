package org.cryptomator.cli.commands.impl;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.commands.ArgsInteractiveCommand;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.frontend.WebDav;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class CommandBind implements ArgsInteractiveCommand, ConsoleCommand {

	private static final String NAME = "bind";
	private static final Options OPTIONS = new Options();

	private final AtomicReference<WebDav> webDav;

	static {
		OPTIONS.addOption(Option.builder() //
				.longOpt("address") //
				.argName("WebDAV bind address") //
				.desc("TCP socket bind address of the WebDAV server. Use 0.0.0.0 to accept all incoming connections.") //
				.hasArg() //
				.optionalArg(false) //
				.build());
		OPTIONS.addOption(Option.builder() //
				.longOpt("port") //
				.argName("WebDAV port") //
				.desc("TCP port, the WebDAV server should listen on.") //
				.hasArg() //
				.optionalArg(false) //
				.build());
	}

	@Inject
	public CommandBind(AtomicReference<WebDav> webDav) {
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
	public Options interactiveOptions() {
		return OPTIONS;
	}

	@Override
	public Options consoleOptions() {
		return OPTIONS;
	}

	@Override
	public void interactiveExecute(CommandLine cmdLine) {
		execute(cmdLine);
	}

	@Override
	public void interactiveParsingFailed(ParseException parseException, String cmdLine) {
		System.out.println("Parsing failed!");
	}

	@Override
	public void consoleExecute(CommandLine consoleCmdLine) {
		execute(consoleCmdLine);
	}

	private void execute(CommandLine cmdLine) {
		if (this.webDav.get() != null) {
			System.out.println("Already bound!");
			return;
		}
		int port;
		try {
			port = Integer.parseInt(cmdLine.getOptionValue("port"));
		} catch (NumberFormatException e) {
			System.out.println("Invalid format!");
			return;
		}
		this.webDav.set(new WebDav(cmdLine.getOptionValue("address"), port)); //TODO Synchronization
	}
}