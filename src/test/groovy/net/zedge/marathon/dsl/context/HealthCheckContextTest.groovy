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
import net.zedge.marathon.dsl.DslExecutionException
import spock.lang.Specification

import net.zedge.marathon.dsl.context.HealthCheckContext.Protocol

/**
 * @author stig@zedge.net
 */
class HealthCheckContextTest extends Specification {

    static final HEALTH_COMMAND = 'health.sh'
    static final HEALTH_PATH = '/path'
    static final TEST_SECONDS = 42
    static final PORT_NAME1 = 'port1'
    static final PORT_NAME2 = 'port2'
    static final PORT_NAME3 = 'port3'

    DslContext parent
    HealthCheckContext sut

    def setup() {
        parent = new DslContext()
        sut = new HealthCheckContext(parent)
    }

    def cleanup() {
    }

    def "test protocol(Protocol)"() {
        when:
        def ret = sut.protocol(Protocol.TCP)
        then:
        assert sut.data.protocol == Protocol.TCP.name
        assert ret == sut
    }

    def "test protocol(String)"() {
        when:
        def ret = sut.protocol('TCP')
        then:
        assert sut.data.protocol == Protocol.TCP.name
        assert ret == sut
    }

    def "test protocol(invalid String)"() {
        when:
        sut.protocol('FOO')
        then:
        thrown DslExecutionException
    }

    def "test command"() {
        when:
        def ret = sut.command(HEALTH_COMMAND)
        then:
        assert sut.data == [protocol: 'COMMAND', command: [value: HEALTH_COMMAND]]
        assert ret == sut
    }

    def "test tcp"() {
        when:
        def ret = sut.tcp(0)
        then:
        assert sut.data == [protocol: 'TCP', portIndex: 0]
        assert ret == sut
    }

    def "test http(int)"() {
        when:
        def ret = sut.http(0)
        then:
        assert sut.data == [protocol: 'HTTP', portIndex: 0]
        assert ret == sut
    }

    def "test http(String)"() {
        when:
        def ret = sut.http(HEALTH_PATH)
        then:
        assert sut.data == [protocol: 'HTTP', portIndex: 0, path: HEALTH_PATH]
        assert ret == sut
    }

    def "test gracePeriodSeconds"() {
        when:
        def ret = sut.gracePeriodSeconds(TEST_SECONDS)
        then:
        assert sut.data.gracePeriodSeconds == TEST_SECONDS
        assert ret == sut
    }

    def "test intervalSeconds"() {
        when:
        def ret = sut.intervalSeconds(TEST_SECONDS)
        then:
        assert sut.data.intervalSeconds == TEST_SECONDS
        assert ret == sut
    }

    def "test portName with no container"() {
        when:
        def ret = sut.portName(PORT_NAME1)
        then:
        thrown DslExecutionException
    }

    def "test portName with no port mappings"() {
        given:
        parent.data.container = buildContainerContextWithPortMappings([])
        when:
        def ret = sut.portName(PORT_NAME1)
        then:
        thrown DslExecutionException
    }

    def "test portName with Docker port mappings"() {
        given:
        parent.data.container = buildContainerContextWithPortMappings([PORT_NAME1, PORT_NAME2, PORT_NAME3])
        when:
        def ret = sut.portName(PORT_NAME2)
        then:
        assert sut.data == [portIndex: 1]
        assert ret == sut
    }

    def "test portName with Docker port definitions"() {
        given:
        parent.data.portDefinitions = buildPortDefinitions([PORT_NAME1, PORT_NAME2, PORT_NAME3])
        when:
        def ret = sut.portName(PORT_NAME2)
        then:
        assert sut.data == [portIndex: 1]
        assert ret == sut
    }

    def "test portIndex"() {
        when:
        def ret = sut.portIndex(2)
        then:
        assert sut.data == [portIndex: 2]
        assert ret == sut
    }

    def "test timeoutSeconds"() {
        when:
        def ret = sut.timeoutSeconds(TEST_SECONDS)
        then:
        assert sut.data == [timeoutSeconds: TEST_SECONDS]
        assert ret == sut
    }

    def "test maxConsecutiveFailures"() {
        when:
        def ret = sut.maxConsecutiveFailures(3)
        then:
        assert sut.data == [maxConsecutiveFailures: 3]
        assert ret == sut
    }

    def buildContainerContextWithPortMappings(List<String> portNames) {
        def container = new DslContext(data: [docker: new DslContext()])
        container.data.docker.data.portMappings = portNames.collect {
            new DslContext(data: [name: it])
        }
        container
    }

    def buildPortDefinitions(List<String> portNames) {
        portNames.collect {
            new DslContext(data: [name: it])
        }
    }

}
