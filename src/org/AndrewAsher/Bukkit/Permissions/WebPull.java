package org.AndrewAsher.Bukkit.Permissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimerTask;
import java.util.logging.Level;


public class WebPull extends TimerTask {
	
	private static final String URL = "http://localhost";
	
	PermissionsPlugin plugin;
	String last;
		
	public WebPull(){}
	
	public WebPull(PermissionsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() 
	{
		try {
			URL remote = new URL(URL);
			URLConnection remoteFile = remote.openConnection();
			BufferedReader in = new BufferedReader (new InputStreamReader(remoteFile.getInputStream()));
			
			String input = "";
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				input += inputLine;
			}
			
			if (!input.equals(last))
			{
				last = input;
				plugin.updatePermissions(input);
			}
		} 
		catch (MalformedURLException e) 
		{
			plugin.getServer().getLogger().logp(Level.SEVERE, "WebPull", "Run", "URL to pull from is malformed");
			
		} 
		catch (IOException e) 
		{
			plugin.getServer().getLogger().logp(Level.SEVERE, "WebPull", "Run", "Error connecting to web server");
		}
		

	}
	
	public void test()
	{
		try {
			URL remote = new URL(URL);
			URLConnection remoteFile = remote.openConnection();
			BufferedReader in = new BufferedReader (new InputStreamReader(remoteFile.getInputStream()));
			
			String input = "";
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				input += inputLine;
			}
			
			if (!input.equals(last))
			{
				last = input;
				plugin.updatePermissions(input);
			}
		} 
		catch (MalformedURLException e) 
		{
			
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	


}
