package teamassign.teamassign.commands;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import sun.security.krb5.Config;
import teamassign.teamassign.ConfigManager;
import teamassign.teamassign.TeamAssign;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Commands implements CommandExecutor {

    private TeamAssign plugin;

    public Commands(TeamAssign plugin){
        this.plugin = plugin;
        plugin.getCommand("teamassign").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player pSender = (Player) sender;
            if(sender.hasPermission("ta.admin")){
                if (args[0].equalsIgnoreCase("assign") && args.length == 1) {
                    ConfigManager.assignRandTeams(pSender);
                } else if (args[0].equalsIgnoreCase("tp") && args.length == 1){
                    List<Player> allPlayers = ConfigManager.getAllTeamPlayers();

                    for(Player p : allPlayers){
                        try{
                            p.teleport(new Location(pSender.getWorld(), 242, 52, 222));
                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
                            LeatherArmorMeta lam = (LeatherArmorMeta) helm.getItemMeta();
                            lam.setUnbreakable(true);
                            if(ConfigManager.isBlueTeam(p)){
                                lam.setColor(Color.BLUE);
                            }else{
                                lam.setColor(Color.RED);
                            }
                            lam.addEnchant(Enchantment.BINDING_CURSE, 1, false);
                            helm.setItemMeta(lam);
                            p.getInventory().setHelmet(helm);
                        }catch(NullPointerException e){
                            continue;
                        }
                    }

                    new BukkitRunnable(){
                        int startingCount = ConfigManager.getTotalPlayersCount();
                        List<Player> playersAlive = new ArrayList<>();
                        @Override
                        public void run() {
                            int currentPlayerCount = ConfigManager.getTotalPlayersCount();
                            if(currentPlayerCount == startingCount / 2 && startingCount == 2){
                                teleportPlayersToLobby(ConfigManager.getAllTeamPlayers());
                                this.cancel();
                            }else if(currentPlayerCount == 0){
                                teleportPlayersToLobby(getLastHalf());
                                this.cancel();
                            }else if(currentPlayerCount == startingCount / 2){
                                setLastHalf();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 10L);
                }else if(args[0].equalsIgnoreCase("count") && args.length == 1){
                    pSender.sendMessage("There are " + ConfigManager.getTotalPlayersCount() + " players left!");
                }
            }else{
                pSender.sendMessage("[" + plugin.getName() + "] You do not have permisison to do that!");
            }
        }

        return false;
    }

    public void teleportPlayersToLobby(List<Player> playersStanding){
        for(Player p : playersStanding){
            try{
                p.teleport(new Location(Bukkit.getWorld("Spawn"), 252, 87, 302));
            }catch(NullPointerException e){
                continue;
            }
        }
    }

    public void setLastHalf(){
        List<String> lastHalf = new ArrayList<>();
        for(Player p : ConfigManager.getAllTeamPlayers()){
            lastHalf.add(p.getName());
        }
        plugin.getConfig().set("LastHalf", lastHalf);
        plugin.saveConfig();
    }

    public List<Player> getLastHalf(){
        List<String> lastHalf = plugin.getConfig().getStringList("LastHalf");
        List<Player> lastHalfPlayers = new ArrayList<>();
        for(String s : lastHalf){
            lastHalfPlayers.add(Bukkit.getPlayer(s));
        }
        return lastHalfPlayers;
    }

}
