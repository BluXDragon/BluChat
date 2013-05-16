package com.bluxdragon.bluchat;

import net.canarymod.api.entity.living.humanoid.Player;

public class BluChatChannel {

	private boolean hidden;
	private boolean alwaysShown;
	private String displayName;
	private String permissionToSpeak;
	private String permissionToHear;
	private String channelChatFormat;
	
	public BluChatChannel(boolean isHidden, boolean isAlwaysShown, String showName, String speakPerm, String hearPerm, String newChatFormat){
		hidden = isHidden;
		alwaysShown = isAlwaysShown;
		displayName = showName.replace("%&", "§").replace("%_", " ");
		permissionToSpeak = speakPerm;
		permissionToHear = hearPerm;
		channelChatFormat = newChatFormat;
	}
	
	public boolean isChannelHidden(){
		return hidden;
	}
	public boolean isChannelAlwaysShown(){
		if (this.isChannelHidden())return false;
		return alwaysShown;
	}
	public String getDisplayName(){
		return displayName;
	}
	
	public String getRequiredSpeakPermission(){
		return permissionToSpeak;
	}
	
	public String getRequiredHearPermission(){
		return permissionToHear;
	}
	
	public String getChatFormat(){
		return channelChatFormat;
	}
	
	public boolean canBeSeenByPlayer(Player player){
		if (this.isChannelHidden() && !player.hasPermission("bluchat.channel.seehidden"))return false;
		if (this.isChannelAlwaysShown())return true;
		if (player.hasPermission(permissionToSpeak) || player.hasPermission(permissionToHear))return true;
		return false;
	}
	
}
