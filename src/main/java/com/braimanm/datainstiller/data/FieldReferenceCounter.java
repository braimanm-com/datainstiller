package com.braimanm.datainstiller.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class FieldReferenceCounter {
    private Map<String, Integer> fieldClasses;

    public FieldReferenceCounter() {
        fieldClasses = new HashMap<>();
    }

    private String getKey(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "." + field.getName();
    }

    public Integer getCounter(Field field) {
        String key = getKey(field);
        if (fieldClasses.containsKey(key)) {
            return fieldClasses.get(key);
        } else {
            fieldClasses.put(key, 0);
            return 0;
        }
    }

    public void incrementCounter(Field field) {
        int i = getCounter(field) + 1;
        fieldClasses.put(getKey(field), i);
    }

    public void reset(Field field) {
        fieldClasses.put(getKey(field), 0);
    }
}
