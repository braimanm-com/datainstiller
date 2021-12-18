package com.braimanm.datainstiller.data;

public class Data2 extends DataPersistence {
	public byte byte1;
	public Byte byte2;
	public short short1;
	public Short short2;
	public int int1;
	public Integer int2;
	public long long1;
	public Long long2;
	public float float1;
	public Float float2;
	public double double1;
	public Double double2;
	public boolean boolean1;
	public Boolean boolean2;
	public char char1;
	public Character char2;
	@Data(value = "value2", alias = "ALIAS2")
	public String string;
	public Data4 data4;
	public Data3 data3;

	public static class Data4 extends DataPersistence {
		public String string;
	}

	public class Data3 extends DataPersistence {
		public String string;
	}
	
	
	
}
