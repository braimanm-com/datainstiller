package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("pers1")
public class Pers1 extends DataPersistence {
    @Data(alias = "string1")
    String s1;
    String s2;
    Pers2 pers2;
}
