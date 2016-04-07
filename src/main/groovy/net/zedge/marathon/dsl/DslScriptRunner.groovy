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
class DslScriptRunner {

    static final String BASECLASS_PROPERTY = 'net.zedge.marathon.dsl.baseclass'

    static DslScript runDslScript(File scriptFile, ClusterConfig marathonConfig) {
        Binding binding = new Binding()
        binding.setVariable('marathonConfig', marathonConfig)
        binding.setVariable('cluster', marathonConfig.name)
        String baseClassName = System.getProperty(BASECLASS_PROPERTY) ?: DslScript.class.getName()
        runDslScript(scriptFile, binding, baseClassName)
    }

    static DslScript runDslScript(File scriptFile, Binding binding, String baseClassName) {

        ClassLoader parentClassLoader = DslScriptRunner.classLoader
        String scriptBody = scriptFile.text
        CompilerConfiguration config = createCompilerConfiguration(baseClassName)
        // Otherwise baseScript won't take effect
        def compilerConfig = createCompilerConfiguration(baseClassName)
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(parentClassLoader, compilerConfig)
        try {
            GroovyScriptEngine engine = new GroovyScriptEngine('.', groovyClassLoader)
            try {
                engine.config = config
                try {
                    Script script
                    String scriptBaseName = scriptFile.name.replaceAll(/[^a-zA-Z_]/, '_')
                    Class cls = engine.groovyClassLoader.parseClass(scriptBody, scriptBaseName)
                    script = InvokerHelper.createScript(cls, binding)
                    assert script instanceof DslScript
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
                    ((Closeable) engine.groovyClassLoader).close()
                }
            }
        } finally {
            if (groovyClassLoader instanceof Closeable) {
                ((Closeable) groovyClassLoader).close()
            }
        }
    }

    private static CompilerConfiguration createCompilerConfiguration(String baseClassName) {
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
