package org.AndrewAsher.Bukkit.Permissions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Main class for PermissionsBukkit.
 */
public class PermissionsPlugin extends JavaPlugin {

    private BlockListener blockListener = new BlockListener(this);
    private PlayerListener playerListener = new PlayerListener(this);
    private HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
    private HashMap<String, Permission> groups = new HashMap<String, Permission>();
    private JSONObject perms;
    private Timer t;
    

    // -- Basic stuff
    @Override
    public void onEnable() {
    	
    	//generate groups from config.yml
    	createGroups();
    	
    	//used to pull permissions from web server
        t = new Timer();
        
        t.scheduleAtFixedRate(new WebPull(this), 0, 60000);

        // Events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_KICK, playerListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);

        // Register everyone online right now
        for (Player p : getServer().getOnlinePlayers()) {
            registerPlayer(p);
        }

        // How are you gentlemen
        getServer().getLogger().info(getDescription().getFullName() + " is now enabled");
        
        
    	
    }

    @Override
    public void onDisable() {
        // Unregister everyone
        //for (Player p : getServer().getOnlinePlayers()) {
        //    unregisterPlayer(p);
        //}

        // Good day to you! I said good day!
        getServer().getLogger().info(getDescription().getFullName() + " is now disabled");
    }



    // -- Plugin stuff
    protected void registerPlayer(Player player) {
        PermissionAttachment attachment = player.addAttachment(this);
        permissions.put(player.getName(), attachment);
        
        
        try
        {
        	//parse JSON from server to check for this user's permissions
        	JSONArray perm = perms.getJSONArray(player.getName());
        	
        	//if not registered elsewhere do not grant any permissions
        	if (perm == null)
        		return;
        	
        	//grant all permissions defined
        	for (int i = 0; i < perm.length(); i ++)
        	{
        		String p = perm.getString(i);
        		Permission permission = groups.get(p);
        		
        		//if current permission is a group
        		if(permission != null)
        			attachment.setPermission(permission, true);
        		
        		//if current permission is a single permission
        		else
        			attachment.setPermission(p, true);
        	}
        	
        	giveDefaultPermissions(attachment);
        	
        	player.recalculatePermissions();
        	
        }
        catch (JSONException e)
        {
        	getServer().getLogger().logp(Level.SEVERE, "PermissionsPlugin", "registerPlayer", "JSON is malformed please check output from remote server");
        }
        
    }

    protected void unregisterPlayer(Player player) {
        player.removeAttachment(permissions.get(player.getName()));
        permissions.remove(player.getName());
    }
    
    protected void updatePermissions(String newPermissions)
    {
    	try
    	{
    		perms = new JSONObject(new JSONTokener(newPermissions));
    		
    		for (@SuppressWarnings("unchecked") Iterator<String> it = perms.keys(); it.hasNext();)
    		{
    			String name = it.next();
    			JSONArray perm = perms.getJSONArray(name);
    			PermissionAttachment attachment = permissions.get(name);
    			if (attachment == null)
    				continue;
            	
            	for (int i = 0; i < perm.length(); i ++)
            	{
            		String p = perm.getString(i);
            		Permission permission = groups.get(p);
            		
            		if(permission != null)
            			attachment.setPermission(permission, true);
            		else
            			attachment.setPermission(p, true);
            	}
            	
            	giveDefaultPermissions(attachment);
            	
            	getServer().getPlayer(name).recalculatePermissions();
    		}
    		
    	}
        catch (JSONException e)
        {
        	getServer().getLogger().logp(Level.SEVERE, "PermissionsPlugin", "updatePermissions", "Unable to parse JSON");
        }
    }
    
    
    private void createGroups()
    {
    	HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
    	Map<String, Object> all = getConfiguration().getAll();
    	Iterator<String> it = getConfiguration().getNodes("permissions.groups").keySet().iterator();
    	
    	while(it.hasNext())
    	{
    		String group = it.next();
    		
    		for (String s : getConfiguration().getKeys("permissions.groups." + group))
    		{
    			temp.put(s, (Boolean) all.get("permissions.groups." + group + "." + s));
    		}
    		
    		groups.put(group, new Permission("permissions.groups." + group, PermissionDefault.FALSE, (HashMap<String, Boolean>) temp.clone()));
    		temp.clear();
    	}
    	
    }
    
    private void giveDefaultPermissions(PermissionAttachment a)
    {
    	Permission p = groups.get("default");
    	
    	if(p != null)
    		a.setPermission(p, true);
    	else
    		getServer().getLogger().log(Level.WARNING, "Attempted to add default permissions, but no group \"default\" exists.");
    }

}
