/*
Copyright 2010-2012 Michael Braiman

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

package datainstiller.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import datainstiller.generators.GeneratorInterface;

/**
 * 
 * @author Michael Braiman braimanm@gmail.com
 *          <p/>
 *          This is{@link XStream} Converter implementation for marshaling and unmarshaling {@link DataAliases} map.
 *          During unmarshaling, if alias value is data generator expression then this expression is resolved to data using specific generator. 
 */
public class DataAliasesConverter implements Converter {
	DataGenerator classDataGenerator;
	
	public DataAliasesConverter(DataGenerator classDataGenerator) {
		this.classDataGenerator=classDataGenerator;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		return (type.equals(DataAliases.class));
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source != null) {
			DataAliases aliases=(DataAliases)source;
			for (String key:aliases.map.keySet()){
				writer.startNode(key);
				writer.setValue(aliases.get(key).toString());
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		DataAliases aliases = new DataAliases();
		String nodeName;
		String value;
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			nodeName = reader.getNodeName();
			value = reader.getValue();
			if (value.matches("\\$\\[.+\\]")) {
				Pattern pattern = Pattern.compile("\\$\\[(.+)\\(\\s*'\\s*(.*)\\s*'\\s*\\,\\s*'\\s*(.*)\\s*'\\s*\\)");
				Matcher matcher = pattern.matcher(value);
				if (matcher.find() != true) {
					throw new PatternUnmarshalException(value + " - invalid data generation expression!");
				}
				
				GeneratorInterface genType = classDataGenerator.getGenerator(matcher.group(1).trim());
			
				String init = matcher.group(2);
				String val = matcher.group(3);
				value = genType.generate(init, val);
			}
			aliases.put(nodeName, value);
			reader.moveUp();
		}
		return aliases;
	}
}
