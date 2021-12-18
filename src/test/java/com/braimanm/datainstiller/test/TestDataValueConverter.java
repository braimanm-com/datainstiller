package com.braimanm.datainstiller.test;

import java.util.ArrayList;
import java.util.List;

import com.braimanm.datainstiller.data.*;
import com.thoughtworks.xstream.XStream;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDataValueConverter {

	class LocalData extends DataPersistence {
		MoodData1 mood1;
		MoodData1 mood11;
		MoodData2 mood2;
	}
	
	@Test
	public void testMood(){
		List<DataValueConverter> converters = new ArrayList<>();
		converters.add(new MoodConverter());
		DataGenerator gen = new DataGenerator(new XStream(), converters);
		LocalData testData = gen.generate(LocalData.class);
		String xml = testData.toXML();
		Assert.assertTrue(xml.contains("<mood1 mood=\"mood\">mood1</mood1>"));
		Assert.assertTrue(xml.contains("<mood11 mood=\"mood\">mood11</mood11>"));
		Assert.assertTrue(xml.contains("<mood2 mood=\"mood\">mood2</mood2>"));
	}
	

}
