package teamassign.teamassign;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import teamassign.teamassign.commands.Commands;
import teamassign.teamassign.listeners.TeamAssignListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TeamAssign extends JavaPlugin {

    private static TeamAssign instance;

    @Override
    public void onEnable() {
        instance = this;
        new Commands(this);
        new TeamAssignListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TeamAssign getInstance(){
        return instance;
    }

    public static List<Player> getPlayersInLobby(Player p){
        Location centerLoc = new Location(p.getLocation().getWorld(), 252, 87, 302);
        Collection<Entity> entitiesInLobby = centerLoc.getWorld().getNearbyEntities(centerLoc, 9, 12, 9);
        //Bukkit.broadcastMessage("Entities: " + entitiesInLobby.toString());
        List<Player> playersInLobby = new ArrayList<>();
        for(Entity e : entitiesInLobby){
            if(e instanceof Player){
                playersInLobby.add(((Player) e).getPlayer());
            }
        }
        return playersInLobby;
    }


}
