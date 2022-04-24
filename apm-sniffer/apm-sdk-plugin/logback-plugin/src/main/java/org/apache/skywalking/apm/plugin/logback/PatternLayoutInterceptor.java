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

package org.apache.skywalking.apm.plugin.logback;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

public class PatternLayoutInterceptor implements InstanceMethodsAroundInterceptor {

  private static final String PATTERN_TRACE_ID = "%mdc{trace_id}";
  
  @Override
  public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                           Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
    if (allArguments != null && allArguments.length > 0) {
      String oldPattern = (String) allArguments[0];

      if (!oldPattern.contains(PATTERN_TRACE_ID)) {
        allArguments[0] = addSWPatternBeforeMsgPattern(oldPattern);
      }
    }
  }

  @Override
  public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                            Class<?>[] argumentsTypes, Object ret) throws Throwable {
    return ret;
  }

  private String addSWPatternBeforeMsgPattern(String oldPattern) {
    return oldPattern.replaceFirst(LogbackPluginConfig.Plugin.Logback.OLD_LAYOUT_PATTERN_REGEX,
        LogbackPluginConfig.Plugin.Logback.NEW_LAYOUT_PATTERN);
  }

  @Override
  public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                    Class<?>[] argumentsTypes, Throwable t) {
  }

}
