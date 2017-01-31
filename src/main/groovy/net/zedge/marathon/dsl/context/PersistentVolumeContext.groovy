/*
 * Copyright 2017 Zedge, Inc.
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
class PersistentVolumeContext extends DslContext {

    def type(String type) {
        data.type = type
    }

    def size(int size) {
        data.size = size
    }

    def maxSize(int maxSize) {
        data.maxSize = maxSize
    }

    def pathConstraint(String pattern) {
        addToDataList('constraints', ['path', 'LIKE', pattern])
    }
}
