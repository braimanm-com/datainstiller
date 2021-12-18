package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamConverter(MoodConverter.class)
public interface MoodInterface {
	String getMood();
	String getValue();
	void init(String mood, String value);
}
