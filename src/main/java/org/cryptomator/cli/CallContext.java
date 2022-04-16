package org.cryptomator.cli;

public record CallContext(CommandHandler caller, String cmdLine) {

}