package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("unused")
@XStreamAlias("array-simple-pojo")
public class SimplePOJOArray extends DataPersistence {
	@Data(alias = "bytePrimitive", value = "0")
	private byte[] bytePrimitive;
	@Data(alias = "byteType", value = "1")
	private Byte[] byteType;
	@Data(alias = "shortPrimitive", value = "2")
	private short[] shortPrimitive;
	@Data(alias = "shortType", value = "3")
	private Short[] shortType;
	@Data(alias = "intPrimitive", value = "4", nArray = 4)
	private int[] intPrimitive;
	@Data(alias = "integerType", value = "5", nArray = 5)
	private Integer[] integerType;
	@Data(alias = "longPrimitive", value = "6")
	private long[] longPrimitive;
	@Data(alias = "longType", value = "7")
	private Long[] longType;
	@Data(alias = "floatPrimitive", value = "8")
	private float[] floatPrimitive;
	@Data(alias = "floatType", value = "9")
	private Float[] floatType;
	@Data(alias = "doublePrimitive", value = "10")
	private double[] doublePrimitive;
	@Data(alias = "doubleType", value = "11")
	private Double[] doubleType;
	@Data(alias = "booleanTrue", value = "true")
	private boolean[] booleanPrimitive;
	@Data(alias = "booleanTrue")
	private Boolean[] booleanType;
	@Data(alias = "charPrimitive", value = "\u0061")
	private char[] charPrimitive;
	@Data(alias = "characterType", value = "a")
	private Character[] characterType;
	@Data(alias = "stringType", value = "stringType")
	private String[] stringType;

}
