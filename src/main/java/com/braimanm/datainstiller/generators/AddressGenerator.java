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

package com.braimanm.datainstiller.generators;

import java.util.List;

public class AddressGenerator extends File2ListReader implements GeneratorInterface{
	List<String> streets = null;
	List<String> cities = null;

	private void init() {
		if (streets == null && cities == null) {
			streets = populate("/streets");
			cities = populate("/canada_cities");
		}
	}

	public Address generateAddress() {
		return generateAddress(false);
	}

	public Address generateAddress(boolean generatePostalCodes) {
		init();
		Address address = new Address();
		address.country = "CANADA";
		int m = (int) Math.pow(10, (int) (Math.random() * 5 + 1));
		address.streetNumber = (String.valueOf((int) (Math.random() * m) + 1));
		address.streetName = streets.get((int) (Math.random() * streets.size()));
		String[] city = cities.get((int) (Math.random() * cities.size())).split("\\t");
		address.city = city[0];
		address.provinceName = city[1];
		address.provinceCode = city[2];
		address.postalCode = city[3];
		if (generatePostalCodes) {
			address.postalCode = new AlphaNumericGenerator().generate("(A)[a](B) [b](C)[c]");
		}
		return address;
	}

	@Override
	public String generate(String pattern, String value) {
		Address address = generateAddress();
		return address.toString(pattern);
	}

	public static class Address{
		public String country;
		public String streetNumber;
		public String streetName;
		public String city;
		public String provinceName;
		public String provinceCode;
		public String postalCode;

		@Override
		public String toString(){
			String str = "Country: " + country + ", ";
			str += "Street #: " + streetNumber + ", ";
			str += "Street Name: " + streetName + ", ";
			str += "City: " + city + ", ";
			str += "Province Name: " + provinceName + ", ";
			str += "Province Code: " + provinceCode + ", ";
			str += "Postal Code: " + postalCode;
			return str;
		}

		/* Pattern:
		 * {C} - country
		 * {#} - street number
		 * {S} - street name
		 * {T} - city
		 * {P} - province name
		 * {K} - province code
		 * {O} - postal code
		 */
		public String toString(String pattern){
			String out=pattern;
			out=out.replace("{C}", country);
			out=out.replace("{#}", streetNumber);
			out=out.replace("{S}", streetName);
			out=out.replace("{T}", city);
			out=out.replace("{P}", provinceName);
			out=out.replace("{K}", provinceCode);
			out=out.replace("{O}", postalCode);
			return out;
		}
	}
	
//	@Test
//	public static void test(){
//		AddressGenerator addrGen=new AddressGenerator();
//		System.out.println(addrGen.generateAddress() + "\n");
//		System.out.println(addrGen.generateAddress(true) + "\n");
//		Address address=addrGen.generateAddress();
//		System.out.println(((GeneratorInterface) addrGen).generate("{#} {S}, {T}, {O}, {K} ({P})",null));
//	}


}


