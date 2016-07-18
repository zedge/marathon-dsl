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
import net.zedge.marathon.dsl.DslCompilationException
import net.zedge.marathon.dsl.DslVerificationException

/**
 * @author stig@zedge.net
 */
class ContainerContext extends DslContext {

    def type(Type type) {
        data.type = type.name
    }

    def docker(Closure closure) {
        data.type = Type.DOCKER.name
        data.docker = new DockerContext()
        data.docker.with(closure)
    }

    def volume(Closure closure) {
        def context = new ContainerVolumeContext()
        addToDataList('volumes', context)
        context.with(closure)
    }

    @Override
    def verifyConfig() throws DslVerificationException {
        super.verifyConfig()
        if (!data.type) {
            throw new DslVerificationException('container.type not specified!')
        }
    }

    enum Type {
        DOCKER("DOCKER")
        final String name
        public Type(String name) { this.name = name }
    }

}
