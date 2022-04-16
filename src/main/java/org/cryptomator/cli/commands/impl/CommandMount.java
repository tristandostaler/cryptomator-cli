package org.cryptomator.cli.commands.impl;

import com.google.common.base.Preconditions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cryptomator.cli.Args;
import org.cryptomator.cli.CryptomatorCli;
import org.cryptomator.cli.commands.ArgsInteractiveCommand;
import org.cryptomator.cli.commands.ConsoleCommand;
import org.cryptomator.cli.frontend.FuseMount;
import org.cryptomator.cli.frontend.WebDav;
import org.cryptomator.cryptofs.CryptoFileSystemProperties;
import org.cryptomator.cryptofs.CryptoFileSystemProvider;
import org.cryptomator.cryptolib.common.MasterkeyFileAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.cryptomator.cli.Constants.PEPPER;
import static org.cryptomator.cli.Constants.SCHEME;

public class CommandMount implements ArgsInteractiveCommand, ConsoleCommand {

	private final static Logger LOG = LoggerFactory.getLogger(CommandMount.class);

	private final static String NAME = "mount";
	private final static Options OPTIONS = new Options();

	static {
		OPTIONS.addOption(Option.builder() //
				.longOpt("vault") //
				.argName("Path of a vault") //
				.desc("Format must be vaultName=/path/to/vault") //
				.valueSeparator() //
				.hasArgs() //
				.build());
		OPTIONS.addOption(Option.builder() //
				.longOpt("password") //
				.argName("Password of a vault") //
				.desc("Format must be vaultName=password") //
				.valueSeparator() //
				.hasArgs() //
				.build());
		OPTIONS.addOption(Option.builder() //
				.longOpt("passwordfile") //
				.argName("Passwordfile for a vault") //
				.desc("Format must be vaultName=passwordfile") //
				.valueSeparator() //
				.hasArgs() //
				.build());
		OPTIONS.addOption(Option.builder() //
				.longOpt("fusemount") //
				.argName("mount point") //
				.desc("Format must be vaultName=mountpoint") //
				.valueSeparator() //
				.hasArgs() //
				.build());
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
		System.out.println("Mount > FAILED");
		parseException.printStackTrace();
	}

	@Override
	public void consoleExecute(CommandLine cmdLine) {
		execute(cmdLine);
	}

	private void execute(CommandLine cmdLine) {
		try {
			var args = new Args(cmdLine);
			validate(args);
			startup(args);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
			Args.printUsage();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void validate(Args args) throws IllegalArgumentException {
		Set<String> vaultNames = args.getVaultNames();
		if (args.hasValidWebDavConf() && (args.getPort() < 0 || args.getPort() > 65536)) {
			throw new IllegalArgumentException("Invalid WebDAV Port.");
		}

		if (vaultNames.size() == 0) {
			throw new IllegalArgumentException("No vault specified.");
		}

		for (String vaultName : vaultNames) {
			Path vaultPath = Paths.get(args.getVaultPath(vaultName));
			if (!Files.isDirectory(vaultPath)) {
				throw new IllegalArgumentException("Not a directory: " + vaultPath);
			}
			args.addPasswortStrategy(vaultName).validate();

			Path mountPoint = args.getFuseMountPoint(vaultName);
			if (mountPoint != null && !Files.isDirectory(mountPoint)) {
				throw new IllegalArgumentException("Fuse mount point does not exist: " + mountPoint);
			}
		}
	}

	private void startup(Args args) throws IOException {
		Optional<WebDav> server = initWebDavServer(args);
		ArrayList<FuseMount> mounts = new ArrayList<>();

		MasterkeyFileAccess masterkeyFileAccess = new MasterkeyFileAccess(PEPPER, CryptomatorCli.getSecureRandom());

		for (String vaultName : args.getVaultNames()) {
			Path vaultPath = Paths.get(args.getVaultPath(vaultName));
			LOG.info("Unlocking vault \"{}\" located at {}", vaultName, vaultPath);
			String vaultPassword = args.getPasswordStrategy(vaultName).password();
			CryptoFileSystemProperties properties = CryptoFileSystemProperties.cryptoFileSystemProperties().withKeyLoader(keyId -> {
				Preconditions.checkArgument(SCHEME.equalsIgnoreCase(keyId.getScheme()), "Only supports keys with scheme " + SCHEME);
				Path keyFilePath = vaultPath.resolve(keyId.getSchemeSpecificPart());
				return masterkeyFileAccess.load(keyFilePath, vaultPassword);
			}).build();

			Path vaultRoot = CryptoFileSystemProvider.newFileSystem(vaultPath, properties).getPath("/");

			Path fuseMountPoint = args.getFuseMountPoint(vaultName);
			if (fuseMountPoint != null) {
				FuseMount newMount = new FuseMount(vaultRoot, fuseMountPoint);
				if (newMount.mount()) {
					mounts.add(newMount);
				}
			}

			server.ifPresent(serv -> serv.addServlet(vaultRoot, vaultName));
		}

		/* //TODO
		Runnable shutdown = () -> {
			LOG.info("Shutting down...");
			try {
				server.ifPresent(serv -> serv.stop());

				for (FuseMount mount : mounts) {
					mount.unmount();
				}
				LOG.info("Shutdown successful.");
			} catch (Throwable e) {
				LOG.error("Error during shutdown", e);
			}
		};
		waitForShutdown(shutdown);*/
	}

	private Optional<WebDav> initWebDavServer(Args args) {
		Optional<WebDav> server = Optional.empty();
		if (args.hasValidWebDavConf()) {
			server = Optional.of(new WebDav(args.getBindAddr(), args.getPort()));
		}
		return server;
	}

	private void waitForShutdown(Runnable runnable) {
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
		LOG.info("Press Ctrl+C to terminate.");

		// Block the main thread infinitely as otherwise when using
		// Fuse mounts the application quits immediately.
		try {
			Object mainThreadBlockLock = new Object();
			synchronized (mainThreadBlockLock) {
				while (true) {
					mainThreadBlockLock.wait();
				}
			}
		} catch (Exception e) {
			LOG.error("Main thread blocking failed.");
		}
	}
}