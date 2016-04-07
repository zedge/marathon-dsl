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

package net.zedge.marathon

import groovy.json.JsonOutput
import net.zedge.marathon.dsl.DslCompilationException
import net.zedge.marathon.dsl.DslScript
import net.zedge.marathon.dsl.DslExecutionException
import net.zedge.marathon.dsl.DslScriptRunner

/**
 * @author stig@zedge.net
 */
class MarathonDslMain {

    public final static MAIN_COMMAND = 'marathon-dsl'

    private static CliBuilder cliBuilder

    static {
        cliBuilder = new CliBuilder(
                usage: "${MAIN_COMMAND} [OPTIONS] DSL-FILE",
                header: '\nOptions:\n'
        )
        cliBuilder.h(longOpt: 'help', 'Usage information')
    }

    public static void main(String[] argv) {
        def options = parseOptions(argv)
        List<String> args = options.arguments()

        def config = makeMarathonConfig()
        DslScript script = null
        try {
            script = DslScriptRunner.runDslScript(new File(args[0]), config)
        } catch (DslExecutionException e) {
            System.err.println("${MAIN_COMMAND}: ${e.message}")
            System.exit(1)
        } catch (DslCompilationException e) {
            System.err.println("${MAIN_COMMAND}: ${e.message}")
            System.exit(1)
        } catch (IOException e) {
            System.err.println("${MAIN_COMMAND}: ${e.message}")
            System.exit(1)
        }
        if (!script) {
            cliBuilder.usage()
            System.exit(0)
        }
        String json = script.toJsonString()
        println System.console() ? JsonOutput.prettyPrint(json) : json
    }

    private static OptionAccessor parseOptions(String[] args) {
        OptionAccessor opt = cliBuilder.parse(args)
        if (opt.h || opt.arguments().size() == 0) {
            cliBuilder.usage()
            System.exit(0)
        }
        opt
    }

    private static ClusterConfig makeMarathonConfig() {
        new ClusterConfig(
                marathonLeaderUrl: 'http://m1.dcos:8080',
                mesosMasterUrl: 'http://m1.dcos:5050',
                name: 'local'
        )
    }

}
