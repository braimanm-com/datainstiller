package com.braimanm.datainstiller.test;

import com.braimanm.datainstiller.data.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;


public class TestGenerator {

	@XStreamAlias("data1")
	class LocalData extends DataPersistence {
		byte byte1;
		Byte byte2;
		short short1;
		Short short2;
		int int1;
		Integer int2;
		long long1;
		Long long2;
		float float1;
		Float float2;
		double double1;
		Double double2;
		boolean boolean1;
		Boolean boolean2;
		char char1;
		Character char2;
		@Data(value = "value", alias = "ALIAS")
		String string;
		Data2 data;
		List<String> list;
		Set<String> set;
	}

	
	@Test
	public void test_generators_and_aliases(){
		 LocalData test = new LocalData().fromResource("Data1.xml", true);
		 Assert.assertNull(test.getDataAliases());
		 Assert.assertTrue(test.string.matches("\\(\\d{3}\\) \\d{3}-\\d{4}"));
		 Assert.assertTrue(test.data.string.matches("\\d{2} \\D+ \\d{4}"));
		 for (String data : test.list){
			 Assert.assertTrue(data.matches("\\d{2} \\D+ \\d{4}"));
		 }
		 for (String data : test.set ){
			 Assert.assertTrue(data.matches("\\w+ \\w+"));
		 }
	}

	@Test // Visual validation
	public void test(){
		DataGenerator gen = new DataGenerator(new XStream());
		System.out.println(gen.getGenerator("ADDRESS").generate("{#} {S}, {T}, {O}, {K} ({P})",null));
		System.out.println(gen.getGenerator("ALPHANUMERIC").generate("{A}{B}{C}{D}(a)(A)(a)",null));
		System.out.println(gen.getGenerator("CUSTOM_LIST").generate(null, "aaa,bbb,ccc,ddd"));
		System.out.println(gen.getGenerator("DATE").generate("dd MMM yyyy", "2010/01/01|2013/12/31|yyyy/MM/dd"));
		System.out.println(gen.getGenerator("HUMAN_NAMES").generate("{M} and {F} {S}", null));
		System.out.println(gen.getGenerator("NUMBER").generate("##.000", "-100,100"));
		System.out.println(gen.getGenerator("WORD").generate("{a} {b}",null));

	}

	@Test // Visual validation
	public void testJexl() {
		Pers1 pers1 = new Pers1().fromResource("pers1.xml", true);
		System.out.println(pers1.toXML());
	}

}
