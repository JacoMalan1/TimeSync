/*
   A plugin for Bukkit to sync all of your worlds' time.
 
    Copyright (C) 2017  Jacob Jacobus Malan

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.codelog.timesync;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class CommandSync implements CommandExecutor 
{

	Main plugin;

	public CommandSync(Main instance)
	{
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{

		try
		{

			FileConfiguration config = plugin.getConfig();
			if (config.getBoolean("Debug"))
			{

				sender.sendMessage(Main.prefix + ChatColor.GRAY + "Args: " + args);

			}

			if (args[0].length() == 0)
			{

				sendHelp(sender);

				return true;

			}

			try
			{

				if (args[0].equals(""))
				{

					sendHelp(sender);
					return true;

				}
				else if (args[0].equals("sync"))
				{

					if (!sender.hasPermission("timesync.sync"))
					{
						
						sender.sendMessage(ChatColor.RED + "You lack the required permissions node: " + ChatColor.DARK_RED + "timesync.sync");
						
						return true;
						
					}
					
					if (args[1].length() == 0)
					{
						sendHelp(sender);
						return true;
					}

					int time;

					try
					{
						time = Integer.parseInt(args[1]);
					}
					catch (NumberFormatException e)
					{
						time = 0;
						sendUsage(sender);
						return true;
					}

					for (World w:Bukkit.getWorlds())
					{

						if (!config.getStringList("ExcludeWorlds").contains(w.getName()))
						{

							w.setTime(time);
							sender.sendMessage(ChatColor.GREEN + "Time in world " + w.getName() + " set to " + Integer.toString(time) + ".");

						}
						else
						{
							sender.sendMessage(ChatColor.YELLOW + "World " + w.getName() + " excluded from sync.");
						}

					}
					return true;
				}

				else if (args[0].equals("exclude"))
				{
					if (!sender.hasPermission("timesync.exclude"))
					{
						
						sender.sendMessage(ChatColor.RED + "You lack the required permissions node: " + ChatColor.DARK_RED + "timesync.exclude");
						
						return true;
						
					}
					
					if (args[1].length() == 0)
					{
						sendHelp(sender);
						return true;
					}

					boolean found = false;

					for (World w:Bukkit.getWorlds())
					{
						if (w.getName().equals(args[1]))
						{
							found = true;
							break;
						}
					}

					if (!found)
					{
						sender.sendMessage(ChatColor.RED + "World not found!");
						return true;
					}
					else
					{
						List<String> excludeWorlds = config.getStringList("ExcludeWorlds");
						excludeWorlds.add(args[1]);
						config.set("ExcludeWorlds", excludeWorlds);
						sender.sendMessage(ChatColor.GREEN + "World " + args[1] + " has been excluded from time synchronisation.");
						plugin.saveConfig();
						return true;
					}

				}

				else if (args[0].equals("include"))
				{
					if (!sender.hasPermission("timesync.exclude"))
					{
						
						sender.sendMessage(ChatColor.RED + "You lack the required permissions node: " + ChatColor.DARK_RED + "timesync.exclude");
						
						return true;
						
					}
					
					if (args[1].length() == 0)
					{
						sendHelp(sender);
						return true;
					}

					List<String> excludeWorlds = config.getStringList("ExcludeWorlds");

					int i = 0;
					for (String w:excludeWorlds)
					{

						if (w.equals(args[1]))
						{
							excludeWorlds.remove(i);
							config.set("ExcludeWorlds", excludeWorlds);
							sender.sendMessage(ChatColor.GREEN + "World " + args[1] + " removed from exclude list.");
							plugin.saveConfig();
							return true;
						}

						i++;

					}

					sender.sendMessage(ChatColor.RED + "World not found!");
					return true;

				}

				else if (args[0].equalsIgnoreCase("help"))
				{
					if (!sender.hasPermission("timesync.use"))
					{
						
						sender.sendMessage(ChatColor.RED + "You lack the required permissions node: " + ChatColor.DARK_RED + "timesync.use");
						
						return true;
						
					}
					sendHelp(sender);
				}

				else
				{

					sendUsage(sender);
					return true;

				}

			}
			catch (Exception e)
			{

				if (config.getBoolean("Debug"))
				{
					sender.sendMessage(Main.prefix + e.getMessage());
				}

				sendUsage(sender);
				return true;

			}
		}
		catch (Exception e)
		{

			sender.sendMessage(e.getMessage());
			return true;

		}
		return true;

	}

	public void sendHelp(CommandSender sender)
	{

		sender.sendMessage(ChatColor.BLUE + "Help page 1 of 1 for timesync:");
		sender.sendMessage(ChatColor.BLUE +"/timesync sync <time>");
		sender.sendMessage(ChatColor.BLUE + "/timesync exclude <world>");
		sender.sendMessage(ChatColor.BLUE + "/timesync include <world>");

	}

	public void sendUsage(CommandSender sender)
	{

		sender.sendMessage(ChatColor.RED + "usage: /timesync [sync:exclude:include]>");

	}
}
