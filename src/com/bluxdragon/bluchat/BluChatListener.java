package com.bluxdragon.bluchat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.chat.TextFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.command.PlayerCommandHook;
import net.canarymod.hook.player.ChatHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.plugin.PluginListener;
import net.visualillusionsent.utils.PropertiesFile;
import net.visualillusionsent.utils.StringUtils;

public class BluChatListener implements PluginListener{

	public static HashMap<Player, String> playerNicks = new HashMap<Player, String>();
	public static HashMap<Player, String> playerDisps = new HashMap<Player, String>();
	public static HashMap<Player, ArrayList<String>> ignoreList = new HashMap<Player, ArrayList<String>>();
	public static HashMap<Player, String> playerChatColor = new HashMap<Player, String>();
	public static HashMap<String, BluChatChannel> chatChannels = new HashMap<String, BluChatChannel>();

	//props
	public static PropertiesFile props;
	public static char colorCode;
	public static String[] worldColors;
	public static String chatFormat; //%title %message %group %player %&# (p player, w world, cc chatcolour)
	public static String meFormat; //%title %message %group %player %&# (p player, w world, cc chatcolour)
	public static String whisperGetFormat; //%title %message %group %sender %receiver %&# (p player, w world, cc chatcolour)
	public static String whisperSendFormat; //%title %message %group %sender %receiver %&# (p player, w world, cc chatcolour)
	public static String defChatColor;
	//logProps
	public static boolean logCommands;
	public static boolean logChannel;
	public static boolean logWhisper;
	public static PropertiesFile logProps;
	public BluChatListener(){
		new java.io.File("plugins/config/BluChat/Groups/".replace("/", File.separator)).mkdirs();
		new java.io.File("plugins/config/BluChat/Players/".replace("/", File.separator)).mkdirs();
		props = new PropertiesFile("plugins/config/BluChat/BluChatSettings.properties".replace("/", File.separator));
		logProps = new PropertiesFile("plugins/config/BluChat/BluChatLogging.properties".replace("/", File.separator));
		//props
		colorCode = props.getString("Color-Code", "^").charAt(0);
		worldColors = props.getStringArray("World-Colors", new String[] {"f", "c", "5"});
		chatFormat = props.getString("Chat-Format", "%title%&w<%&p%player%&w> %&cc%message");
		meFormat = props.getString("Me-Format", "%&w*%&b%&o%player %message");
		whisperGetFormat = props.getString("Whisper-Get-Format", "%&d%sender: %message");
		whisperSendFormat = props.getString("Whisper-Send-Format", "%&dTo %receiver: %message");
		defChatColor = props.getString("Default-Chat-Color", "f");
		props.save();
		//logprops
		logCommands = logProps.getBoolean("Log-all-Player-Commands", false);
		logChannel = logProps.getBoolean("Log-Channel-Chat", true);
		logWhisper = logProps.getBoolean("Log-Player-Whispers", false);
		logProps.save();
		BluChatFunctions.Initialize();
	}
	
	@HookHandler
	public void onCommand(PlayerCommandHook hook){
		Player player = hook.getPlayer();
		String[] split = hook.getCommand();
		if (split.length < 2)return;
		split[0] = split[0].toLowerCase().replace("/", "");
		if (chatChannels.containsKey(split[0])){
			BluChatChannel bcp = chatChannels.get(split[0]);
			if (player.hasPermission(bcp.getRequiredSpeakPermission())){
				String pc = player.getPrefix();
				String wc = "§"+BluChatFunctions.getWorldColor(player.getWorld());
				String cc = "§"+BluChatFunctions.getPlayerChatColor(player);
				String nick = BluChatFunctions.getNickname(player);
				String title = BluChatFunctions.getPlayerTitle(player);
				String message = bcp.getChatFormat().replace("%player", nick).replace("%title", title).replace("%&p", pc).replace("%&w", wc).replace("%&cc", cc).replace("%&", "§").replace("%group", player.getGroup().getName()).replace("%message", StringUtils.joinString(split, " ", 1));
				//Color chat
				message = BluChatFunctions.formatCode(message, player);
				
				//Ignore List
				ArrayList<Player> recipients = Canary.getServer().getPlayerList();
				if (!player.hasPermission("bluchat.player.cannotignore")){
					for (Player ips : ignoreList.keySet()){
						for (String s : ignoreList.get(ips)){
							if (s.equals(player.getName().toLowerCase())){recipients.remove(ips); continue;}
						}
					}
				}
				//Channel
				//
				for (Player p : recipients){
					if (p.hasPermission(bcp.getRequiredHearPermission())){
						if (p == player)continue;
						p.sendMessage(message);
					}
				}
				player.sendMessage(message);
				if (logChannel)Canary.logInfo(TextFormat.removeFormatting(message));
				hook.setCanceled();
				return;
			}
		}
		if (logCommands){
			Canary.logInfo(player.getName()+" ["+StringUtils.joinString(split, " ", 0)+"]");
		}
	}
	
	@HookHandler
	public void onLogin(ConnectionHook hook){
		Player p = hook.getPlayer();
		if (BluChatFunctions.playerHasDisplayName(p)){
			p.setDisplayName(BluChatFunctions.getDisplayName(p));
		}
	}
	
	@HookHandler
	public void onChat(ChatHook hook){
		Player player = hook.getPlayer();
		String pc = player.getPrefix();
		String wc = "§"+BluChatFunctions.getWorldColor(player.getWorld());
		String cc = "§"+BluChatFunctions.getPlayerChatColor(player);
		String message = hook.getMessage();
		String nick = BluChatFunctions.getNickname(player);
		String title = BluChatFunctions.getPlayerTitle(player);
		
		//Color chat
		message = BluChatFunctions.formatCode(message, player);
		
		//Ignore List
		ArrayList<Player> recipients = Canary.getServer().getPlayerList();
		if (!player.hasPermission("bluchat.player.cannotignore")){
			for (Player ips : ignoreList.keySet()){
				for (String s : ignoreList.get(ips)){
					if (s.equals(player.getName().toLowerCase())){recipients.remove(ips); continue;}
				}
			}
		}
		//Channel
		hook.setReceiverList(recipients);
		
		//Building the message
		hook.setFormat(chatFormat.replace("%player", nick).replace("%title", title).replace("%&p", pc).replace("%&w", wc).replace("%&cc", cc).replace("%&", "§"));
		hook.setMessage(message);
		
		//Noise!
		/*for (Player p : recipients){
			if (p.hasPermission("bluchat.player.noise")){
			}
		}*/
	}
}
