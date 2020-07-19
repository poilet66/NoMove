package me.bukkit.nomove;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoMove extends JavaPlugin implements Listener {
	
	private int spawncount = 0;
	private ArrayList<Player> players = new ArrayList<Player>();

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
	}
	
	@Override
	public void onDisable() {
		this.getServer().getPluginManager().disablePlugin(this);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(this.getConfig().getConfigurationSection("Players").getKeys(false).size() < 25) {
			this.getConfig().set("Players." + player.getName(), player);
			this.saveConfig();
		}
		else {
			this.getConfig().set("Spectators." + player.getName(), player);
			this.saveConfig();
		}
	}
		
	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(this.getConfig().getConfigurationSection("Players").getKeys(false).contains(player.getName())) {
			this.getConfig().set("Players." + player.getName(), null);
			this.saveConfig();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.isOp()) {
				
				/*if(cmd.getName().equalsIgnoreCase("setup")) {
					int count = 0;
					for(String playerID : this.getConfig().getConfigurationSection("Players").getKeys(true)) {
						Player loadedPlayer = this.getServer().getPlayer(playerID);
						
					}
				}*/
				
				if(cmd.getName().equalsIgnoreCase("setupbasic")) {
					//gamePlayer.sendMessage("spawns: " + this.getConfig().getConfigurationSection("SpawnLoc").getKeys(false).size());
					//gamePlayer.sendMessage("count: " + count);
					int count = 0;
					for(Player onlinePlayer : getServer().getOnlinePlayers()) {
						if(onlinePlayer instanceof Player) {
							if(players.contains(onlinePlayer)) {
								if(count >= this.getConfig().getConfigurationSection("SpawnLoc").getKeys(false).size()) {
									onlinePlayer.sendMessage(ChatColor.RED + "There were no spaces left so you were made a spectator.");
									getServer().dispatchCommand(getServer().getConsoleSender(), "spec on " + onlinePlayer.getName());
								}
								else {
									Location teleLoc = new Location(Bukkit.getWorld("world"), this.getConfig().getDouble("SpawnLoc.Loc_" + count + ".x"), this.getConfig().getDouble("SpawnLoc.Loc_" + count + ".y"), this.getConfig().getDouble("SpawnLoc.Loc_" + count + ".z"), (float) this.getConfig().getDouble("SpawnLoc.Loc_" + count + ".pitch"), (float) this.getConfig().getDouble("SpawnLoc.Loc_" + count + ".yaw"));
									onlinePlayer.teleport(teleLoc);
								}						
								count++;
							}
							else {
								onlinePlayer.sendMessage(ChatColor.RED + "You werent in the player list so you were made a spectator.");
								getServer().dispatchCommand(getServer().getConsoleSender(), "spec on " + onlinePlayer.getName());
							}
						}

						
					}
				}
				
				else if(cmd.getName().equalsIgnoreCase("savespawnloc")) {
					this.getConfig().set("SpawnLoc.Loc_" + spawncount + ".x", player.getLocation().getX());
					this.getConfig().set("SpawnLoc.Loc_" + spawncount + ".y", player.getLocation().getY());
					this.getConfig().set("SpawnLoc.Loc_" + spawncount + ".z", player.getLocation().getZ());
					this.getConfig().set("SpawnLoc.Loc_" + spawncount + ".pitch", player.getLocation().getPitch());
					this.getConfig().set("SpawnLoc.Loc_" + spawncount + ".yaw", player.getLocation().getYaw());
					this.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Location has been saved.");
					spawncount++;
				}
				
				else if(cmd.getName().equalsIgnoreCase("hgspec")) {
					if(args.length != 2) {
						sender.sendMessage(ChatColor.RED + "You have not included a subcommand and player name.");
					}
					else {
						Player target = Bukkit.getServer().getPlayer(args[1]);
						if(args[0].equalsIgnoreCase("add")) {
							if(!(players.contains(target))) {
								players.add(target);
								sender.sendMessage(ChatColor.GREEN + target.getDisplayName() + " was added to player list.");
							}
							else {
								sender.sendMessage(ChatColor.RED + target.getDisplayName() + " is already in player list.");
							}
						}
						else if(args[0].equalsIgnoreCase("remove")) {
							if(!(players.contains(target))) {
								sender.sendMessage(ChatColor.RED + target.getDisplayName() + " is already not in player list.");
							}
							else {
								players.remove(target);
								sender.sendMessage(ChatColor.GREEN + target.getDisplayName() + " was removed from player list.");
							}
						}
						else {
							sender.sendMessage("incorrect subcommand");
						}
					}
				}
				
			}
			
			else {
				player.sendMessage(ChatColor.RED + "Only OPs can run this command.");
			}
			
			return true;
		}
		
		else {
			sender.sendMessage(ChatColor.RED + "Only players can run this command.");
			return false;
		}
	}
	
	
}
