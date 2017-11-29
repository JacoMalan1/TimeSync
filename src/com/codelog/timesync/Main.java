package com.codelog.timesync;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener
{

	public FileConfiguration config = getConfig();
	boolean debug = false;
	public static String prefix = ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "TimeSync" + ChatColor.GRAY + "]: ";

	@Override
	public void onEnable()
	{

		List<String> strings = null;
		config.addDefault("SyncOnStart", true);
		config.addDefault("Debug", false);
		config.addDefault("StartTime", 0);
		config.addDefault("ExcludedWorlds", strings);
		config.options().copyDefaults(true);
		saveConfig();
		debug = config.getBoolean("Debug");

		this.getCommand("timesync").setExecutor(new CommandSync(this));

		getServer().getPluginManager().registerEvents(this, this);

		if (config.getBoolean("SyncOnStart"))
		{

			syncWorlds(config.getLong("StartTime"));

		}

		new BukkitRunnable()
		{

			public void run()
			{
				long[] times = new long[Bukkit.getWorlds().size()];
				int i = 0;
				long time = 0;

				for (World w:Bukkit.getWorlds())
				{

					if (!config.getStringList("ExcludeWorlds").contains(w.getName()))
					{
						if (config.getBoolean("Debug"))
						{
							
							getServer().broadcastMessage(prefix + ChatColor.GRAY + "Checking world: " + w.getName());
							
						}
						times[i] = w.getTime();
						i++;
					}

				}

				time = times[0];

				for (i = 0; i < times.length ; i++)
				{

					if (!(time == times[i]))
					{

						if (config.getBoolean("Debug"))
						{
							getServer().broadcastMessage(prefix + ChatColor.GRAY + "Single: " + Long.toString(time) + " List: " + times.toString());
						}
						syncWorlds(time);

					}

				}
			}

		}.runTaskTimer(this, 20 * 10, 20 * 20);
		
		getServer().getLogger().info("TimeSync v" + this.getDescription().getVersion() + " has been enabled.");

	}

	@Override
	public void onDisable()
	{
		getServer().getLogger().info("TimeSync v" + this.getDescription().getVersion() + " has been disabled.");
	}

	protected void syncWorlds(long time)
	{

		for (World w:Bukkit.getWorlds())
		{

			if (!config.getStringList("ExcludeWorlds").contains(w.getName()))
			{			
				w.setTime(time);
				continue;				
			}

		}

		if (debug)
		{

			getServer().broadcastMessage(prefix + ChatColor.GRAY + "Syncing worlds.");

		}


	}

}
