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

import net.zedge.marathon.dsl.DslContext

/**
 * @author stig@zedge.net
 */
class DockerContext extends DslContext {

    def image(String image) {
        data.image = image
    }

    def network(String network) {
        data.network = network
    }

    def network(Network network) {
        this.network(network.name)
    }

    def forcePullImage(boolean forcePullImage = true) {
        data.forcePullImage = forcePullImage
    }

    def portMapping(Closure closure) {
        def context = new DockerPortMappingContext()
        addToDataList('portMappings', context)
        context.with(closure)
    }

    def privileged(boolean privileged = true) {
        data.privileged = privileged
    }

    def parameter(String key, String value) {
        Map parameter = ['key': key, 'value': value]
        addToDataList('parameters', parameter)
    }

    def parameters(Map<String, String> params) {
        data.parameters = params.entrySet().collect { [key: it.key, value: it.value] } as List
    }

    @Override
    def applyDefaults() {
        if (!data.network) {
            data.network = Network.BRIDGE.name
        }
    }

    enum Network {
        BRIDGE("BRIDGE"),
        HOST("HOST")
        final String name
        public Network(String name) {
            this.name = name
        }
    }

}
