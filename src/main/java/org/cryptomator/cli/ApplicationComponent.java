package org.cryptomator.cli;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

	CommandHandler commandHandler();

}