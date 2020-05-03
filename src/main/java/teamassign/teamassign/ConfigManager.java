package teamassign.teamassign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static TeamAssign plugin = TeamAssign.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    public static void assignRandTeams(Player p){
        Location centerLoc = new Location(p.getLocation().getWorld(), 252, 87, 302);
        List<Player> playersInLobby = TeamAssign.getPlayersInLobby(p);
        boolean isBlueTeam = true;

        List<String> blueTeam = new ArrayList<>();
        List<String> redTeam = new ArrayList<>();
        ScoreboardSetter ss = new ScoreboardSetter();

        for(Player pl : playersInLobby){
            String playerName = pl.getName();
            if(isBlueTeam){
                blueTeam.add(playerName);
                pl.sendMessage("[" + plugin.getName() + "] You have been assigned to " + ChatColor.BLUE + "blue team!");
                isBlueTeam = false;
            }else{
               redTeam.add(playerName);
                pl.sendMessage("[" + plugin.getName() + "] You have been assigned to "+ ChatColor.RED + "red team!");
                isBlueTeam = true;
            }
            ss.setScoreboards(pl);
        }

        //Bukkit.broadcastMessage("Red:" + redTeam.size());
        //Bukkit.broadcastMessage("Blue:" + blueTeam.size());
        config.set("RedTeam", redTeam);
        config.set("BlueTeam", blueTeam);
        TeamAssign.getInstance().saveConfig();
    }

    public static List<String> getRedTeam(){
        List<String> redTeam = config.getStringList("RedTeam");
        for(String b : redTeam){
            if(Bukkit.getPlayer(b) == null){
                redTeam.remove(b);
            }else{
                if(!Bukkit.getPlayer(b).isOnline()){
                    redTeam.remove(b);
                }
            }
        }
        return redTeam;
    }

    public static List<String> getBlueTeam(){
        List<String> blueTeam = config.getStringList("BlueTeam");
        for(String b : blueTeam){
            if(Bukkit.getPlayer(b) == null){
                blueTeam.remove(b);
            }else{
                if(!Bukkit.getPlayer(b).isOnline()){
                    blueTeam.remove(b);
                }
            }
        }
        return blueTeam;
    }

    public static boolean isBlueTeam(Player p){
        return getBlueTeam().contains(p.getName());
    }

    public static List<Player> getAllTeamPlayers(){
        List<String> redTeam = getRedTeam();
        List<String> blueTeam = getBlueTeam();
        List<Player> allPlayers = new ArrayList<>();
        for(String playerName : redTeam){
            if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName))){
                allPlayers.add(Bukkit.getPlayer(playerName));
            }
        }
        for(String playerName : blueTeam){
            if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName))){
                allPlayers.add(Bukkit.getPlayer(playerName));
            }
        }
        return allPlayers;
    }

    public static void setAsDeadPlayer(Player p){

        List<String> redTeam = getRedTeam();
        List<String> blueTeam = getBlueTeam();

        if(redTeam.contains(p.getName())){
            redTeam.remove(p.getName());
            config.set("RedTeam", redTeam);
        }else{
            blueTeam.remove(p.getName());
            config.set("BlueTeam", blueTeam);
        }

        List<String> deadPlayers = config.getStringList("DeadPlayers");
        if(!deadPlayers.contains(p.getName())){
            deadPlayers.add(p.getName());
        }
        config.set("DeadPlayers", deadPlayers);
        plugin.saveConfig();
    }

    public static boolean isDeadPlayer(Player p){
        return config.getStringList("DeadPlayers").contains(p.getName());
    }

    public static boolean isPartOfLastHalf(Player p){
        if(p != null && p.isOnline()){
            return config.getStringList("LastHalf").contains(p.getName());
        }else{
            return false;
        }
    }

    public static boolean isOnTeam(Player p){
        if(!p.isOnline()){
            return false;
        }else{
            List<String> redTeam = getRedTeam();
            List<String> blueTeam = getBlueTeam();
            return redTeam.contains(p.getName()) || blueTeam.contains(p.getName());
        }
    }

    public static int getTotalPlayersCount(){
        return getRedTeam().size() + getBlueTeam().size();
    }

    public static void removeFromDeadList(Player player) {
        List<String> deadPlayers = config.getStringList("DeadPlayers");
        deadPlayers.remove(player.getName());
        config.set("DeadPlayers", deadPlayers);
        plugin.saveConfig();
    }
}
