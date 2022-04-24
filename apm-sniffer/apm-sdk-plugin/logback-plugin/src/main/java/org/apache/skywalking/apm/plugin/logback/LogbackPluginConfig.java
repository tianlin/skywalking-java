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

import org.apache.skywalking.apm.agent.core.boot.PluginConfig;

public class LogbackPluginConfig {

  public static class Plugin {
    @PluginConfig(root = LogbackPluginConfig.class)
    public static class Logback {
      /**
       * PatternLayoutInterceptor will replace OLD_LAYOUT_PATTERN_REGEX with NEW_LAYOUT_PATTERN
       */
      public static String OLD_LAYOUT_PATTERN_REGEX = "%m|%msg|%message";
      public static String NEW_LAYOUT_PATTERN = "TID: %mdc{trace_id} SPANID: %mdc{span_id} %m";
    }
  }

}
