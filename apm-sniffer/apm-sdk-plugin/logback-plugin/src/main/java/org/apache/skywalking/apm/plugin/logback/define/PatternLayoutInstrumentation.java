/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.plugin.logback.define;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

public class PatternLayoutInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

  public static final String ENHANCE_CLASS = "ch.qos.logback.core.pattern.PatternLayoutBase";
  public static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.logback.PatternLayoutInterceptor";

  @Override
  protected ClassMatch enhanceClass() {
    return byName(ENHANCE_CLASS);
  }

  @Override
  public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
    return null;
  }

  @Override
  public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
    return new InstanceMethodsInterceptPoint[]{
        new InstanceMethodsInterceptPoint() {
          @Override
          public ElementMatcher<MethodDescription> getMethodsMatcher() {
            return ElementMatchers.isMethod()
                .and(isPublic())
                .and(namedOneOf("setPattern"))
                .and(takesArguments(java.lang.String.class));
          }

          @Override
          public String getMethodsInterceptor() {
            return INTERCEPT_CLASS;
          }

          @Override
          public boolean isOverrideArgs() {
            return true;
          }
        }
    };
  }
}
