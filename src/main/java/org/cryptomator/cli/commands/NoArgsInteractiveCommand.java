package org.cryptomator.cli.commands;

import org.cryptomator.cli.CallContext;

public non-sealed interface NoArgsInteractiveCommand extends InteractiveCommand {

	void interactiveExecute(CallContext context);

}