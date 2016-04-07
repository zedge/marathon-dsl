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

import spock.lang.Shared
import spock.lang.Specification

import net.zedge.marathon.dsl.context.HealthCheckContext.Protocol

/**
 * @author stig@zedge.net
 */
class AppContextTest extends Specification {

    @Shared AppContext appContext
    static final String TEST_ID = 'test'
    static final List<String> TEST_ARGS = ['-a', '-b']
    static final double TEST_CPUS = 1.42
    static final int TEST_MEM = 256
    static final int TEST_INSTANCES = 5
    static final String TEST_EXECUTOR = "test_executor"

    def setup() {
        appContext = new AppContext(TEST_ID)
    }

    def "id"() {
        expect:
        assert appContext.id == TEST_ID
    }

    def "cmd"() {
        when:
        appContext.cmd("foo")
        then:
        appContext.data.cmd == "foo"
    }

    def "args(String...)"() {
        when:
        appContext.args(*TEST_ARGS)
        then:
        appContext.data.args == TEST_ARGS
    }

    def "args(List<String>)"() {
        when:
        appContext.args(TEST_ARGS)
        then:
        appContext.data.args == TEST_ARGS
    }

    def "cpus"() {
        when:
        appContext.cpus(TEST_CPUS)
        then:
        appContext.data.cpus == TEST_CPUS
    }

    def "mem"() {
        when:
        appContext.mem(TEST_MEM)
        then:
        appContext.data.mem == TEST_MEM
    }

    def "portDefinition"() {
        when:
        appContext.portDefinition {}
        appContext.portDefinition {}
        then:
        assert appContext.data.portDefinitions instanceof List
        assert appContext.data.portDefinitions.size() == 2
    }

    def "requirePorts"() {
        when:
        appContext.requirePorts(true)
        then:
        appContext.data.requirePorts == true
    }

    def "instances"() {
        when:
        appContext.instances(TEST_INSTANCES)
        then:
        appContext.data.instances == TEST_INSTANCES
    }

    def "Executor"() {
        when:
        appContext.executor(TEST_EXECUTOR)
        then:
        appContext.data.executor == TEST_EXECUTOR
    }

    def "container"() {
        when:
        boolean closureCalled = false
        def closure = {
            closureCalled = true
        }
        appContext.container(ContainerContext.Type.DOCKER, closure)
        then:
        assert closureCalled
        assert appContext.data.container instanceof ContainerContext
    }

    def "env(String, String)"() {
        when:
        appContext.env('FOO', 'foo')
        appContext.env('BAR', 'bar')
        then:
        assert appContext.data.env == [FOO: 'foo', BAR: 'bar']
    }

    def "env(Map)"() {
        when:
        appContext.env(FOO: 'foo', BAR: 'bar')
        then:
        assert appContext.data.env == [FOO: 'foo', BAR: 'bar']
    }

    def "constraint(String)"() {
        when:
        appContext.constraint('hostname')
        then:
        assert appContext.data.constraints.size() == 1
        assert appContext.data.constraints[0] instanceof ConstraintContext
        assert appContext.data.constraints[0].field == 'hostname'
    }

    def "constraint(String, Closure)"() {
        when:
        def closureCalled = false
        def closure = { closureCalled = true }
        appContext.constraint('hostname', closure)
        then:
        assert closureCalled
        assert appContext.data.constraints[0].field == 'hostname'

    }

    def "healthCheck(Closure)"() {
        when:
        def closureCalled = false
        def closure = { closureCalled = true }
        appContext.healthCheck(closure)
        then:
        assert closureCalled
        assert appContext.data.healthChecks.size() == 1
        assert appContext.data.healthChecks[0] instanceof HealthCheckContext
    }

    def "healthCheck(Protocol, Closure)"() {
        when:
        def closureCalled = false
        def closure = { closureCalled = true }
        appContext.healthCheck(Protocol.TCP, closure)
        then:
        assert closureCalled
        assert appContext.data.healthChecks.size() == 1
        assert appContext.data.healthChecks[0] instanceof HealthCheckContext
        assert appContext.data.healthChecks[0].data.protocol == Protocol.TCP.name
    }

    def "healthCheck(Protocol)"() {
        when:
        appContext.healthCheck(Protocol.TCP)
        then:
        assert appContext.data.healthChecks.size() == 1
        assert appContext.data.healthChecks[0] instanceof HealthCheckContext
        assert appContext.data.healthChecks[0].data.protocol == Protocol.TCP.name
    }

    def "upgradeStrategy(Map)"() {
        def testUpgradeStrategy = [minimumHealthCapacity: 1, maximumOverCapacity: 1]
        when:
        appContext.upgradeStrategy(testUpgradeStrategy)
        then:
        appContext.data.upgradeStrategy == testUpgradeStrategy
    }

    def "upgradeStrategy(Closure)"() {
        when:
        def closureCalled = false
        def closure = { closureCalled = true }
        appContext.upgradeStrategy(closure)
        then:
        assert closureCalled
    }

    def "privileged()"() {
        when:
        appContext.privileged()
        then:
        assert appContext.data.privileged == true
    }

    def "privileged(boolean)"() {
        when:
        appContext.privileged(false)
        then:
        assert appContext.data.privileged == false
    }

    def "backoffSeconds"() {
        when:
        appContext.backoffSeconds(42)
        then:
        assert appContext.data.backoffSeconds == 42
    }

    def "backoffFactor"() {
        when:
        appContext.backoffFactor(4.2)
        then:
        assert appContext.data.backoffFactor == 4.2
    }

    def "maxLaunchDelaySeconds"() {
        when:
        appContext.maxLaunchDelaySeconds(123)
        then:
        assert appContext.data.maxLaunchDelaySeconds == 123
    }

    def "label"() {
        when:
        appContext.label("key", "value")
        then:
        assert appContext.data.labels == ["key": "value"]
    }

    def "labels"() {
        when:
        appContext.label("oldkey", "oldvalue")
        appContext.labels("newkey": "newvalue")
        then:
        assert appContext.data.labels == ["newkey": "newvalue"]
    }

    def "acceptedResourceRoles"() {
        when:
        appContext.acceptedResourceRoles("role1", "role2")
        then:
        assert appContext.data.acceptedResourceRoles == ["role1", "role2"]
    }

}
