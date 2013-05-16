package com.bluxdragon.bluchat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.DimensionType;
import net.canarymod.api.world.World;
import net.canarymod.chat.TextFormat;
import net.canarymod.user.Group;
import net.visualillusionsent.utils.PropertiesFile;

public class BluChatFunctions extends BluChatListener{

	public static ArrayList<Character> validCodes = new ArrayList<Character>();
	public static HashMap<Player, String> titles = new HashMap<Player, String>();
	public static HashMap<Player, Group> playerGroups = new HashMap<Player, Group>();
	public static PropertiesFile chatColorPlayerProps = new PropertiesFile("plugins/config/BluChat/Players/PlayerChatColors.txt".replace("/", File.separator));
	public static PropertiesFile chatColorGroupProps = new PropertiesFile("plugins/config/BluChat/Groups/GroupChatColors.txt".replace("/", File.separator));
	public static PropertiesFile chatTitlePlayerProps = new PropertiesFile("plugins/config/BluChat/Players/PlayerTitles.txt".replace("/", File.separator));
	public static PropertiesFile chatTitleGroupProps = new PropertiesFile("plugins/config/BluChat/Groups/GroupTitles.txt".replace("/", File.separator));
	
	public static void Initialize(){
		for (char c : "0123456789abcdefklmnorpw".toCharArray()){
			validCodes.add(c);
		}
		chatColorPlayerProps.save();
		chatColorGroupProps.save();
		chatTitlePlayerProps.save();
		chatTitleGroupProps.save();
		/*PropertiesFile channelProps = new PropertiesFile("plugins/config/BluChat/Channels.txt".replace("/", File.separator));
		Map<String, String> map = channelProps.getPropertiesMap();
		for (String s : map.keySet()){
			BluChatChannel bcp = new BluChatChannel(channelProps.getBoolean(s, false), );
			chatChannels.put(s, bcp);
		}
		channelProps.save();*/
		
		File[] fList = new File("plugins/config/BluChat/Channels/").listFiles(new FilenameFilter() { 
	         public boolean accept(File dir, String filename)
            { return filename.endsWith(".txt"); }
		} );
		for (File f : fList){
			PropertiesFile pf = new PropertiesFile(f.getAbsolutePath());
			String pfName = f.getName().replace(".txt", "");
			String chatAlias = pf.getString("Alias-To-Chat-Here", pfName).toLowerCase();
			boolean hide = pf.getBoolean("Is-Channel-Hidden", false);
			boolean shown = pf.getBoolean("Channel-Always-Shown", false);
			String displayName = pf.getString("Display-Name", pfName);
			String speakPerm = pf.getString("Permission-To-Speak", "bluchat.channel."+chatAlias);
			String listenPerm = pf.getString("Permission-To-Listen", "bluchat.channel."+chatAlias);
			String newChatFormat = pf.getString("Chat-Format", chatFormat);
			BluChatChannel bcp = new BluChatChannel(hide, shown, displayName, speakPerm, listenPerm, newChatFormat);
			chatChannels.put(chatAlias, bcp);
			pf.save();
		}
	}
	
	public static String getPlayerChatColor(Player player){
		String cc = defChatColor;
		if (playerChatColor.containsKey(player)){
			cc = playerChatColor.get(player);
		}else{
			if (chatColorPlayerProps.containsKey(player.getName())){
				cc = chatColorPlayerProps.getString(player.getName());
				playerChatColor.put(player, cc);
			}else if (chatColorGroupProps.containsKey(player.getGroup().getName())){
				cc = chatColorGroupProps.getString(player.getGroup().getName());
				playerChatColor.put(player, cc);
			}
		}
		return cc;
	}
	
	public static String getPlayerTitle(Player player){
		if (titles.containsKey(player)){
			if (playerGroups.containsKey(player)){
				if (playerGroups.get(player) == player.getGroup())return titles.get(player);
			}
		}
		String title = "";
		if (chatTitlePlayerProps.containsKey(player.getName())){
			title = chatTitlePlayerProps.getString(player.getName());
		}else if (chatTitleGroupProps.containsKey(player.getGroup().getName())){
			title = chatTitleGroupProps.getString(player.getGroup().getName());
		}
		title = title.replace("%_", " ");
		titles.put(player, title);
		playerGroups.put(player, player.getGroup());
		return title;
	}
	
	public static String getWorldColor(World world){
		String wc = worldColors[0];
		if (world.getType() == DimensionType.fromName("NETHER"))wc = worldColors[1];
		if (world.getType() == DimensionType.fromName("END"))wc = worldColors[2];
		return wc;
	}
	
	public static boolean playerHasNickname(Player player){
		if (playerNicks.containsKey(player)){
			if (!playerNicks.get(player).equals(player.getName()))return true;
		}
		return false;
	}
	
	public static String getNickname(Player player){
		String nn = player.getName();
		if (!playerHasNickname(player))return nn;
		if (playerHasNickname(player))nn = playerNicks.get(player);
		return nn;
	}
	
	public static void setNickname(Player targetPlayer, String newName, boolean removeFormatting){
		if (removeFormatting){
			BluChatListener.playerNicks.put(targetPlayer, TextFormat.removeFormatting(newName));
		}else{
			BluChatListener.playerNicks.put(targetPlayer, newName);
		}
		Canary.logInfo(targetPlayer.getName()+"'s nickname is now "+TextFormat.removeFormatting(newName));
	}
	
	public static boolean isValidNickname(Player player, String newNick){
		for (String s : playerNicks.values()){
			if (TextFormat.removeFormatting(s).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
		}
		for (Player p : Canary.getServer().getPlayerList()){
			if (p == player)continue;
			if (TextFormat.removeFormatting(p.getName()).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
			if (TextFormat.removeFormatting(p.getDisplayName()).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
		}
		return true;
	}
	
	public static String getDisplayName(Player player){
		String nn = player.getName();
		if (!playerHasDisplayName(player))return nn;
		if (playerHasNickname(player))nn = playerDisps.get(player);
		return nn;
	}
	
	public static void setDisplayName(Player targetPlayer, String newName, boolean removeFormatting){
		if (newName.equals(targetPlayer.getName())){
			playerDisps.remove(targetPlayer);
			targetPlayer.setDisplayName(targetPlayer.getName());
			return;
		}
		if (removeFormatting){
			BluChatListener.playerDisps.put(targetPlayer, TextFormat.removeFormatting(newName));
			targetPlayer.setDisplayName(TextFormat.removeFormatting(newName));
		}else{
			BluChatListener.playerDisps.put(targetPlayer, newName);
			targetPlayer.setDisplayName(newName);
		}
		Canary.logInfo(targetPlayer.getName()+"'s display name is now "+TextFormat.removeFormatting(newName));
	}
	
	public static boolean playerHasDisplayName(Player player){
		if (playerDisps.containsKey(player)){
			if (!playerDisps.get(player).equals(player.getName()))return true;
		}
		return false;
	}
	
	public static boolean isValidDisplayName(Player player, String newNick){
		for (String s : playerDisps.values()){
			if (TextFormat.removeFormatting(s).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
		}
		for (Player p : Canary.getServer().getPlayerList()){
			if (p == player)continue;
			if (TextFormat.removeFormatting(p.getName()).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
			if (TextFormat.removeFormatting(p.getDisplayName()).equalsIgnoreCase(TextFormat.removeFormatting(newNick)))return false;
		}
		return true;
	}
	
	public static String formatCode(String message, Player player){
		if (player.hasPermission("bluchat.player.colorchat")){
			
			char[] array = message.toCharArray();
			for (int i = 0; i < array.length-1; i++){
				if (array[i] == colorCode){
					if (!validCodes.contains(array[i+1]))continue;
					if (player.hasPermission("bluchat.player.colorchat."+array[i+1])){
						array[i] = '§';
						if (array[i+1] == 'w')array[i+1] = BluChatFunctions.getWorldColor(player.getWorld()).charAt(0);
						if (array[i+1] == 'p')array[i+1] = player.getPrefix().charAt(1);
					}
				}
			}
			message = new String(array);
			
		}
		return message;
	}
	
}
