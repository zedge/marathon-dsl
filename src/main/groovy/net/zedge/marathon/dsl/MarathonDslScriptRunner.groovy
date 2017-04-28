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

package net.zedge.marathon.dsl

import net.zedge.marathon.ClusterConfig
import net.zedge.marathon.dsl.context.ContainerContext
import net.zedge.marathon.dsl.context.ContainerVolumeContext
import net.zedge.marathon.dsl.context.DockerContext
import net.zedge.marathon.dsl.context.HealthCheckContext
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.runtime.InvokerHelper

/**
 * @author stig@zedge.net
 */
class MarathonDslScriptRunner {

    static final String BASECLASS_PROPERTY = 'net.zedge.marathon.dsl.baseclass'

    ClusterConfig clusterConfig

    MarathonDslScriptRunner(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig
    }

    MarathonDslScript runDslScript(File scriptFile) {
        Binding binding = new Binding()
        binding.setVariable('marathonConfig', clusterConfig)
        binding.setVariable('cluster', clusterConfig.name)
        String baseClassName = System.getProperty(BASECLASS_PROPERTY) ?: MarathonDslScript.class.getName()
        runDslScript(scriptFile, binding, baseClassName)
    }

    MarathonDslScript runDslScript(File scriptFile, Binding binding, String scriptBaseClass) {

        ClassLoader parentClassLoader = MarathonDslScriptRunner.classLoader
        String scriptBody = scriptFile.text
        // Otherwise baseScript won't take effect
        def compilerConfig = createCompilerConfiguration(scriptBaseClass)
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(parentClassLoader, compilerConfig)
        try {
            GroovyScriptEngine engine = new GroovyScriptEngine('.', groovyClassLoader)
            try {
                engine.config = compilerConfig
                try {
                    Script script
                    String scriptBaseName = scriptFile.name.replaceAll(/[^a-zA-Z_]/, '_')
                    Class cls = engine.groovyClassLoader.parseClass(scriptBody, scriptBaseName)
                    script = InvokerHelper.createScript(cls, binding)
                    assert script instanceof MarathonDslScript
                    script.run()
                    return script
                } catch (CompilationFailedException e) {
                    throw new DslCompilationException(e.message, e)
                } catch (GroovyRuntimeException e) {
                    throw new DslExecutionException(e.message, e)
                } catch (ResourceException e) {
                    throw new IOException('Unable to run script', e)
                } catch (ScriptException e) {
                    throw new IOException('Unable to run script', e)
                }
            } finally {
                if (engine.groovyClassLoader instanceof Closeable) {
                    (engine.groovyClassLoader as Closeable).close()
                }
            }
        } finally {
            if (groovyClassLoader instanceof Closeable) {
                (groovyClassLoader as Closeable).close()
            }
        }
    }

    protected CompilerConfiguration createCompilerConfiguration(String baseClassName) {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT)
        config.scriptBaseClass = baseClassName

        // Import some constants/enums so that user doesn't have to.
        ImportCustomizer icz = new ImportCustomizer()
        icz.addStaticImport(ContainerContext.Type.class.name, 'DOCKER')
        icz.addStaticImport(DockerContext.Network.class.name, 'BRIDGE')
        icz.addStaticImport(DockerContext.Network.class.name, 'HOST')
        icz.addStaticImport(ContainerVolumeContext.Mode.class.name, 'RO')
        icz.addStaticImport(ContainerVolumeContext.Mode.class.name, 'RW')
        icz.addStaticImport(HealthCheckContext.Protocol.class.name, 'COMMAND')
        icz.addStaticImport(HealthCheckContext.Protocol.class.name, 'HTTP')
        icz.addStaticImport(HealthCheckContext.Protocol.class.name, 'TCP')
        config.addCompilationCustomizers(icz)

        config
    }

}
