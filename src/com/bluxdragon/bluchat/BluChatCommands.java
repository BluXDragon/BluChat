package com.bluxdragon.bluchat;

import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.blocks.CommandBlock;
import net.canarymod.api.world.blocks.ComplexBlock;
import net.canarymod.chat.Colors;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.TextFormat;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.utils.StringUtils;

public class BluChatCommands implements CommandListener{
	
	//chatFormat.replace("%player", nick).replace("%title", title).replace("%&p", pc).replace("%&w", wc).replace("%&c", cc).replace("%&", "§")
	@Command(aliases = { "channel" },
            description = "Channel info",
            permissions = { "bluchat.channel" },
            toolTip = "/channel [channelname]",
            min = 1)
	public void channel(MessageReceiver caller, String[] split){
		if (caller instanceof Player){
			Player p = ((Player) caller).getPlayer();
			if (split.length == 1){
				p.sendMessage(" ");
				p.sendMessage(Colors.YELLOW+"List of Channels");
				String m = "";
				for (String s : BluChatListener.chatChannels.keySet()){
					BluChatChannel bcp = BluChatListener.chatChannels.get(s);
					if (bcp.canBeSeenByPlayer(p))m = m+bcp.getDisplayName()+", ";
				}
				if (m.endsWith(", "))m = m.substring(0, m.length()-2);
				p.sendMessage(m);
				p.sendMessage(" ");
			}else{
				for (String s : BluChatListener.chatChannels.keySet()){
					BluChatChannel bcp = BluChatListener.chatChannels.get(s);
					if (bcp.canBeSeenByPlayer(p)){
						if (split[1].equalsIgnoreCase(bcp.getDisplayName())){
							//p.sendMessage(Colors.LIGHT_GREEN+"Channel "+bcp.getDisplayName());
							/*if (p.hasPermission(bcp.getRequiredHearPermission())){
								p.sendMessage("You can hear messages in this channel!");
							}else{
								p.sendMessage("You can §cNOT§f hear messages in this channel!");
							}
							if (p.hasPermission(bcp.getRequiredSpeakPermission())){
								p.sendMessage("You can speak in this channel!");
								p.sendMessage("§7/"+s+" <message>§f to speak in this channel.");
							}else{
								p.sendMessage("You can §cNOT§f speak in this channel!");
							}
							p.sendMessage(" ");*/
							p.sendMessage(" ");
							p.sendMessage(TextFormat.YELLOW+TextFormat.BOLD+"Players involved "+bcp.getDisplayName());
							p.sendMessage(Colors.YELLOW+"[S] - Can Speak, [H] - Can Hear");
							for (Player player : Canary.getServer().getPlayerList()){
								String m = player.getName() + "§7 ";
								boolean send = false;
								if (player.hasPermission(bcp.getRequiredSpeakPermission())){send = true; m = m + "[S] ";}
								if (player.hasPermission(bcp.getRequiredHearPermission())){send = true; m = m + "[H] ";}
								if (send)p.sendMessage(m);
							}
							p.sendMessage(" ");
							return;
						}
					}
				}
				p.sendMessage(Colors.YELLOW+"Invalid channel name! \"/channels\" for a list.");
			}
		}
	}

	//@Command(aliases = { "me" },
	@Command(aliases = { "mee" },
            description = "Perform an action! Emote! Whatever!",
            permissions = { "bluchat.command.me" },
            toolTip = "/me <message>",
            min = 2)
	public void me(MessageReceiver caller, String[] split){
		if (caller instanceof Player){
			//Gets
			Player player = ((Player) caller).getPlayer();
			ArrayList<Player> recipients = Canary.getServer().getPlayerList();
			String nick = BluChatFunctions.getNickname(player);
			String pc = player.getPrefix();
			String wc = "§"+BluChatFunctions.getWorldColor(player.getWorld());
			String cc = "§"+BluChatFunctions.getPlayerChatColor(player);
			String title = BluChatFunctions.getPlayerTitle(player);
			
			//Ignore List
			if (!player.hasPermission("bluchat.player.cannotignore")){
				for (Player ips : BluChatListener.ignoreList.keySet()){
					for (String s : BluChatListener.ignoreList.get(ips)){
						if (s.equals(player.getName().toLowerCase()))recipients.remove(ips);
					}
				}
			}
			
			//Build Message
			String message = StringUtils.joinString(split, " ", 1);
			message = BluChatFunctions.formatCode(message, player);
			String sendMessage = BluChatListener.meFormat.replace("%player", nick).replace("%title", title).replace("%&p", pc).replace("%&w", wc).replace("%&cc", cc).replace("%message", message).replace("%&", "§");
			
			//Send!
			for (Player p : recipients){
				p.sendMessage(sendMessage);
			}
			
		}
	}

	//@Command(aliases = { "w", "whisper", "msg", "tell" },
	@Command(aliases = { "w", "whisper" },
            description = "Send a private message to someone",
            helpLookup = "w",
            permissions = { "bluchat.command.whisper" },
            toolTip = "/w <recipient player> <message>",
            min = 3)
	public void whisper(MessageReceiver caller, String[] split){
		
		Player targetPlayer = Canary.getServer().matchPlayer(split[1]);
		if (targetPlayer == null){
			caller.message(Colors.LIGHT_RED+"Invalid player! Please specify an online player.");
			return;
		}
		
		String recipientNick = BluChatFunctions.getNickname(targetPlayer);

		String nick;// = BluChatFunctions.getNickname(player);
		String pc;// = player.getPrefix();
		String wc;// = "§"+BluChatFunctions.getWorldColor(player.getWorld());
		String cc;// = "§"+BluChatFunctions.getPlayerChatColor(player);
		String title;// = BluChatFunctions.getPlayerTitle(player);

		String message = StringUtils.joinString(split, " ", 2);
		
		if (caller instanceof Player){
			Player player = ((Player) caller).getPlayer();
			
			//Ignore List
			if (BluChatListener.ignoreList.containsKey(targetPlayer) && !player.hasPermission("bluchat.player.cannotignore")){
				for (String ilps : BluChatListener.ignoreList.get(targetPlayer)){
					if (ilps.toLowerCase().equals(player.getName().toLowerCase())){
						player.sendMessage(Colors.LIGHT_RED+"That player has you ignored!");
						return;
					}
				}
			}
			
			nick = BluChatFunctions.getNickname(player);
			pc = player.getPrefix();
			wc = "§"+BluChatFunctions.getWorldColor(player.getWorld());
			cc = "§"+BluChatFunctions.getPlayerChatColor(player);
			title = BluChatFunctions.getPlayerTitle(player);
			
			message = BluChatFunctions.formatCode(message, player);
		}else if (caller instanceof CommandBlock){
			nick = "CommandBlock";
			pc = "§f";
			wc = "§"+BluChatFunctions.getWorldColor(((ComplexBlock) caller).getWorld());
			cc = "§f";
			title = "";
		}else{
			nick = "Server";
			pc = "§f";
			wc = "§f";
			cc = "§f";
			title = "";
		}
		
		//%title %message %group %sender %receiver %&# (p player, w world, cc chatcolour)
		
		String sendMessage = BluChatListener.whisperSendFormat.replace("%sender", nick).replace("%receiver", recipientNick).replace("%title", title).replace("&p", pc).replace("&w", wc).replace("&cc", cc).replace("%message", message).replace("%&", "§");
		String getMessage = BluChatListener.whisperGetFormat.replace("%sender", nick).replace("%receiver", recipientNick).replace("%title", title).replace("&p", pc).replace("&w", wc).replace("&cc", cc).replace("%message", message).replace("%&", "§");
		
		//Send!
		targetPlayer.sendMessage(getMessage);
		caller.message(sendMessage);
		
		//Log
		if (BluChatListener.logWhisper)Canary.logInfo(TextFormat.removeFormatting(sendMessage));
	}
	
	@Command(aliases = { "namechange", "displayname" },
            description = "Change your chat and display name",
            permissions = { "bluchat.command.namechange", "bluchat.command.namechange.other" },
            toolTip = "/namechange <new name> [target player]",
            min = 2)
	public void namechange(MessageReceiver caller, String[] split){
		Player targetPlayer = null;
		if (split.length == 3){
			if (caller instanceof Player){
				Player p = ((Player) caller).getPlayer();
				if (!p.hasPermission("bluchat.command.namechange.other")){
					caller.message(Colors.LIGHT_RED+"You do not have permission to do that to other players!");
					return;
				}
			}
			targetPlayer = Canary.getServer().matchPlayer(split[2]);
		}else{
			if (caller instanceof Player)targetPlayer = ((Player) caller).getPlayer();
		}
		
		if (targetPlayer == null){
			caller.message(Colors.LIGHT_RED+"Invalid target player.");
		}
		
		String newName = split[1].replace(BluChatListener.colorCode, '§');

		if (!BluChatFunctions.isValidNickname(targetPlayer, newName)){
			caller.message(Colors.YELLOW+"A player with that nickname already exists!");
			return;
		}
		if (!BluChatFunctions.isValidDisplayName(targetPlayer, newName)){
			caller.message(Colors.YELLOW+"A player with that display name already exists!");
			return;
		}
		
		BluChatFunctions.setNickname(targetPlayer, newName, true);
		BluChatFunctions.setDisplayName(targetPlayer, newName, true);
		caller.message(Colors.YELLOW+targetPlayer.getName()+"'s new display and nickname is "+newName+"!");
	}
	
	@Command(aliases = { "nickname", "nick" },
            description = "Change your chat name",
            permissions = { "bluchat.command.nickname", "bluchat.command.nickname.other" },
            toolTip = "/nick <new nickname> [target player]",
            min = 2)
	public void nickname(MessageReceiver caller, String[] split){
		Player targetPlayer = null;
		if (split.length == 3){
			if (caller instanceof Player){
				Player p = ((Player) caller).getPlayer();
				if (!p.hasPermission("bluchat.command.nickname.other")){
					caller.message(Colors.LIGHT_RED+"You do not have permission to do that to other players!");
					return;
				}
			}
			targetPlayer = Canary.getServer().matchPlayer(split[2]);
		}else{
			if (caller instanceof Player)targetPlayer = ((Player) caller).getPlayer();
		}
		
		if (targetPlayer == null){
			caller.message(Colors.LIGHT_RED+"Invalid target player.");
		}
		
		String newName = split[1].replace(BluChatListener.colorCode, '§');
		
		if (!BluChatFunctions.isValidNickname(targetPlayer, newName)){
			caller.message(Colors.YELLOW+"A player with that nickname already exists!");
			return;
		}
		
		BluChatFunctions.setNickname(targetPlayer, newName, true);
		caller.message(Colors.YELLOW+targetPlayer.getName()+"'s new nickname is "+newName+"!");
	}
	
	@Command(aliases = { "getname" },
            description = "Get the display/real name of a nicknamed player",
            permissions = { "bluchat.command.getname" },
            toolTip = "/getname <Taret player's nickname>",
            min = 2)
	public void getNickname(MessageReceiver caller, String[] split){
		Player targetPlayer = null;
		for (Player p : BluChatListener.playerNicks.keySet()){
			if (BluChatListener.playerNicks.get(p).equalsIgnoreCase(split[1])){
				targetPlayer = p;
				break;
			}
		}
		
		if (targetPlayer == null){
			caller.message(Colors.LIGHT_RED+"Invalid target nickname.");
			return;
		}
		
		caller.message(Colors.YELLOW+BluChatListener.playerNicks.get(targetPlayer)+"'s real name is "+targetPlayer.getName());
		if (BluChatFunctions.playerHasDisplayName(targetPlayer))caller.message(Colors.YELLOW+BluChatListener.playerNicks.get(targetPlayer)+"'s display name is "+targetPlayer.getDisplayName());
	}
	
	@Command(aliases = { "ignore" },
            description = "Ignore a player. Case insensitive.",
            permissions = { "bluchat.command.ignore" },
            toolTip = "/ignore <playername>",
            min = 2)
	public void ignorePlayer(MessageReceiver caller, String[] split){
		if (caller instanceof Player){
			Player player = ((Player) caller).getPlayer();
			String ignoredPlayer = null;
			Player tp = Canary.getServer().matchPlayer(split[1]);
			if (tp == null){
				ignoredPlayer = split[1].toLowerCase();
			}else{
				ignoredPlayer = tp.getName().toLowerCase();
			}
			
			ArrayList<String> ignored = new ArrayList<String>();
			if (BluChatListener.ignoreList.containsKey(player)){
				ignored = BluChatListener.ignoreList.get(player);
				if (ignored.contains(ignoredPlayer)){
					ignored.remove(ignoredPlayer);
					player.sendMessage(Colors.YELLOW+ignoredPlayer+" has been removed from your ignore list.");
					return;
				}
			}else{
				Player ip = Canary.getServer().getPlayer(ignoredPlayer);
				if (ip != null){
					if (ip.hasPermission("bluchat.player.cannotignore")){
						player.sendMessage(Colors.LIGHT_RED+"You cannot ignore that player!");
						return;
					}
				}
				ignored.add(ignoredPlayer);
			}
			BluChatListener.ignoreList.put(player, ignored);
			player.sendMessage(Colors.YELLOW+ignoredPlayer+" has been added to your ignore list.");
		}
	}
	
}
