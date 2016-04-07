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

/**
 * @author stig@zedge.net
 */
class UpgradeStrategyContext {
    Map upgradeStrategy
    def UpgradeStrategyContext(Map json) {
        json.upgradeStrategy = upgradeStrategy = [:]
    }

    def minimumHealthCapacity(double minimumHealthCapacity) {
        upgradeStrategy.minimumHealthCapacity = minimumHealthCapacity
    }

    def maximumOverCapacity(double maximumOverCapacity) {
        upgradeStrategy.maximumOverCapacity = maximumOverCapacity
    }

}
