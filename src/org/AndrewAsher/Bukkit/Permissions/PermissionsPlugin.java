package org.AndrewAsher.Bukkit.Permissions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
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
    private HashMap<String, Permission> plugins = new HashMap<String, Permission>();
    private JSONObject perms;
    

    // -- Basic stuff
    @Override
    public void onEnable() {

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
        	JSONArray perm = perms.getJSONArray(player.getName());
        	
        	for (int i = 0; i < perm.length(); i ++)
        	{
        		attachment.setPermission(plugins.get(perm.getString(i)), true);
        	}
        	
        	giveDefaultPermissions(attachment);
        	
        	player.recalculatePermissions();
        	
        }
        catch (JSONException e)
        {
        	
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
    		
    		for (Iterator<String> it = perms.keys(); it.hasNext();)
    		{
    			String name = it.next();
    			JSONArray perm = perms.getJSONArray(name);
    			PermissionAttachment attachment = permissions.get(name);
    			if (attachment == null)
    				continue;
            	
            	for (int i = 0; i < perm.length(); i ++)
            	{
            		attachment.setPermission(plugins.get(perm.getString(i)), true);
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
    
    private void giveDefaultPermissions(PermissionAttachment attachment)
    {
    	attachment.setPermission("admincmd.player.list", true);
    	attachment.setPermission("wolfpound.use", true);
    	attachment.setPermission("sortal.warp", true);
    	attachment.setPermission("sortal.coords", true);
    	attachment.setPermission("admincmd.time.day", true);
    	attachment.setPermission("admincmd.item.add", true);
    	attachment.setPermission("admincmd.tp.to", true);
    	attachment.setPermission("sortal.createwarp", true);
    	attachment.setPermission("sortal.placesign", true);
    	attachment.setPermission("sortal.delwarp", true);
    	attachment.setPermission("worldedit.navigation.jumpto", true);
    	attachment.setPermission("worldedit.navigation.thru", true);
    	attachment.setPermission("admincmd.weather.clear", true);
    	attachment.setPermission("admincmd.spawn.tp", true);
    	attachment.setPermission("permissions.build", true);
    	
    }

}