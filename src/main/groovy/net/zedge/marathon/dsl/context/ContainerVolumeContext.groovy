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
class ContainerVolumeContext extends DslContext {

    def containerPath(String containerPath) {
        data.containerPath = containerPath
    }

    def hostPath(String hostPath) {
        data.hostPath = hostPath
    }

    def mode(Mode mode) {
        data.mode = mode.name
    }

    @Override
    def applyDefaults() {
        if (!data.mode) {
            data.mode = Mode.RW.name
        }
    }

    enum Mode {
        RW("RW"),
        RO("RO")
        final String name
        public Mode(String name) {
            this.name = name
        }
    }
}
