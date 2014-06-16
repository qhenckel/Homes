package io.github.qhenckel.homes;

import java.util.Set;

import io.github.qhenckel.DataBase;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Home extends JavaPlugin {

	DataBase db;
	
	public void onEnable(){
		db = new DataBase(this, "Homes");
	}
	
	public void onDisable(){
		db.saveDataBase();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if(sender instanceof Player){
			Player p = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("homelist")){
				Set<String> blah = db.getConfig().getConfigurationSection(p.getName()).getKeys(false);
				String[] keys = new String[blah.size()];
				keys = blah.toArray(keys);
				sender.sendMessage("List of your homes:");
				for(int i = 0; i < (keys.length); i++) {
					sender.sendMessage(keys[i]);
				}
				return true;
			}
			
			if(args.length >= 1){
				String name = args[0];
				if(cmd.getName().equalsIgnoreCase("home")){
					if(!isHome(name, p)){
						if(isHome(name.replaceAll("<", "").replaceAll(">", ""), p)){
							p.sendMessage("Looks like you had '< >' around your home name.");
							p.sendMessage("Don't do that. Your home is now called:" + name.replaceAll("<", "").replaceAll(">", ""));
							return true;
						} else {
							p.sendMessage(name + " Is not a valid home. Try /homelist to see a list of your valid homes.");
							return true;
						}
					}
					//world;x,y,z
					
					if(!db.getConfig().isString(p.getName() + "." + name)){
						Bukkit.getLogger().info("is new!");
						ConfigurationSection f = db.getConfig().getConfigurationSection(p.getName() + "." + name);
						World world = Bukkit.getWorld(f.getString("world"));
						double x = f.getDouble("x");
						double y = f.getDouble("y");
						double z = f.getDouble("z");
						float yaw = f.getLong("yaw");
						float pitch = f.getLong("pitch");
						p.teleport(new Location(world, x, y, z, yaw, pitch));
						return true;
					} else {
						Bukkit.getLogger().info("is old!");
						String par = db.getConfig().getString(p.getName() + "." + name);
						World world = Bukkit.getWorld(par.split(";")[0]);
						double x = Double.parseDouble(par.split(";")[1].split(",")[0]);
						double y = Double.parseDouble(par.split(";")[1].split(",")[1]);
						double z = Double.parseDouble(par.split(";")[1].split(",")[2]);
						p.teleport(new Location(world, x, y, z));
						return true;
					}
				}
				
				if(cmd.getName().equalsIgnoreCase("sethome")){
					if(name.indexOf('<') != -1 || name.indexOf('>') != -1){
						p.sendMessage("looks like you tried to use carrots in your home name.");
						p.sendMessage("Don't do that. Try again with out any '< >'");
						return true;
					}
					Location loc = p.getLocation();
					db.getConfig().set(p.getName() + "." + name + ".world", loc.getWorld().getName());
					db.getConfig().set(p.getName() + "." + name + ".x", loc.getX());
					db.getConfig().set(p.getName() + "." + name + ".y", loc.getY());
					db.getConfig().set(p.getName() + "." + name + ".z", loc.getZ());
					db.getConfig().set(p.getName() + "." + name + ".pitch", loc.getPitch());
					db.getConfig().set(p.getName() + "." + name + ".yaw", loc.getYaw());
					db.saveDataBase();
					p.sendMessage("Home " + name + " set successfully!");
					return true;
				}
				
				if(cmd.getName().equalsIgnoreCase("delhome")){
					if(!isHome(name, p)){
						if(isHome(name.replaceAll("<", "").replaceAll(">", ""), p)){
							p.sendMessage("Looks like you had '< >' around your home name.");
							p.sendMessage("Don't do that. Your home is now called:" + name.replaceAll("<", "").replaceAll(">", ""));
							return true;
						} else {
							p.sendMessage(name + " Is not a valid home. Try /homelist to see a list of your valid homes.");
							return true;
						}
					}
					db.getConfig().set(p.getName() + "." + name, null);
					db.saveDataBase();
					p.sendMessage("Home " + name + " deleted successfully!");
					return true;
				}
			} else {
				sender.sendMessage(cmd.getUsage());
				return true;
			}			
		} else {
			sender.sendMessage("you are not a player, silly.");
			return true;
		}
		return false;
	}

	public boolean isHome(String name, Player p) {
		Set<String> keys = db.getConfig().getConfigurationSection(p.getName()).getKeys(false);
		return keys.contains(name);
	}
}
