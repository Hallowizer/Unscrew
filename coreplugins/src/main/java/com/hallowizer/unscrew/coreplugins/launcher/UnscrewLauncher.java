package com.hallowizer.unscrew.coreplugins.launcher;

import java.util.ArrayList;
import java.util.Arrays;

import com.hallowizer.modwrapper.api.ModWrapper;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnscrewLauncher {
	@SneakyThrows
	public void main(String[] args) {
		ModWrapper.launch(new ArrayList<>(Arrays.asList(args)), "com.hallowizer.unscrew.coreplugins.launcher.UnscrewModLoader");
	}
}
