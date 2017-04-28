/*
 * Copyright 2016 Zedge, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package net.zedge.marathon.dsl

import groovy.json.JsonOutput
import net.zedge.marathon.dsl.context.AppContext

/**
 * @author stig@zedge.net
 */
abstract class MarathonDslScript extends Script {

    public List<AppContext> apps

    MarathonDslScript() {
        this.apps = []
    }

    /**
     * Define a Marathon application.
     *
     * @param id
     * @param closure
     * @return
     */
    def app(String id, @DelegatesTo(value = AppContext.class) Closure closure) {
        currentAppContext = new AppContext(id)
        currentAppContext.with(closure)
        apps << currentAppContext
        currentAppContext
    }

    /**
     * Retrieve an N character git Hash (default 7) of the current HEAD.
     *
     * @param length
     * @return
     */
    def gitHash() {
        'git rev-parse --short HEAD'.execute().text.trim()
    }

    /**
     * Retrieve an environment variable, or a default value if not defined.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    def env(String name, String defaultValue = '') {
        System.getenv(name) ?: defaultValue
    }

    def toJsonString() {
        return JsonOutput.toJson(apps.collect { it.toJsonData() })
    }

}
