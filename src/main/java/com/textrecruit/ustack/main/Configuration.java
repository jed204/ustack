package com.textrecruit.ustack.main;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.textrecruit.ustack.aaa.ResourceDefinition;
import com.textrecruit.ustack.aaa.ResourceLink;
import com.textrecruit.ustack.aaa.RoleDefinition;
import com.textrecruit.ustack.aaa.UBasePermission;
import com.textrecruit.ustack.data.APIClient;
import com.textrecruit.ustack.data.UserAccount;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.apache.log4j.BasicConfigurator;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Configuration {

	private static final String AppName = "ConfigApp";
	private PrintWriter out;
	
	public static void main(String[] args) throws Exception {

		BasicConfigurator.configure();
		Configuration cfg = new Configuration();
		cfg.run();

		// if (args.length < 2 || "-help".equalsIgnoreCase( args[0] ))
		// {
		// System.err.println("Usage: com.textrecruit.ustack.main.Configuration -addexternalapi [applicationame] [apiname] [publickey] [privatekey]");
		// System.err.println("Usage: com.textrecruit.ustack.main.Configuration -removeexternalapi [applicationame] [apiname]");
		// return;
		// }
		//
		// if (args[0].equalsIgnoreCase("-addexternalapi"))
		// {
		// UOpts.setAppName(args[1]);
		// ExternalAPIParams api =
		// ExternalAPIParams.createExternalAPIParams(args[2], args[3], args[4]);
		// api.save("Command Line");
		// System.out.println("API [" + args[2] + "] has been added");
		// }
		// else if (args[0].equalsIgnoreCase("-removeexternalapi"))
		// {
		// UOpts.setAppName(args[1]);
		// ExternalAPIParams api = ExternalAPIParams.getByName(args[2]);
		// if (api != null)
		// {
		// api.delete();
		// System.out.println("API [" + args[2] + "] has been deleted");
		// }
		// else
		// System.err.println("Could not find API by name '" + args[2] + "'");
		// }
	}

	private void run() throws Exception {
		
		 ConsoleReader reader = new ConsoleReader();
		 
         List<Completer> completors = new LinkedList<Completer>();
         completors.add(new StringsCompleter(getCommandNames()));
         
         for (Completer c : completors) {
             reader.addCompleter(c);
         }
         
         // get environment name
		 reader.setPrompt("Environment name: ");

         String envName = reader.readLine();
         UOpts.setAppName(envName);
         
		 reader.setPrompt("prompt> ");

         String line;
         out = new PrintWriter(reader.getOutput());

 		 List<Command> cmds = getCommands();

         while ((line = reader.readLine()) != null) {
            	 
             if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                 break;
             
             boolean found = false;
             for (int i = cmds.size() - 1; i >= 0; i--) {
            	 
            	 Command c = cmds.get(i);
            	 if (line.toLowerCase().startsWith(c.name.toLowerCase()))
            	 {
            		 found = true;
     				@SuppressWarnings("rawtypes") Class[] partypes = new Class[]{ String.class };
            		 Method m = getClass().getDeclaredMethod(c.method, partypes);
            		 if (m == null)
            			 out.println("Error finding command method: " + c.method + "()");
            		 else
            			 m.invoke(this, new Object[] { line.substring(c.name.length()).trim() });
            	 }
            		 
             }
             
             if (!found)
            	 out.println("Unknown command: " + line);
             
             out.flush();
         }
	}
	
	private List<String> getCommandNames() {
		List<String> ret = new ArrayList<String>();
		List<Command> cmds = getCommands();
		for (Command c : cmds)
			ret.add(c.name);
		
		return ret;
	}
	
	private List<Command> getCommands()
	{
		List<Command> ret = new ArrayList<Command>();
		ret.add(new Command("help", "This output", "help"));
		ret.add(new Command("initialize", "Initializes the database by creating a new user and the proper initial roles", "initialize"));
		ret.add(new Command("use", "Switches environment", "use"));
		return ret;
	}
	
	protected void use(String line)
	{
		if (line.length() == 0)
		{
			out.println("Usage: use <environment name>");
			return;
		}
		
        UOpts.setAppName(line);
	}
	
	protected void initialize(String line)
	{
		String[] spl = line.split(" ");
		if (spl.length != 3)
		{
			out.println("Usage: initialize <admin_username> <admin_password> <api_clientid>");
			return;
		}
		
		String userName = spl[0];
		String password = spl[1];
		String clientId = spl[2];
		
		try {
			/*
			 * User
			 */
			ResourceDefinition res = ResourceDefinition.getByName("Setup App");
			if (res == null)
				res = ResourceDefinition.createResource("Setup App", ResourceDefinition.TYPE_USERACCESS);
			
			if (!res.hasRole("General"))
			{
				RoleDefinition role = new RoleDefinition("General");
				role.addPermission(UBasePermission.Login.getPermission());
				role.addPermission(UBasePermission.ManageRoles.getPermission());
				res.addRole(role);
				res.addCanManage("app");
				res.addManagedBy("app");
			}
			res.save(AppName);

			UserAccount user = UserAccount.getUser( userName );
			if (user == null)
				user = UserAccount.createUser("app setup", userName, password); 

			user.addResourceLinkIfNeeded("Setup", new ResourceLink(res, "General"));
			user.save(AppName);

			/*
			 * API
			 */
			res = ResourceDefinition.getByName("Setup Api");
			if (res == null)
				res = ResourceDefinition.createResource("Setup Api", ResourceDefinition.TYPE_APIACCESS);
			
			if (!res.hasRole("Admin"))
			{
				RoleDefinition role = new RoleDefinition("Admin");
				role.addPermission(UBasePermission.ManageRoles.getPermission());
				res.addRole(role);
				res.addCanManage("app");
				res.addManagedBy("app");
			}
			res.save(AppName);

			APIClient selectedAPI = APIClient.getAPIClient(clientId);
			if (selectedAPI == null)
			{
				selectedAPI = APIClient.createAPI(AppName, clientId);
				selectedAPI.addManagedBy("app");
				selectedAPI.addResourceLink("Core", new ResourceLink(res, "Admin"));
				selectedAPI.save(AppName);
			}
			
			BasicDBList keyList = selectedAPI.getAPIKeys();
			for (int i = 0; i < keyList.size(); i++)
			{
				DBObject k = (DBObject)keyList.get(i);
				out.println("Api Key: " + selectedAPI.getKey((String)k.get("uid")));
			}
			
			
		} catch (Exception e) {
			out.println("ERROR: " + e.getMessage());
		}
		
	}
	
	protected void help(String line)
	{
		out.println("HELP");
		out.println("====");
		
		List<Command> cmds = getCommands();
		for (Command c : cmds)
			out.println(String.format("%-32s%s", c.name, c.help));
	}
	
	private class Command {
		
		String name;
		String help;
		String method;
		
		public Command(String n, String h, String m) {
			name = n;
			help = h;
			method = m;
		}
		
	}
	

}
