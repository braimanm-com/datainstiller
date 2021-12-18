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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Michael Braiman braimanm@gmail.com
 *
 * This annotation instruct how to generate data for the annotated field. 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Data {
	boolean skip() default false;
	String generatorType() default "";
	String value() default "";
	String pattern() default "";
	String alias() default "";
	int nArray() default 3;
	Class<?>[] clasz() default void.class;
	Class<?> fieldClass() default void.class;
	String fieldName() default "";
}