/*
Copyright 2010-2021 Michael Braiman braimanm@gmail.com

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

import com.braimanm.datainstiller.generators.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.CharArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.converters.enums.EnumSetConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class DataGenerator {
	private int nArray = 3;
	private int recursionLevel = 2;
	private final FieldReferenceCounter recursionCounter = new FieldReferenceCounter();
	private final FieldDataStore fieldDataStore = new FieldDataStore();
	private final Map<String, GeneratorInterface> generatorStore = new HashMap<>();
	private final XStream xstream;

	public DataGenerator(XStream xstream) {
		this(xstream, null);
	}
	
	public DataGenerator(XStream xstream, List<DataValueConverter> converters){
		this.xstream = xstream;
		this.xstream.addPermission(AnyTypePermission.ANY);
		registerGenerator("ADDRESS", new AddressGenerator());
		registerGenerator("ALPHANUMERIC", new AlphaNumericGenerator());
		registerGenerator("CUSTOM_LIST",new CustomListGenerator());
		registerGenerator("DATE",new DateGenerator());
		registerGenerator("HUMAN_NAMES",new HumanNameGenerator());
		registerGenerator("WORD",new WordGenerator());
		registerGenerator("NUMBER",new NumberGenerator());
		registerGenerator("FILE2LIST",new File2ListGenerator());
        if (converters != null) {
            for (Converter converter : converters){
            	xstream.registerConverter(converter);
            }
        }
	}
	
	public GeneratorInterface getGenerator(String generator){
		return  generatorStore.get(generator);
	}
	
	public void registerGenerator(String key, GeneratorInterface generator) {
		generatorStore.put(key, generator);
	}
		
	public int getRecursionLevel() {
		return recursionLevel;
	}

	public void setRecursionLevel(int recursionLevel) {
		if (recursionLevel <= 0) {
			this.recursionLevel = 1;
		} else {
			this.recursionLevel = recursionLevel;
		}
	}

	public int getnArray() {
		return nArray;
	}

	public void setnArray(int n) {
		if (n <= 0) {
			this.nArray = 1;
		} else {
			this.nArray = n;
		}
	}

	private int getnArrayForField(Field field) {
		int n = nArray;
		FieldData fieldData = fieldDataStore.getData(field);
		if (fieldData != null && fieldData.nArray() > 0) {
			n = fieldData.nArray();
		}
		return n;
	}

	public Object deepCopy(Object source){
		String xml = xstream.toXML(source);
		return xstream.fromXML(xml);
	}

	private String getGeneratedValue(FieldData fieldData){
		String value = fieldData.value();
		String alias = fieldData.alias();
        String aliasValue = (alias != null) ? fieldDataStore.getAliases().getAsString(alias) : null;

        if (aliasValue != null) {
            return "${" + alias + "}";
        }

        if (fieldData.generatorType() != null) {
            GeneratorInterface generator = generatorStore.get(fieldData.generatorType());
            if (generator != null) {
                value = generator.generate(fieldData.pattern(), fieldData.value());
            } else {
				throw new GeneratorNotFoundException("Generator " + fieldData.generatorType() + " was not found!");
			}
		}
		
		if (alias!=null) {
			fieldDataStore.getAliases().put(alias, value);
			return "${" + alias + "}";
		}

		return value;
	}

    private String generateValueForField(Class<?> cls, Field field) {
        String returnValue = null;
        FieldData fieldData = fieldDataStore.getData(field);
        if (fieldData != null) {
            returnValue = getGeneratedValue(fieldData);
        }
		if (cls.isArray()){
			cls = cls.getComponentType();
		}
		if (cls.isPrimitive() || Primitives.unbox(cls) !=null || cls.isEnum()) {
            if (returnValue == null || returnValue.isEmpty()) {
            	if (cls.equals(char.class)) {
            		return "";
				} else {
					return "0";
				}
            }
            if (returnValue.startsWith("${")) {
                return fieldData.resolveAlias();
			}
		} 
		if (cls.equals(Date.class)){
			String defaultPattern = "yyyy-MM-dd HH:mm:ss.S z";
			return new SimpleDateFormat(defaultPattern).format(new Date());
		}

        if (returnValue == null) {
            return field.getName();
        }
			
		return returnValue;
	}
	
	private boolean isInnerClass(Class<?> cls){
		if (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())){
			System.err.println("[WARNING] Only static nested classes are supported. Class " + cls.getCanonicalName() +" should be declared as static!");
			return true;
		} 
		return false;
	}
	
	private void processAnnotations(Class<?> clasz){
		MetaData metaData = clasz.getAnnotation(MetaData.class);
        if (metaData != null) {
            for (Data data : metaData.value()) {
                Class<?> cls = clasz;
                if (data.fieldClass() != void.class) {
                    cls = data.fieldClass();
                }
				if (data.fieldName().trim().isEmpty()){
					throw new AnnotationProcessingException("Field 'fieldName' must be provided in MetaData annotation" + data);
				}
				fieldDataStore.setData(cls, data.fieldName(), new FieldData(data));
			}
		}
		Class<?> superClasz = clasz;
		do {
			for (Field field : superClasz.getDeclaredFields()){
				Data data = field.getAnnotation(Data.class);
                if (data == null) {
                    continue;
                }
				if (!fieldDataStore.containsKey(field)){
				    fieldDataStore.setData(field, new FieldData(data));
				}
			}
			superClasz = superClasz.getSuperclass();
        } while (superClasz != null);
    }

    @SuppressWarnings("restriction")
	private Class<?> getGenericTypeOrString(Field field,int argumentNum){
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType){
			Type realType = ((ParameterizedType)type).getActualTypeArguments()[argumentNum];
			if (!(realType instanceof WildcardType)){
				if (realType instanceof ParameterizedType){
					System.err.println("[WARNING] Collection of collection is not supported in field " + field);
					return null;
				} else {
					return (Class<?>) realType;
				}
			}
		}
		return String.class;
	}

	public <T> T generate(Class<T> cls) {

		T obj = generate(cls, null);
		
		if (fieldDataStore.getAliases().size()>0 ){
			boolean dataAliasesFound = false;
			Class<?> clz = cls;
			do {
				for (Field field : clz.getDeclaredFields()) {
					if (field.getType().equals(DataAliases.class)){
						try {
							field.setAccessible(true);
							field.set(obj,fieldDataStore.getAliases());
							dataAliasesFound = true;
							break;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
				clz = clz.getSuperclass();
			} while(clz != null);
			
			if (!dataAliasesFound) {
				throw new AliasWriteException("Can't save aliases! The generated class or its supper class should have DataAliases type field declared.");
			}
		}
		
		return obj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	private <T> T generate(Class<T> cls, Field ffield) {
		processAnnotations(cls);

		Converter conv = xstream.getConverterLookup().lookupConverterForType(cls);

		if (conv instanceof DataValueConverter) {
			String stringValue = generateValueForField(cls,ffield);
			return ((DataValueConverter) conv).fromString(stringValue, cls, ffield);
		}
		
		if (conv instanceof SingleValueConverter && !(conv instanceof EncodedByteArrayConverter)) {
			String stringValue = generateValueForField(cls,ffield);
			Object value = ((SingleValueConverter) conv).fromString(stringValue);
			return (T) value;
		}


		if (conv instanceof ArrayConverter || conv instanceof CharArrayConverter || conv instanceof EncodedByteArrayConverter){
			int n = getnArrayForField(ffield);
			T array = (T) Array.newInstance(cls.getComponentType(), n);
            Object element = generate(cls.getComponentType(), ffield);
			for (int i=0; i<n; i++){
				if (i > 0) {
					element = deepCopy(element);
				}
				Array.set(array, i, element);
			}
			recursionCounter.reset(ffield);
			return array;
		}

		Class concreteCollectionClass = null;
		if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
			FieldData fieldData = fieldDataStore.getData(ffield);
			if (fieldData!=null && fieldData.clasz()!=null){
				for (Class<?> clz: fieldData.clasz()){
					if (cls.isAssignableFrom(clz)){
						return (T) generate(clz, ffield);
					}
				}
			}

			Class[] collection = new Class[]{ArrayList.class, HashSet.class, HashMap.class};
			for (Class<?> clz: collection){
				if (cls.isAssignableFrom(clz)) {
					concreteCollectionClass = clz;
					conv = xstream.getConverterLookup().lookupConverterForType(clz);
					break;
				}
			}

			if (concreteCollectionClass == null) {
				System.err.println("[WARNING] Please provide implimentation class for " + cls.getCanonicalName() + " field '" + ffield + "'");
				return null;
			}
		}
		
		if (conv instanceof CollectionConverter){
			int n = getnArrayForField(ffield);
            Collection collection;
            if (concreteCollectionClass == null) concreteCollectionClass = cls;
            try {
            	collection = (Collection) concreteCollectionClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			Class element = getGenericTypeOrString(ffield,0);
			if (element != null) {
				Object colElement = generate(element, ffield);
				for (int i = 0; i < n; i++) {
					if (i > 0) {
						colElement = deepCopy(colElement);
					}
					collection.add(colElement);
				}
			}
			recursionCounter.reset(ffield);
			return (T) collection;
		}
		
		if (conv instanceof MapConverter){
			Map map = null;
			if (concreteCollectionClass == null) concreteCollectionClass = cls;
			try {
				map = (Map) concreteCollectionClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			Class keyElement = getGenericTypeOrString(ffield, 0);
			Class valueElement = getGenericTypeOrString(ffield, 1);
			if (keyElement!=null && valueElement!=null){
				if (map != null) {
					map.put(generate(keyElement, ffield), generate(valueElement, ffield));
				}
			}
			return (T) map;
		}
		
		if (conv instanceof EnumConverter){
			return cls.getEnumConstants()[Integer.parseInt(generateValueForField(cls,ffield))];
		}
		
		if (conv instanceof EnumSetConverter){
			Type type = ffield.getGenericType();
			if (type instanceof ParameterizedType){
				Class genType = (Class) ((ParameterizedType)type).getActualTypeArguments()[0];
				Enum e = (Enum) genType.getEnumConstants()[Integer.parseInt(generateValueForField(genType,ffield))];
				return (T) EnumSet.of(e);
			}
		}
		
		if (conv instanceof ReflectionConverter) {

			Object obj = xstream.getReflectionProvider().newInstance(cls);

			if (ffield != null) {
				if (recursionCounter.getCounter(ffield) < recursionLevel) {
					recursionCounter.incrementCounter(ffield);
				} else {
					return (T) obj;
				}
			}

			Class superCls = cls;
			do {
				for (Field field : superCls.getDeclaredFields()){
                    if (isInnerClass(field.getType())) {
                        System.err.println("          Field '" + field.getName() + "' was skipped by generator.");
                        continue;
					}
					field.setAccessible(true);
					FieldData fieldData = fieldDataStore.getData(field);
                    if (fieldData != null && fieldData.skip()) {
                        continue;
                    }
					if (field.isAnnotationPresent(XStreamOmitField.class)){
						continue;
					}
					if (Modifier.isStatic(field.getModifiers())){
						continue;
					}

					Object value = generate(field.getType(), field);
					try {
						field.set(obj, value);
                    } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                        throw new RuntimeException(e);
                    }
				}
				superCls = superCls.getSuperclass();
			} while (superCls != null);

			return (T) obj;
		}
		
		if (conv instanceof DataAliasesConverter) {
			//No data should be generated for DataAliasesConverter
			return null;
		}

		System.err.println("[WARNING] Converter type: " + conv.getClass().getName() + ". Can't generate data for field " + ffield);
		return null;
	}
	
}