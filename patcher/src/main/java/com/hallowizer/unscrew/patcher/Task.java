package com.hallowizer.unscrew.patcher;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Task {
	UNPATCHED(UnpatchedSource::generate);
	
	private final Consumer<String[]> task;
	
	public void execute(String[] args) {
		task.accept(args);
	}
}
