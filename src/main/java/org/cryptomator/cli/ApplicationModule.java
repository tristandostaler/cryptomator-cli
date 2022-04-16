package org.cryptomator.cli;

import dagger.Module;
import dagger.Provides;
import org.cryptomator.cli.commands.CommandModule;
import org.cryptomator.cli.frontend.WebDav;

import javax.inject.Singleton;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicReference;

@Module(includes = CommandModule.class)
public abstract class ApplicationModule {

	@Singleton
	@Provides
	public static SecureRandom provideSecureRandom() {
		try {
			return SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("A strong algorithm must exist in every Java platform.", e);
		}
	}

	@Singleton
	@Provides
	public static AtomicReference<WebDav> provideWebDav() {
		return new AtomicReference<>();
	}
}