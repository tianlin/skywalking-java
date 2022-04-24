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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.logback.internal.UnionMap;
import org.apache.skywalking.apm.toolkit.logging.common.log.SkyWalkingContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.apache.skywalking.apm.plugin.logback.Constants.SPAN_ID;
import static org.apache.skywalking.apm.plugin.logback.Constants.TRACE_ID;

public class LoggingEventInterceptor implements InstanceMethodsAroundInterceptor {

  private static ILog LOGGER = LogManager.getLogger(LoggingEventInterceptor.class);

  @Override
  public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                           Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

  }

  @Override
  public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                            Class<?>[] argumentsTypes, Object ret) throws Throwable {
    Map<String, String> mdcPropertyMap = (Map<String, String>) ret;
    if (mdcPropertyMap != null && mdcPropertyMap.containsKey(TRACE_ID)) {
      // Assume already instrumented event if traceId is present.
      return mdcPropertyMap;
    }

    Map<String, String> spanContextData = new HashMap<>(2);

    if (!ContextManager.isActive() && allArguments.length > 0) {
      if (allArguments[0] instanceof EnhancedInstance) {
        SkyWalkingContext skyWalkingContext =
            (SkyWalkingContext) ((EnhancedInstance) allArguments[0]).getSkyWalkingDynamicField();
        if (skyWalkingContext != null) {
          spanContextData.put(TRACE_ID, skyWalkingContext.getTraceId());
          int spanId = (int) FieldUtils.readField(skyWalkingContext, "spanId", true);
          spanContextData.put(SPAN_ID, String.valueOf(spanId));
        }
      }
    } else {
      spanContextData.put(TRACE_ID, ContextManager.getGlobalTraceId());
      spanContextData.put(SPAN_ID, String.valueOf(ContextManager.getSpanId()));
    }

    if (!spanContextData.isEmpty()) {
      if (mdcPropertyMap == null) {
        mdcPropertyMap = spanContextData;
      } else {
        mdcPropertyMap = new UnionMap<>(mdcPropertyMap, spanContextData);
      }
    }

    return  mdcPropertyMap;
  }

  @Override
  public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                    Class<?>[] argumentsTypes, Throwable t) {

  }

}
