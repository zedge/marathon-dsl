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

/**
 * DSL contexts are fragments of config that usually represent JSON objects
 * in the final Marathon app JSON output (occasionally JSON arrays).
 *
 * @author stig@zedge.net
 */
class DslContext {

    Map data = [:]

    /**
     * Return the configuration in this context converted to whatever format
     * is used to represent it in the JSON format.
     *
     * @return Map
     */

    def Object toJsonData() {
        this.applyDefaults()
        this.verifyConfig()
        Map configCopy = [:]
        data.each { k, v ->
            // Omit arrays and objects if they are empty
            if ((v instanceof Collection) && v.isEmpty()) {
                return
            }
            else if (v instanceof DslContext) {
                configCopy.put(k, v.toJsonData())
            }
            else if (v instanceof List) {
                configCopy.put(k, convertList(v))
            }
            else {
                configCopy.put(k, v)
            }
        }
        configCopy
    }

    /**
     * Add an element to a config entry that is a list. Initialize the list if
     * it is not present already.
     *
     * @param name    Key/field name in config map
     * @param what    The config value
     * @return what
     */
    def addToDataList(String name, Object what) {
        if (!data[name]) {
            data[name] = []
        }
        data[name] << what
        what
    }

    /**
     * Add a element to a config entry that is a map. Initialize the map if
     * it is not present already.
     *
     * @param name
     * @param key
     * @param what
     */
    def putToDataMap(String name, String key, Object what) {
        if (!data[name]) {
            data[name] = [:]
        }
        data[name][key] = what
        what
    }

    /**
     * Recursively convert a list to whatever format is used in the JSON output.
     *
     * @param list
     * @return
     */
    def convertList(List list) {
        list.collect {
            if (it instanceof List) {
                convertList(it)
            } else if (it instanceof DslContext) {
                it.toJsonData()
            } else {
                it
            }
        }
    }

    /**
     * The <code>applyDefaults()</code> method is called just before the
     * config is converted to JSON, and can be used to inject default
     * values within each DslContext implementation.
     */
    def applyDefaults() {
    }

    def verifyListItems(List list) {
        list.each {
            if (it instanceof List) {
                verifyListItems(it)
            } else if (it instanceof Map) {
                verifyMapItems(it)
            } else if (it instanceof DslContext) {
                it.verifyConfig()
            }
        }
    }

    def verifyMapItems(Map map) {
        map.each { k, v ->
            if (v instanceof List) {
                verifyListItems(v)
            } else if (v instanceof Map) {
                verifyMapItems(v)
            } else if (v instanceof DslContext) {
                v.verifyConfig()
            }
        }
    }

    /**
     * The <code>verifyMethod()</code> method is called just before the config
     * is converted to JSON (after <code>applyDefaults()>), and can be used to
     * report erroneous configs. Errors are reported by using DslVerificationException,
     * make sure you provide a helpful error message.
     *
     * @return
     * @throws DslVerificationException
     */
    def verifyConfig() throws DslVerificationException {
        verifyMapItems(data)
    }

}
