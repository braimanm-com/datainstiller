package com.braimanm.datainstiller.data;

public class MoodType implements MoodInterface {
	String mood;
	String value;

	public MoodType() {
	}
	
	@Override
	public String getMood() {
		return mood;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void init(String mood, String value) {
		this.mood=mood;
		this.value=value;
	}
}
