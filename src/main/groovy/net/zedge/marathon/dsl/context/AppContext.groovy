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
class AppContext extends DslContext {

    def AppContext(String id) {
        data = [id: id]
    }

    def getId() {
        data.id
    }

    def cmd(String cmd) {
        data.cmd = cmd
    }

    def args(List<String> args) {
        data.args = args
    }

    def args(String... args) {
        this.args(args.toList())
    }

    def cpus(double cpus) {
        data.cpus = cpus
    }

    def mem(double mem) {
        data.mem = mem
    }

    // ports: The ports array is used to define ports that should be considered as part of a resource offer.
    // It is necessary only to define this array if you are using HOST networking and no port mappings are specified.
    // Only one of ports and portDefinitions should be defined at the same time.

    // NOTE: about port definitions: This must be ports that the mesos-slave is allowed to allocate!
    // see https://www.codatlas.com/github.com/mesosphere/marathon/master/docs/docs/host-port.md
    //
    // portDefinitions: The portDefinitions array is used to define ports that should be considered as part of a
    // resource offer. It is necessary only to define this array if you are using HOST networking and no port mappings
    // are specified. This array is meant to replace the ports array, and makes it possible to specify a port name,
    // protocol and labels. Only one of ports and portDefinitions should be defined at the same time.
    def portDefinition(@DelegatesTo(value = PortDefinitionContext.class) Closure closure) {
        def context = new PortDefinitionContext()
        addToDataList('portDefinitions', context)
        context.with(closure)
    }

    /**
     * requirePorts is a property that specifies whether Marathon should specifically look for specified ports in
     * the resource offers it receives. This ensures that these ports are free and available to be bound to on the
     * Mesos agent. This does not apply to BRIDGE mode networking.
     *
     * @param requirePorts
     */
    def requirePorts(boolean requirePorts) {
        data.requirePorts = requirePorts
    }

    /**
     * Set the number of tasks (instances) the application should be running.
     *
     * @param instances
     * @return
     */
    def instances(int instances) {
        data.instances = instances
    }

    def executor(String executor) {
        data.executor = executor
    }

    /**
     * Configure the application to run in a container.
     * @param type
     * @param closure
     * @return
     */
    def container(ContainerContext.Type type, @DelegatesTo(value = ContainerContext.class) Closure closure) {
        this.container(closure).type(type)
    }

    /**
     * Configure the application to run in a container.
     * @param type
     * @param closure
     * @return
     */
    ContainerContext container(@DelegatesTo(value = ContainerContext.class) Closure closure) {
        data.container = new ContainerContext()
        data.container.with(closure)
        data.container
    }

    /**
     * Add an environment variable.
     * @param key   environment variable value
     * @param value environment variable value
     * @return
     */
    def env(String key, String value) {
        putToDataMap('env', key, value)
    }

    /**
     * Replace all environment variables.
     * @param env   all environment variables
     * @return
     */
    def env(Map<String, String> env) {
        data.env = env
    }

    def constraint(String field) {
        def context = new ConstraintContext(field)
        addToDataList('constraints', context)
        context
    }

    def constraint(String field, @DelegatesTo(value = ConstraintContext.class) Closure closure) {
        this.constraint(field).with(closure)
    }

    def healthCheck(HealthCheckContext.Protocol protocol) {
        def context = new HealthCheckContext(this);
        addToDataList('healthChecks', context)
        context.protocol(protocol.name)
        context
    }

    def healthCheck(@DelegatesTo(value = HealthCheckContext.class) Closure closure) {
        def context = new HealthCheckContext(this);
        addToDataList('healthChecks', context)
        context.with(closure)
    }

    def healthCheck(HealthCheckContext.Protocol protocol,
                    @DelegatesTo(value = HealthCheckContext.class) Closure closure) {
        this.healthCheck(protocol).with(closure)
    }

    def upgradeStrategy(@DelegatesTo(value = UpgradeStrategyContext.class) Closure closure) {
        new UpgradeStrategyContext(data).with(closure)
    }

    def upgradeStrategy(Map upgradeStrategy) {
        data.upgradeStrategy = upgradeStrategy
    }

    def privileged(boolean privileged = true) {
        data.privileged = privileged
    }

    def backoffSeconds(double backoffSeconds) {
        data.backoffSeconds = backoffSeconds
    }

    def backoffFactor(double backoffFactor) {
        data.backoffFactor = backoffFactor
    }

    def maxLaunchDelaySeconds(int maxLaunchDelaySeconds) {
        data.maxLaunchDelaySeconds = maxLaunchDelaySeconds
    }

    def label(String name, String value) {
        putToDataMap('labels', name, value)
    }

    def labels(Map labels) {
        data.labels = labels
    }

    def acceptedResourceRoles(String... roles) {
        roles.each { addToDataList('acceptedResourceRoles', it) }
    }

    def residency(@DelegatesTo(value = ResidencyContext.class) Closure closure) {
        data.residency = new ResidencyContext()
        data.residency.with(closure)
    }

}
