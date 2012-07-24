package com.untzuntz.ustack.main;

import org.apache.log4j.BasicConfigurator;

import com.untzuntz.ustack.data.ExternalAPIParams;

public class Configuration {
	
	public static void main(String[] args) throws Exception {

		BasicConfigurator.configure();
		if (args.length < 2 || "-help".equalsIgnoreCase( args[0] ))
		{
			System.err.println("Usage: com.untzuntz.ustack.main.Configuration -addexternalapi [applicationame] [apiname] [publickey] [privatekey]");
			System.err.println("Usage: com.untzuntz.ustack.main.Configuration -removeexternalapi [applicationame] [apiname]");
			return;
		}
		
		if (args[0].equalsIgnoreCase("-addexternalapi"))
		{
			UOpts.setAppName(args[1]);
			ExternalAPIParams api = ExternalAPIParams.createExternalAPIParams(args[2], args[3], args[4]);
			api.save("Command Line");
			System.out.println("API [" + args[2] + "] has been added"); 
		}
		else if (args[0].equalsIgnoreCase("-removeexternalapi"))
		{
			UOpts.setAppName(args[1]);
			ExternalAPIParams api = ExternalAPIParams.getByName(args[2]);
			if (api != null)
			{
				api.delete();
				System.out.println("API [" + args[2] + "] has been deleted"); 
			}
			else
				System.err.println("Could not find API by name '" + args[2] + "'");
		}
	}
	

}
