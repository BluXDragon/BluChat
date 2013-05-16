package com.bluxdragon.bluchat;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.plugin.Plugin;

public class BluChat extends Plugin{
	public static String name = "BluChat";

	public void disable() {
		Canary.help().unregisterCommands(this);
		getLogman().logInfo(name + " disabled.");
	}

	public boolean enable() {
		
		Canary.hooks().registerListener(new BluChatListener(), this);
		
		try {
			Canary.commands().registerCommands(new BluChatCommands(), this, true);
		} catch (CommandDependencyException e) {
			e.printStackTrace();
		}
		//getLogman().logInfo(name + " " + getVersion() + " by " + getAuthor() + " enabled.");
		return true;
	}
	
}

/*
Permissions
	bluchat.command.me
	bluchat.command.whisper
	bluchat.command.namechange
	bluchat.command.namechange.other
	bluchat.command.nickname
	bluchat.command.nickname.other
	bluchat.command.getname
	bluchat.command.ignore
	bluchat.channel //provides list, and tells you who can hear/speak in a channel
	bluchat.channel.seehidden
	bluchat.player.noise
	bluchat.player.cannotignore
	bluchat.player.colorchat
	bluchat.player.colorchat.3
*/