package net.awired.visuwall.cli;

import net.awired.ajsl.cli.argument.CliArgumentManager;
import net.awired.ajsl.cli.argument.args.CliOneParamArgument;
import net.awired.ajsl.cli.param.CliParamInt;
import net.awired.ajsl.cli.param.CliParamString;

class ArgumentManager extends CliArgumentManager {

	public final CliOneParamArgument<Integer> portArg;
	
	public final CliOneParamArgument<String> contextPath;
	
	
	
	public ArgumentManager(Main main) {
		super("visuwall");
		
		CliParamInt portParam = new  CliParamInt("port");
		portParam.setNegativable(false);
		portParam.setZeroable(false);
		
		portArg = new CliOneParamArgument<Integer>('p', portParam);
		portArg.setParamOneDefValue(8081);
		portArg.setName("port");
		portArg.setDescription("Port for servlet Contrainer");
		addArg(portArg);
		
	
		contextPath = new CliOneParamArgument<String>('c', new CliParamString("contextPath"));
		contextPath.setParamOneDefValue("/");
		contextPath.setName("contextpath");
		contextPath.setDescription("Context path to access the application");
		addArg(contextPath);
		
		
		
	}
}