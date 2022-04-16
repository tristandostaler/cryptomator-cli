/*******************************************************************************
 * Copyright (c) 2016 Sebastian Stenzel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE.txt.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.cryptomator.cli.pwd.PasswordFromFileStrategy;
import org.cryptomator.cli.pwd.PasswordFromPropertyStrategy;
import org.cryptomator.cli.pwd.PasswordFromStdInputStrategy;
import org.cryptomator.cli.pwd.PasswordStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class Args {

	private static final String USAGE = "java -jar cryptomator-cli.jar" //
			+ " --bind localhost --port 8080" //
			+ " --vault mySecretVault=/path/to/vault --password mySecretVault=FooBar3000" //
			+ " --vault myOtherVault=/path/to/other/vault --password myOtherVault=BarFoo4000" //
			+ " --vault myThirdVault=/path/to/third/vault --passwordfile myThirdVault=/path/to/passwordfile";
	private static final Options OPTIONS = new Options();

	static {
		OPTIONS.addOption(Option.builder() //
				.longOpt("bind") //
				.argName("WebDAV bind address") //
				.desc("TCP socket bind address of the WebDAV server. Use 0.0.0.0 to accept all incoming connections.") //
				.hasArg() //
				.build());
	}

	private final String bindAddr;
	private final int port;
	private final boolean hasValidWebDavConfig;
	private final Properties vaultPaths;
	private final Properties vaultPasswords;
	private final Properties vaultPasswordFiles;
	private final Map<String, PasswordStrategy> passwordStrategies;
	private final Properties fuseMountPoints;

	public Args(CommandLine commandLine) {
		if (commandLine.hasOption("bind") && commandLine.hasOption("port")) {
			hasValidWebDavConfig = true;
			this.bindAddr = commandLine.getOptionValue("bind", "localhost");
			this.port = Integer.parseInt(commandLine.getOptionValue("port", "0"));
		} else {
			hasValidWebDavConfig = false;
			this.bindAddr = "";
			this.port = -1;
		}
		this.vaultPaths = commandLine.getOptionProperties("vault");
		this.vaultPasswords = commandLine.getOptionProperties("password");
		this.vaultPasswordFiles = commandLine.getOptionProperties("passwordfile");
		this.passwordStrategies = new HashMap<>();
		this.fuseMountPoints = commandLine.getOptionProperties("fusemount");
	}

	public boolean hasValidWebDavConf() {
		return hasValidWebDavConfig;
	}

	public String getBindAddr() {
		return bindAddr;
	}

	public int getPort() {
		return port;
	}

	public Set<String> getVaultNames() {
		return vaultPaths.keySet().stream().map(String.class::cast).collect(Collectors.toSet());
	}

	public String getVaultPath(String vaultName) {
		return vaultPaths.getProperty(vaultName);
	}

	public static void printUsage() {
		new HelpFormatter().printHelp(USAGE, OPTIONS);
	}

	public PasswordStrategy addPasswortStrategy(final String vaultName) {
		PasswordStrategy passwordStrategy = new PasswordFromStdInputStrategy(vaultName);

		if (vaultPasswords.getProperty(vaultName) != null) {
			passwordStrategy = new PasswordFromPropertyStrategy(vaultName, vaultPasswords.getProperty(vaultName));
		} else if (vaultPasswordFiles.getProperty(vaultName) != null) {
			passwordStrategy = new PasswordFromFileStrategy(vaultName, Paths.get(vaultPasswordFiles.getProperty(vaultName)));
		}

		this.passwordStrategies.put(vaultName, passwordStrategy);
		return passwordStrategy;
	}

	public PasswordStrategy getPasswordStrategy(final String vaultName) {
		return passwordStrategies.get(vaultName);
	}

	public Path getFuseMountPoint(String vaultName) {
		String mountPoint = fuseMountPoints.getProperty(vaultName);
		if (mountPoint == null) {
			return null;
		}
		Path mountPointPath = Paths.get(mountPoint);
		return mountPointPath;
	}
}
