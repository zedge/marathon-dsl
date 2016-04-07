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

package net.zedge.marathon.dsl.context

import net.zedge.marathon.dsl.DslContext;
import net.zedge.marathon.dsl.annotation.SinceMarathonVersion;

class PortDefinitionContext extends DslContext {

    @SinceMarathonVersion("1.1.0")
    def name(String name) {
        data.name = name
    }

    def port(int port) {
        data.port = port
    }

    def protocol(String protocol) {
        data.protocol = protocol
    }

    @Override
    def applyDefaults() {
        if (!data.protocol) {
            data.protocol = 'tcp'
        }
    }

}