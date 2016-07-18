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
class ConstraintContextTest extends Specification {

    def "unique()"() {
        given:
        def c = new ConstraintContext('foo')
        when:
        c.unique()
        then:
        assert c.toJsonData() == ['foo', 'UNIQUE']
    }

    def "unique(String)"() {
        given:
        def c = new ConstraintContext()
        when:
        c.unique('bar')
        then:
        assert c.toJsonData() == ['bar', 'UNIQUE']
    }

    def "cluster(String)"() {
        given:
        def c = new ConstraintContext('foo')
        when:
        c.cluster('bar')
        then:
        assert c.toJsonData() == ['foo', 'CLUSTER', 'bar']
    }

    def "cluster(String,String)"() {
        given:
        def c = new ConstraintContext()
        when:
        c.cluster('foo', 'bar')
        then:
        assert c.toJsonData() == ['foo', 'CLUSTER', 'bar']
    }

    def "groupBy(int)"() {
        given:
        def c = new ConstraintContext('foo')
        when:
        c.groupBy(2)
        then:
        assert c.toJsonData() == ['foo', 'GROUP_BY', '2']
    }

    def "groupBy(String,int)"() {
        given:
        def c = new ConstraintContext()
        when:
        c.groupBy('bar', 2)
        then:
        assert c.toJsonData() == ['bar', 'GROUP_BY', '2']
    }

    def "like(String)"() {
        given:
        def c = new ConstraintContext('foo')
        when:
        c.like('regex')
        then:
        assert c.toJsonData() == ['foo', 'LIKE', 'regex']
    }

    def "unlike(String)"() {
        given:
        def c = new ConstraintContext('foo')
        when:
        c.unlike('regex')
        then:
        assert c.toJsonData() == ['foo', 'UNLIKE', 'regex']
    }

    def "verifyConfig1"() {
        setup:
        def c = new ConstraintContext()
        c.constraint = []
        when:
        c.verifyConfig()
        then:
        DslVerificationException ex = thrown()
        ex.message.matches(/^invalid constraint: .*/)
    }

    def "verifyConfig2"() {
        given:
        def c = new ConstraintContext()
        c.constraint = [null, 'UNIQUE']
        when:
        c.verifyConfig()
        then:
        DslVerificationException ex = thrown()
        ex.message.matches(/^constraint field missing: .*/)
    }
}
