package me.groot_23.pixel.player.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.language.LanguageApi;

public class GameTeam {

	// currently unused because of the char limit 16 of scoreboard names!
	private static final String SCOREBOARD_TEAM_NAME = "";

	private DyeColor color;
	private List<Player> players = new ArrayList<Player>();
	private boolean friendlyFire, colorVisible;

	public GameTeam(DyeColor color) {
		this(color, false, true);
	}
	
	public GameTeam(DyeColor color, boolean friendlyFire, boolean colorVisible) {
		this.color = color;
		this.friendlyFire = friendlyFire;
		this.colorVisible = colorVisible;
	}
	
	public DyeColor getColor() {
		return color;
	}
	
	public List<Player> getPlayers() {
		return players;
	}

	public String getName() {
		return SCOREBOARD_TEAM_NAME + color.name().toLowerCase();
	}
	
	public void addPlayer(Player player) {
		players.add(player);
		updateScoreboardTeams();
		player.setMetadata("pixel_team", new FixedMetadataValue(Pixel.getPlugin(), color.name().toLowerCase()));
	}

	public void removePlayer(Player player) {
		if(!players.contains(player)) return;
		
		for (Player p : players) {
			Team team = p.getScoreboard().getTeam(SCOREBOARD_TEAM_NAME);
			if(team != null) {
				team.removeEntry(player.getName());
			}
		}
		players.remove(player);
	}

	public List<Player> getPlayersAlive() {
		List<Player> alive = new ArrayList<Player>();
		for (Player player : players) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				alive.add(player);
			}
		}
		return alive;
	}
	
	public int getPlayersAliveCount() {
		int counter = 0;
		for (Player player : players) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				counter++;
			}
		}
		return counter;
	}
	
	public boolean isAlive() {
		for (Player player : players) {
			if (!Pixel.isSpectator(player) && player.isOnline()) {
				return true;
			}
		}
		return false;
	}

	private void updateScoreboardTeams() {
		for (Player player : players) {
			addTeamToScoreboard(player.getScoreboard());
		}
	}
	
	public void addTeamToScoreboard(Scoreboard scoreboard) {
		Team team = scoreboard.getTeam(getName());
		if (team == null) {
			team = scoreboard.registerNewTeam(getName());
			initTeamData(team);
		}
		initTeamData(team);
		for(Player player : players) {
			if (!team.getEntries().contains(player.getName())) {
				team.addEntry(player.getName());
			}
		}
	}

	private void initTeamData(Team team) {
		team.setAllowFriendlyFire(friendlyFire);
		if(colorVisible) {
			team.setPrefix(toChatColor(color) + "[" + LanguageApi.translateColorDefault(color) + "] "); 
		}
	}
	
	public static DyeColor getTeamOfPlayer(Player player) {
		List<MetadataValue> values = player.getMetadata("pixel_team");
		if(values != null) {
			for(MetadataValue val : values) {
				if(val.getOwningPlugin() == Pixel.getPlugin()) {
					return DyeColor.valueOf(val.asString().toUpperCase());
				}
			}
		}
		return null;
	}
	
	public static ChatColor toChatColor(DyeColor color) {
		switch(color) {
		case BLACK: return ChatColor.BLACK;
		case BLUE: return ChatColor.BLUE;
		case BROWN: return ChatColor.GOLD;
		case CYAN: return ChatColor.AQUA;
		case GRAY: return ChatColor.DARK_GRAY;
		case GREEN: return ChatColor.DARK_GREEN;
		case LIGHT_BLUE: return ChatColor.AQUA;
		case LIGHT_GRAY: return ChatColor.GRAY;
		case LIME: return ChatColor.GREEN;
		case MAGENTA: return ChatColor.LIGHT_PURPLE;
		case ORANGE: return ChatColor.GOLD;
		case PINK: return ChatColor.LIGHT_PURPLE;
		case PURPLE: return ChatColor.DARK_PURPLE;
		case RED: return ChatColor.RED;
		case WHITE: return ChatColor.WHITE;
		case YELLOW: return ChatColor.YELLOW;
		default: return null;
		}
		
	}
	
	public static Material toWool(DyeColor color) {
		return Material.getMaterial(color.name() + "_WOOL");
	}

}
