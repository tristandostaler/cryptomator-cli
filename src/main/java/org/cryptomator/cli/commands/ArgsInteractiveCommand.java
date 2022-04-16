package org.cryptomator.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public non-sealed interface ArgsInteractiveCommand extends InteractiveCommand {

	Options interactiveOptions();

	void interactiveExecute(CommandLine cmdLine);

	void interactiveParsingFailed(ParseException parseException, String cmdLine); //TODO Default?

}