package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("generic-data-consumer")
public class GenericDataConsumer extends DataPersistence {
    GenericData entity1;
    GenericData entity2;
    GenericData entiry3;
    GenericData entiry4;
    List<GenericData> entityList;
}
