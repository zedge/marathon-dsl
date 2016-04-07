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
import net.zedge.marathon.dsl.DslContext

import java.util.regex.Pattern

/**
 * @author stig@zedge.net
 */
class ConstraintContext extends DslContext {

    List constraint
    String field

    def ConstraintContext(String field=null) {
        this.field = field
    }

    /**
     * UNIQUE tells Marathon to enforce uniqueness of the attribute across all of an app's tasks.
     * For example the following constraint ensures that there is only one app task running on each host:
     *
     * <code>constraint unique('hostname')</code>
     *
     * @param field
     */
    def unique(String field=this.field) {
        constraint = [field, 'UNIQUE']
    }

    /**
     * CLUSTER allows you to run all of your app's tasks on slaves that share a certain attribute.
     * This is useful for example if you have apps with special hardware needs, or if you want to run
     * them on the same rack for low latency:
     *
     * <code>constraint cluster('rack_id', 'rack-1')</code>
     *
     * You can also use this attribute to tie an application to a specific node by using the hostname property:
     *
     * <code>constraint cluster('hostname', 'a.specific.node.com')</code>
     *
     * @param field
     * @param value
     */
    def cluster(String field, String value) {
        constraint = [field, 'CLUSTER', value]
    }

    def cluster(String value) {
        constraint = [this.field, 'CLUSTER', value]
    }

    /**
     * GROUP_BY can be used to distribute tasks evenly across servers, racks or datacenters.
     *
     * @param spread
     */
    def groupBy(int spread) {
        constraint = [this.field, 'GROUP_BY', "${spread}"]
    }

    /**
     * Marathon only knows about different values of the attribute (e.g. "rack_id") by analyzing incoming
     * offers from Mesos. If tasks are not already spread across all possible values, specify the number of
     * values in constraints. If you don't specify the number of values, you might find that the tasks are
     * only distributed in one value, even though you are using the GROUP_BY constraint. For example, if you
     * are spreading across 3 racks, use <code>constraint groupBy('rack_id', 3)</code>.
     *
     * @param field
     * @param spread
     */
    def groupBy(String field, int spread) {
        constraint = [field, 'GROUP_BY', "${spread}"]
    }

    /**
     * LIKE accepts a regular expression as parameter, and allows you to run your tasks only on the slaves
     * whose field values match the regular expression:
     *
     * <code>constraint('rack_id') like(/rack-[1-3]/)</code>
     *
     * @param value
     * @return
     */
    def like(Pattern value) {
        like(value.toString())
    }

    /**
     * LIKE accepts a regular expression as parameter, and allows you to run your tasks only on the slaves
     * whose field values match the regular expression:
     *
     * <code>constraint('rack_id') like('rack-[1-3]')</code>
     *
     * @param value
     * @return
     */
    def like(String value) {
        constraint = [this.field, 'LIKE', value]
    }

    /**
     * Just like LIKE operator, but only run tasks on slaves whose field values don't match the regular expression:
     *
     * <code>constraint('rack_id') unlike(/rack-[7-9]/)</code>
     *
     * @param value
     * @return
     */
    def unlike(Pattern value) {
        like(value.toString())
    }

    /**
     * Just like LIKE operator, but only run tasks on slaves whose field values don't match the regular expression:
     *
     * <code>constraint('rack_id') unlike('rack-[7-9]')</code>
     *
     * @param value
     * @return
     */
    def unlike(String value) {
        constraint = [this.field, 'UNLIKE', value]
    }

    /**
     * Constraints are represented by a list in Marathon's JSON app definition.
     * @return
     */
    @Override
    def toJsonData() {
        // not calling super.toJsonData() here, since we need to output a list, not a map
        constraint
    }

    @Override
    def verifyConfig() throws DslVerificationException {
        super.verifyConfig()
        if (constraint.size() < 2) {
            throw new DslVerificationException("invalid constraint: ${constraint}")
        }
        if (constraint[0] == null) {
            throw new DslVerificationException("constraint field missing: ${constraint}")
        }
    }

}
