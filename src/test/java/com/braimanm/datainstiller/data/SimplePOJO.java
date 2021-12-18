package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("unused")
@XStreamAlias("simple-pojo")
public class SimplePOJO extends DataPersistence {
	private byte bytePrimitive;
	private Byte byteType;
	private short shortPrimitive;
	private Short shortType;
	private int intPrimitive;
	private Integer integerType;
	private long longPrimitive;
	private Long longType;
	private float floatPrimitive;
	private Float floatType;
	private double doublePrimitive;
	private Double doubleType;
	private boolean booleanPrimitive;
	private Boolean booleanType;
	private char charPrimitive;
	private Character characterType;
	private String stringType;
}
