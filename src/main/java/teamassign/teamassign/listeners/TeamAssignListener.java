package teamassign.teamassign.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import teamassign.teamassign.ConfigManager;
import teamassign.teamassign.TeamAssign;

import java.util.ArrayList;
import java.util.List;

public class TeamAssignListener implements Listener {

    private TeamAssign plugin;

    public TeamAssignListener(TeamAssign plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            //Bukkit.broadcastMessage("Player world: " + p.getWorld().getName());
            //Bukkit.broadcastMessage("Is on team: " + ConfigManager.isOnTeam(p));
            if(ConfigManager.isOnTeam(p) && p.getWorld().getName().equalsIgnoreCase("spawn")){
                //Bukkit.broadcastMessage("player has been set as dead player");
                ConfigManager.setAsDeadPlayer(p);
            }
        }
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        if(ConfigManager.isDeadPlayer(e.getPlayer())){
            Location spectatorBox = new Location(Bukkit.getWorld("spawn"), 307, 68, 222);
            Location lobbyBox = new Location(Bukkit.getWorld("spawn"), 252, 87, 302);
            if(ConfigManager.isPartOfLastHalf(e.getPlayer()) && ConfigManager.getTotalPlayersCount() == 0){
                e.setRespawnLocation(lobbyBox);
                e.getPlayer().teleport(lobbyBox);
            }else{
                e.setRespawnLocation(spectatorBox);
                e.getPlayer().teleport(spectatorBox);
            }
            ConfigManager.removeFromDeadList(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e){
        Entity en = e.getEntity();
        Entity enDamager = e.getDamager();
        if(en instanceof Player && enDamager instanceof  Player){
            Player damaged = (Player) en;
            Player damager = (Player) enDamager;
            if(damager.getLocation().getWorld().getName().equalsIgnoreCase("spawn")){
                if(ConfigManager.isBlueTeam(damaged) && ConfigManager.isBlueTeam(damager)){
                    e.setCancelled(true);
                }else if(!ConfigManager.isBlueTeam(damaged) && !ConfigManager.isBlueTeam(damager)){
                    e.setCancelled(true);
                }
            }
        }
    }
}
