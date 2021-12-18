package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;

public class MoodConverter implements DataValueConverter {

	@Override
	public boolean canConvert(Class type) {
		return (MoodInterface.class.isAssignableFrom(type));
		
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		MoodInterface mood = (MoodInterface) source; 
		if (mood.getMood()!=null){
			writer.addAttribute("mood", mood.getMood());
		}
		if (mood.getValue()!=null){
			writer.setValue(mood.getValue());
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
		
		Object obj;
		try {
			obj = context.getRequiredType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		((MoodInterface) obj).init("mood", "value");
		return obj;
	}

	

	@Override
	public <T> T fromString(String str, Class<T> cls, Field field) {
		T obj;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		((MoodInterface) obj).init("mood", str);
		return obj;
	}

}
