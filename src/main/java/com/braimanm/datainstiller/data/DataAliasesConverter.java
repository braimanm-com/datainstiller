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

import com.braimanm.datainstiller.generators.GeneratorInterface;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Michael Braiman braimanm@gmail.com
 *          This is{@link XStream} Converter implementation for marshaling and unmarshaling {@link DataAliases} map.
 *          During unmarshaling, if alias value is data generator expression then this expression is resolved to data using specific generator. 
 */
public class DataAliasesConverter implements Converter {
    private JexlContext jexlContext;
    private DataAliases globalAliases;

    public DataAliasesConverter(JexlContext jexlContext, DataAliases globalAliases) {
        this.globalAliases = globalAliases;
    	if (jexlContext == null) {
            this.jexlContext = new MapContext();
        } else {
            this.jexlContext = jexlContext;
        }
    }

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
				writer.setValue(aliases.getAsString(key));
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		DataAliases aliases = new DataAliases();
		if (globalAliases != null) {
			globalAliases.forEach((key, value)->jexlContext.set(key, value));
		}
		String nodeName;
		String value;
        Object objValue = null;
        JxltEngine jxlt = new JexlBuilder().strict(true).silent(false).create().createJxltEngine();
        while (reader.hasMoreChildren()) {
			reader.moveDown();
			nodeName = reader.getNodeName();
			value = reader.getValue();
            if (value.matches("\\$\\[.+]")) {
                Pattern pattern = Pattern.compile("\\$\\[(.+)\\(\\s*'\\s*(.*)\\s*'\\s*,\\s*'\\s*(.*)\\s*'\\s*\\)");
                Matcher matcher = pattern.matcher(value);
				if (!matcher.find()) {
					throw new PatternUnmarshalException(value + " - invalid data generation expression!");
				}	
				GeneratorInterface genType = new DataGenerator(new XStream()).getGenerator(matcher.group(1).trim());
				String init = matcher.group(2);
				String val = matcher.group(3);
				value = genType.generate(init, val);
            } else {
                JxltEngine.Expression expr = jxlt.createExpression(value);
                try {
                    objValue = expr.evaluate(jexlContext);
                    if (objValue != null) {
						value = objValue.toString();
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            aliases.put(nodeName, value);
            if (objValue != null) {
                jexlContext.set(nodeName, objValue);
            } else {
                jexlContext.set(nodeName, value);
            }
            objValue = null;
            reader.moveUp();
		}
		return aliases;
	}
}
