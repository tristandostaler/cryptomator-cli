package org.cryptomator.cli.commands;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import org.cryptomator.cli.commands.impl.CommandBind;
import org.cryptomator.cli.commands.impl.CommandExit;
import org.cryptomator.cli.commands.impl.CommandHelp;
import org.cryptomator.cli.commands.impl.CommandMount;
import org.cryptomator.cli.commands.impl.CommandUnbind;
import org.cryptomator.cli.commands.impl.CommandVersion;

@Module
public abstract class CommandModule {

	@IntoSet
	@Binds
	public abstract Command provideCommandBind(CommandBind cmd);

	@IntoSet
	@Binds
	public abstract Command provideCommandExit(CommandExit cmd);

	@IntoSet
	@Binds
	public abstract Command provideCommandHelp(CommandHelp cmd);

	@IntoSet
	@Binds
	public abstract Command provideCommandMount(CommandMount cmd);

	@IntoSet
	@Binds
	public abstract Command provideCommandUnbind(CommandUnbind cmd);

	@IntoSet
	@Binds
	public abstract Command provideCommandVersion(CommandVersion cmd);

}