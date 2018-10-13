package com.hallowizer.unscrew.patcher;

import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Patcher {
	public void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: Patcher <task>");
			return;
		}
		
		String[] newArgs = new String[args.length-1];
		for (int i = 1; i < args.length; i++)
			newArgs[i-1] = args[i];
		
		Task task = Task.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		task.execute(newArgs);
	}
}
