package me.boykev.respawnore;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main
  extends JavaPlugin
  implements Listener
{
  HashMap<Block, Material> block = new HashMap<Block, Material>();
  HashMap<Block, Integer> timer = new HashMap<Block, Integer>();
  
  public void onEnable()
  {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this, this);
    File config = new File(getDataFolder() + File.separator + "config.yml");
    if (!config.exists())
    {
      getLogger().info("Generating config.yml");
      getConfig().addDefault("COAL_ORE", Integer.valueOf(60));
      getConfig().addDefault("IRON_ORE", Integer.valueOf(60));
      getConfig().addDefault("GOLD_ORE", Integer.valueOf(60));
      getConfig().addDefault("DIAMOND_ORE", Integer.valueOf(120));
      getConfig().addDefault("EMERALD_ORE", Integer.valueOf(60));
      getConfig().addDefault("LAPIS_ORE", Integer.valueOf(60));
      getConfig().addDefault("GLOWING_REDSTONE_ORE", Integer.valueOf(60));
      getConfig().addDefault("QUARTZ_ORE", Integer.valueOf(60));
      getConfig().addDefault("block", "AIR");
      getConfig().options().copyDefaults(true);
      saveConfig();
      
      getConfig().options().copyDefaults(true);
      saveConfig();
    }
    BukkitRunnable task = new BukkitRunnable()
    {
      public void run()
      {
        for (Block b : Main.this.timer.keySet())
        {
          int newtime = ((Integer)Main.this.timer.get(b)).intValue() + 1;
          Main.this.timer.put(b, Integer.valueOf(newtime));
          if (newtime >= Main.this.getTime(b)) {
            if (b.getChunk().isLoaded())
            {
              b.setType((Material)Main.this.block.get(b));
              Main.this.block.remove(b);
              Main.this.timer.remove(b);
            }
          }
        }
      }
    };
    task.runTaskTimer(this, 0L, 20L);
  }
  //©Boykev_Development
  public void onDisable()
  {
    for (Block b : this.block.keySet()) {
      b.setType((Material)this.block.get(b));
    }
  }
  
  public int getTime(Block b)
  {
    int time = getConfig().getInt(((Material)this.block.get(b)).toString());
    return time;
  }
  
  @EventHandler(ignoreCancelled=false)
  public void onMine(final BlockBreakEvent event)
  {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }//©Boykev_Development
    if (!getConfig().contains(event.getBlock().getType().toString())) {
      return;
    }
    this.block.put(event.getBlock(), event.getBlock().getType());
    this.timer.put(event.getBlock(), Integer.valueOf(0));
    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        if (event.isCancelled()) {
          return;
        }
        Material material = Material.getMaterial(getConfig().getString("block"));
        event.getBlock().setType(material);
      }
    }, 2L);
  }
  //©Boykev_Development
  @SuppressWarnings("deprecation")
@EventHandler
  public void onClick(PlayerInteractEvent event)
  {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Material material = Material.getMaterial(getConfig().getString("block"));
    Block block = event.getClickedBlock();
    if (block.getType() != material) {
      return;
    }
    if (!this.timer.containsKey(block)) {
      return;
    }
    int left = getTime(block) - ((Integer)this.timer.get(block)).intValue();
    event.getPlayer().sendMessage(ChatColor.RED + "This block will respawn in " + left + " seconds!");
    event.getPlayer().sendTitle(ChatColor.RED + "Respawn in", ChatColor.AQUA + "" + left + " Seconds");
  }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("ore_reload") && sender instanceof Player){
			Player bd = (Player) sender;
			if (bd.hasPermission("OreRespawn.reload")) {
				reloadConfig();
				bd.sendMessage(ChatColor.GREEN + "[OreRespawner] " + ChatColor.RED + "De plugin configuratie is herladen");
				getLogger().warning(ChatColor.RED + "De plugin is herladen");
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "De OreRespawner is herladen!");
				return true;
			}
		}
		return false; 
	}
}
//©Boykev_Development