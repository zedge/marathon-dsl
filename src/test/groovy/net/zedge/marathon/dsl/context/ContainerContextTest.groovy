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

import net.zedge.marathon.dsl.DslVerificationException
import spock.lang.Specification

/**
 * @author stig@zedge.net
 */
class ContainerContextTest extends Specification {

    def "test type"() {
        given:
        def c = new ContainerContext()
        when:
        c.type(ContainerContext.Type.DOCKER)
        then:
        assert c.toJsonData() == [type: 'DOCKER']
    }

    def "test docker"() {
        given:
        def c = new ContainerContext()
        def closureCalled = false
        def closure = { closureCalled = true }
        when:
        c.docker(closure)
        then:
        assert closureCalled
        assert c.data.docker instanceof DockerContext
    }

    def "test volume"() {
        given:
        def c = new ContainerContext()
        def closureCalled = false
        def closure = { closureCalled = true }
        when:
        c.volume(closure)
        then:
        assert closureCalled
        assert c.data.volumes.size() == 1
        assert c.data.volumes[0] instanceof ContainerVolumeContext
    }

    def "test verifyConfig"() {
        given:
        def c = new ContainerContext()
        when:
        c.verifyConfig()
        then:
        thrown DslVerificationException
    }

}
