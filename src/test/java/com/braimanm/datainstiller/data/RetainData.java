package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retain")
public class RetainData extends DataPersistence {
    public byte b;
    public short sh;
    public int i;
    public long l;
    public float f;
    public double d;
    public char c;
    public boolean bo;
    public String s;
    public byte bb = 1;
    public short shsh = 2;
    public int ii = 3;
    public long ll = 4L;
    public float ff = 0.1f;
    public double dd = 0.2d;
    public char cc = 'A';
    public boolean bobo = true;
    public String ss = "String";

    @Data(skip = true)
    public byte bskip = 45;
    public static byte bstat = 55;
    public static final byte bstatfinal = 65;

}
