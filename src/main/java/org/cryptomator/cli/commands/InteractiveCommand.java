package org.cryptomator.cli.commands;

public sealed interface InteractiveCommand extends Command permits ArgsInteractiveCommand, NoArgsInteractiveCommand {

	String interactiveCommandName();

}