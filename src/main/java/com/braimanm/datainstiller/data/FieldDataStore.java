/*
Copyright 2010-2019 Michael Braiman braimanm@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.braimanm.datainstiller.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class FieldDataStore {
	private Map<Field,FieldData> fieldDataStore;
	private DataAliases aliases;
	
	FieldDataStore() {
		fieldDataStore = new HashMap<>();
		aliases = new  DataAliases();
	}
	
	public DataAliases getAliases() {
		return aliases;
	}

	public void setAliases(DataAliases aliases) {
		this.aliases.putAll(aliases);
	}
	
	private Field getFieldFromClass(Class<?> clasz, String fieldName){
		for (Field field : clasz.getDeclaredFields()){
			if (field.getName().equals(fieldName)){
				return field;
			}
		}
		return null;
	}
	
	public FieldData getData(Field field){
		return fieldDataStore.get(field);  
	}
	
	public FieldData getData(Class<?> clasz, String fieldName){
		Field field = getFieldFromClass(clasz, fieldName);
		if (field==null) {
			return null;
		}
		return getData(field);  
	}
	
	public void setData(Class<?> clasz, String fieldName, FieldData data){
		Field field = getFieldFromClass(clasz, fieldName);
		if (field!=null) {
			setData(field, data);
		} else {
			throw new RuntimeException("Field '" + fieldName + "' not exists in class '" + clasz.getSimpleName() + "'");
		}
	}

	public void setData(Field field, FieldData data) {
		data.setParentStore(this);
		fieldDataStore.put(field,data);
	}
	
	public boolean containsKey(Field key){
		return fieldDataStore.containsKey(key);
	}

}
