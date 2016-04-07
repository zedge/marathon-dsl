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

import spock.lang.Specification

/**
 * @author stig@zedge.net
 */
class ConstraintContextTest extends Specification {

    def "cluster(String)"() {
        when:
        def c = new ConstraintContext('foo')
        c.unique()
        then:
        assert c.toJsonData() == ['foo', 'UNIQUE']
    }

    def "cluster(String,String)"() {
        when:
        def c = new ConstraintContext('foo')
        c.unique('bar')
        then:
        assert c.toJsonData() == ['bar', 'UNIQUE']
    }

    def "groupBy(int)"() {
        when:
        def c = new ConstraintContext('foo')
        c.groupBy(2)
        then:
        assert c.toJsonData() == ['foo', 'GROUP_BY', '2']
    }

    def "groupBy(String,int)"() {
        when:
        def c = new ConstraintContext()
        c.groupBy('bar', 2)
        then:
        assert c.toJsonData() == ['bar', 'GROUP_BY', '2']
    }

    def "like(String)"() {
        when:
        def c = new ConstraintContext('foo')
        c.like('regex')
        then:
        assert c.toJsonData() == ['foo', 'LIKE', 'regex']
    }

    def "like(Pattern)"() {
        when:
        def c = new ConstraintContext('bar')
        c.like(/regex/)
        then:
        assert c.toJsonData() == ['bar', 'LIKE', /regex/]
    }

    def "unlike(String)"() {
        when:
        def c = new ConstraintContext('foo')
        c.unlike('regex')
        then:
        assert c.toJsonData() == ['foo', 'UNLIKE', 'regex']
    }

    def "unlike(Pattern)"() {
        when:
        def c = new ConstraintContext('bar')
        c.unlike(/regex/)
        then:
        assert c.toJsonData() == ['bar', 'UNLIKE', /regex/]
    }

    def "verifyConfig"() {

    }

}
