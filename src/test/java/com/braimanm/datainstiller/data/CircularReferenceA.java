package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CLASS-A")
public class CircularReferenceA extends DataPersistence {
    String someValue;
    CircularReferenceB classB;
}
