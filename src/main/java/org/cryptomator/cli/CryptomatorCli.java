/*******************************************************************************
 * Copyright (c) 2016 Sebastian Stenzel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE.txt.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.cli;

import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.commands.impl.CommandBind;
import org.cryptomator.cli.commands.impl.CommandExit;
import org.cryptomator.cli.commands.impl.CommandHelp;
import org.cryptomator.cli.commands.impl.CommandMount;
import org.cryptomator.cli.commands.impl.CommandUnbind;
import org.cryptomator.cli.commands.impl.CommandVersion;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CryptomatorCli {

	private final static SecureRandom secureRandom;

	static {
		try {
			secureRandom = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("A strong algorithm must exist in every Java platform.", e);
		}
	}

	public static void main(String[] args) {
		var commandHandler = new CommandHandler();
		var bind = new CommandBind();
		commandHandler.registerCommands(new CommandMount(), new CommandExit(), new CommandVersion(), bind, new CommandUnbind(bind), new CommandHelp(commandHandler));
		try {
			commandHandler.processArgs(args);
		} catch (ParseException e) {
			throw new RuntimeException(e); //TODO
		}
	}

	public static SecureRandom getSecureRandom() {
		return secureRandom;
	}
}