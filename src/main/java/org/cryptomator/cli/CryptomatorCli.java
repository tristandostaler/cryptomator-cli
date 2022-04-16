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

public class CryptomatorCli {

	private final static ApplicationComponent APPLICATION_COMPONENT = DaggerApplicationComponent.create();

	public static void main(String[] args) {
		var commandHandler = APPLICATION_COMPONENT.commandHandler();
		try {
			commandHandler.processArgs(args);
		} catch (ParseException e) {
			throw new RuntimeException(e); //TODO
		}

	}
}