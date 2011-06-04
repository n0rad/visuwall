package net.awired.visuwall.server.application;

import javax.servlet.ServletContextEvent;

import net.awired.visuwall.core.application.common.ApplicationHelper;

import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import ch.qos.logback.classic.Logger;

public class VisuwallContextLoaderListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// <configuration>
		// <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		// <layout class="ch.qos.logback.classic.PatternLayout">
		// <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
		// </Pattern>
		// </layout>
		// </appender>
		// <root>
		// <level value="INFO" />
		// <appender-ref ref="STDOUT" />
		// </root>
		// </configuration>

		// TODO USE JORAN to configure logback
		// // assume SLF4J is bound to logback in the current environment
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		//
		// try {
		// JoranConfigurator configurator = new JoranConfigurator();
		// configurator.setContext(lc);
		// // the context was probably already configured by default
		// configuration
		// // rules
		// lc.reset();
		// configurator.doConfigure(args[0]);
		// } catch (JoranException je) {
		// je.printStackTrace();
		// }
		// StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
		//
		// logger.info("Entering application.");

		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(ApplicationHelper.findLogLvl());
		super.contextInitialized(event);
	}

}
