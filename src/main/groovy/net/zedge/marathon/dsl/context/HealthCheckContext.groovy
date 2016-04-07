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
import net.zedge.marathon.dsl.DslExecutionException

/**
 * @author stig@zedge.net
 */
class HealthCheckContext extends DslContext {

    def parent

    HealthCheckContext(DslContext appContext) {
        this.parent = appContext
    }

    def protocol(Protocol protocol) {
        this.protocol(protocol.name)
    }

    def protocol(String protocol) {
        def uc = protocol.toUpperCase()
        if (!Protocol.findByName(uc)) {
            throw new DslExecutionException("Invalid health check protocol: '${protocol}")
        }
        data.protocol = uc
        this
    }

    def command(String command) {
        protocol(Protocol.COMMAND)
        data.command = [value: command]
        this
    }

    def tcp(int portIndex) {
        protocol(Protocol.TCP)
        this.portIndex(portIndex)
    }

    def http(int portIndex) {
        protocol(Protocol.HTTP)
        this.portIndex(portIndex)
    }

    def http(String path) {
        this.http(0).path(path)
    }

    def path(String path) {
        data.path = path
        this
    }

    def gracePeriodSeconds(int gracePeriodSeconds) {
        data.gracePeriodSeconds = gracePeriodSeconds
        this
    }

    def intervalSeconds(int intervalSeconds) {
        data.intervalSeconds = intervalSeconds
        this
    }

    def portName(String portName) {
        List<DockerPortMappingContext> portMappings = parent.data?.container?.data?.docker?.data?.portMappings
        // First, example the Docker port mappings, since that is the most common use case.
        if (portMappings) {
            for (int i = 0; i < portMappings.size(); ++i) {
                if (portName.equals(portMappings[i].data.name)) {
                    return this.portIndex(i)
                }
            }
        }
        List<PortDefinitionContext> portDefinitions = parent.data?.portDefinitions
        if (portDefinitions) {
            for (int i = 0; i < portDefinitions.size(); ++i) {
                if (portName.equals(portDefinitions[i].data.name)) {
                    return this.portIndex(i)
                }
            }
        }
        throw new DslExecutionException("healthCheck: could not find any port named '${portName}'")
    }

    def portIndex(int portIndex) {
        data.portIndex = portIndex
        this
    }

    def timeoutSeconds(int timeoutSeconds) {
        data.timeoutSeconds = timeoutSeconds
        this
    }

    def maxConsecutiveFailures(int maxConsecutiveFailures) {
        data.maxConsecutiveFailures = maxConsecutiveFailures
        this
    }

    enum Protocol {
        COMMAND("COMMAND"),
        HTTP("HTTP"),
        TCP("TCP")
        String name
        def Protocol(name) { this.name = name }
        static findByName(String name) {
            values().find { it.name == name }
        }
    }

}
