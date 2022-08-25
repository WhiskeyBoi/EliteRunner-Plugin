package com.EliteRunners;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EliteRunnersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(EliteRunnersPlugin.class);
		RuneLite.main(args);
	}
}