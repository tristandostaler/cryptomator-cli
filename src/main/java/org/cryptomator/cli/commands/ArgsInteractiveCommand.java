package org.cryptomator.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.CallContext;

public non-sealed interface ArgsInteractiveCommand extends InteractiveCommand {

	Options interactiveOptions();

	String interactiveUsage();

	void interactiveExecute(CallContext context, CommandLine cmdLine);

	void interactiveParsingFailed(CallContext context, ParseException parseException); //TODO Default?

}