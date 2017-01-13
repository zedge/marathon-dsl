---
keywords: marathon, dsl, docker, deployment
---
# Marathon Application DSL

The Marathon Application DSL is a Groovy-based Domain Specific Language for
defining Marathon applications.

If you are not familiar with Groovy already, please check out the
"Quick Notes on Groovy" section, which tries to de-mystify some of
the syntactical tricks Groovy employs.

## Table of Contents

* [Installing](#installing)
  * [From Source](#from-source)
* [Command Overview](#command-overview)
  * [marathon-dsl](#marathon-dsl)
* [DSL Examples](#dsl-examples)
  * [Hello, World!](#hello-world)
  * [Multi-port docker application](#multi-port-docker-application)
  * [Every Possible Option](#every-possible-option)
* [Quick Notes on Groovy](#quick-notes-on-groovy)
  * [Omitting Parentheses](#omitting-parentheses)
  * [Closures](#closures)
  * [Command Chains](#command-chains)
  * [Pre-Imported Symbols](#pre-imported-symbols)
* [Context Reference](#context-reference)
  * [AppContext](#appcontext)
  * [ConstraintContext](#constraintcontext)
  * [ContainerContext](#containercontext)
  * [ContainerVolumeContext](#containervolumecontext)
  * [DockerContext](#dockercontext)
  * [DockerPortMappingContext](#dockerportmappingcontext)
  * [HealthCheckContext](#healthcheckcontext)
  * [PortDefinitionContext](#portdefinitioncontext)
  * [UpgradeStrategyContext](#upgradestrategycontext)

## Installing

### From Source

You can build and run the `marathon-dsl` command by typing `./gradlew run`.

## Command Overview

### marathon-dsl

This command generates a JSON app manifest based on a Groovy DSL file. It will always generate a
JSON array of objects, which is suitable for PUT or POST requests to Marathon's `v2/apps` endpoint.

## DSL Examples

### Hello, World!

Create a file called `marathon.groovy`:
```groovy
app('hello-world') {
    container {
        docker {
            image 'nginx'
            portMapping {
                containerPort 80
            }
        }
    }
    healthCheck HTTP portIndex 0
    cpus 0.1
    mem 128
}
```

Convert to JSON by running `marathon-dsl marathon.groovy`. You can then
deploy by curling this to the Marathon API:

```sh
marathon-dsl marathon.groovy | \
    curl -X PUT -H 'Content-type: application/json' -d@- \
    http://marathon-leader.example.com:8080/v2/apps
```

### Multi-port docker application

In this example, we have an application that exposes two ports, 8080
(for serving traffic) and 8081 (as an admin port). Here we want to run
health checks against the admin port.

```groovy
app('myapp') {
    container {
        docker {
            image 'docker.example.com/my/repo'
            network BRIDGE // this is the default
            portMapping {
                containerPort 8080
            }
			portMapping {
				containerPort 8081
			}
        }
    }
    healthCheck HTTP portIndex 1
    instances 5
    cpus 0.5 // this is the amount needed per instance
    mem 512  // ditto
    upgradeStrategy {
        // ok to run at 80% capacity (4 instances) during deploys
        minimumHealthCapacity 0.8
        // also ok to run at up to 150% capacity during deploys 
        maximumOverCapacity 0.5
    }
    env LOG_LEVEL: 'INFO'
}
```

### Every Possible Option

This is not a working example, but contains every possible variation of
every option provided. The names of the various parameters are the same
as in the Marathon API, so please refer to that for a detailed description
for each field: https://mesosphere.github.io/marathon/docs/rest-api.html

```groovy
app('everything') {
    cmd '/bin/something'
    args '-v', '-D', 'FOO=BAR'
    args ['-v', '-D', 'FOO=BAR']
    cpus 1.5  // cores
    mem 1024  // MB
    portDefinition {  // may be repeated
        name 'http'
        protocol 'tcp'
        port 8080
    }
    requirePorts true
    instances 5
    container {
        docker {
            image 'mysql:5.6'
            network BRIDGE  // HOST or BRIDGE (default)
            forcePullImage true  // careful with this!
            portMapping { // may be repeated
                containerPort 80
                hostPort 0  // the default
                servicePort 10001
                protocol 'tcp'
                name 'http'  // so we can refer to it by name rather than index
            }
            privileged true
            parameter 'key', 'value'  // String,String variant
            parameters key: 'value', key2: 'value2'  // Map variant
        }
        volume {  // may be repeated
            hostPath '/shared/mysql/db0'
            containerPath '/var/db/mysql'
            mode RW  // RO or RW (default)
        }
    }
    env 'VAR', 'VALUE'  // String,String variant
    env VAR: 'VALUE',   // Map variant
        VAR2: 'VALUE2'
    constraint 'hostname' unique()
    constraint 'hostname' groupBy 3
    constraint('hostname') { unique() }
    constraint('hostname') { groupBy(3) }
    constraint 'hostname' like 'con0[123].*'
    constraint 'hostname' unlike 'db[0-9].*'
    constraint { cluster 'rack_id', 'b6' }
    healthCheck TCP portName 'http'
    healthCheck HTTP portIndex 0 intervalSeconds 5
    healthCheck {
        protocol TCP // TCP, HTTP or COMMAND
        http '/path' // sets protocol to HTTP
        path '/path'
        portName 'name'
        portIndex 0
    }
    healthCheck(HTTP) {
        path '/path'
		portName 'name'
		portIndex 0
    }
	healthCheck {
		command '/usr/local/bin/service_health_check' // executed on the agent
		gracePeriodSeconds 300
		intervalSeconds 5
		timeoutSeconds 2
		maxConsecutiveFailures 2
	}
	upgradeStrategy {
	    minimumHealthCapacity 1.0
		maximumOverCapacity 1.0
	}
	upgradeStrategy minimumHealthCapacity: 1.0, maximumOverCapacity: 1.0
	privileged() // true is the default parameter value
	privileged false
	backoffSeconds 1
	maxLaunchDelaySeconds 1800
	label 'traefik.enable', 'true' // add a label
	labels FOO: 'bar', BAZ: 'gazonk' // overwrite all labels
	acceptedResourceRoles 'public_agent' // add a resource role
}
```

## Quick Notes on Groovy

This section on Groovy is meant to make you better able to understand
what the DSL is actually doing as you write it.

### Omitting Parentheses

If a Groovy method has one or more parameters, its enclosing parens may
be omitted, meaning that `foo('bar')` is equivalent to `foo 'bar'`.  Note
that this does not apply if there are no parameters, in that case you
would always have to write `foo()`.  If you write `foo` Groovy will
interpret that as reading the `foo` property.

Groovy's map literals can also be abbreviated in a similar way:
the map literal `[a: 1, b: 2]` may be expressed as `a: 1, b: 2`.

### Closures

The DSL relies heavily on Groovy closures, which are code blocks that
are run in some kind of context, and with runtime symbol resolution.

So when you see something like `docker { image 'foo' }`, that is
actually a call to the `docker` method with the closure `{ image 'foo' }`
as the single parameter.

In the DSL, these closures are evaluated in a _context_, which basically
is an instance of a specific class, providing the methods you have
available in the closure.  We will go through the different contexts
below.

### Command Chains

One more shortcut that Groovy provides which is useful for DSLs is
for chaining of calls. For example, these are equivalent statements:
```groovy
foo('bar').gazonk(42)
foo('bar') gazonk(42)
foo 'bar' gazonk 42
```
Again, notice that you may omit the parens if there is a single parameter.
If `gazonk()` were without parameters, it would have to be
`foo 'bar' gazonk()` instead.

### Pre-Imported Symbols

For your convenience, a few symbols have been imported (in the Java /
Groovy `import` sense) in advance when evaluating the DSL script.
So when you see references to for example `BRIDGE`, that's no more magic
than a statically-imported symbol. Marathon-DSL uses these symbols instead of
literal strings to catch errors during compilation rather than when trying
to submit the application to Marathon.

## Context Reference

Groovy closures may be bound to a context when they are run. All of the closures
used in the Marathon DSL do this.

Just as a heads-up, it is worth noting that Groovy resolves symbols at runtime,
and any property or method available in an outer closure will be available in
those that are contained in it.  For example, these two examples will produce
the same output:

```groovy
app('foo') {
  container {
    docker {
    }
    label('foo', 'bar')
  }
}
```

...and...

```groovy
app('foo') {
  container {
    docker {
    }
    label('foo', 'bar')
  }
}
```

The ContainerContext does not define a `label()` method, but since exists in the
AppContext, that one will be called.  If the label call is moved to within the docker
closure however, the result will be different, because DockerContext has a label method.

### AppContext

This is the outermost context, and the closure passed to `app()` is run in this one.

Methods:

* `cmd(String)`
* `args(List<String>)`
* `args(String...)`
* `cpus(double)`
* `mem(double)`
* `portDefinition(Closure)` - runs in [PortDefinitionContext](#PortDefinitionContext)
* `requirePorts(boolean)`
* `instances(int)`
* `executor(String)`
* `container(ContainerContext.Type,Closure)` - runs in [ContainerContext](#ContainerContext)
* `container(Closure)` - runs in [ContainerContext](#ContainerContext)
* `env(String,String)`
* `env(Map)`
* `constraint(String)` - returns [ConstraintContext](#ConstraintContext)
* `constraint(String,Closure)` - runs in and returns [ConstraintContext](#ConstraintContext)
* `healthCheck(HealthCheckContext.Protocol)` - returns [HealthCheckContext](#HealthCheckContext)
* `healthCheck(Closure)` - runs in and returns [HealthCheckContext](#HealthCheckContext)
* `healthCheck(HealthCheckContext.Protocol,Closure)` - runs in and returns [HealthCheckContext](#HealthCheckContext)
* `upgradeStrategy(Closure)` - runs in [UpgradeStrategyContext](#UpgradeStrategyContext)
* `upgradeStrategy(Map)`
* `privileged(boolean)`
* `backoffSeconds(double)`
* `backoffFactor(double)`
* `maxLaunchDelaySeconds(int)`
* `label(String,String)`
* `labels(Map)`
* `acceptedResourceRoles(String...)`

### ConstraintContext

The ConstraintContext has a constructor that takes an optional
attribute name. This is given through the string parameter to
`constraint()` above, and will act as a default attribute/field value
for all of the methods in this context.

* `unique([String])` - defaults to the constructor attribute name
* `cluster(String,String)`
* `cluster(String)` - uses constructor attribute name
* `groupBy(int)` - uses constructor attribute name
* `groupBy(String,int)`
* `like(String)`
* `unlike(String)`

### ContainerContext

Methods:

* `type(ContainerContext.Type)` - ContainerContext.Type may only be `DOCKER` currently
* `docker(Closure)` - implies `type(DOCKER)` - see [DockerContext](#DockerContext)
* `volume(Closure)` - see [ContainerVolumeContext](#ContainerVolumeContext)

### ContainerVolumeContext

Methods:

* `containerPath(String)`
* `hostPath(String)`
* `mode(ContainerVolumeContext.Mode)` - one of `Mode.RW` or `Mode.RO`

### DockerContext

Read more at https://mesosphere.github.io/marathon/docs/native-docker.html

Methods:

* `image(String)`
* `network(DockerContext.Network)` - `HOST` or `BRIDGE` (default)
* `forcePullImage([boolean])` - defaults to true (but only if forcePullImage() is specified in the first place)
* `portMapping(Closure)` - see [DockerPortMappingContext](#DockerPortMappingContext)
* `privileged([boolean])` - defaults to true (but only if privileged() is specified in the first place)
* `parameter(String,String)` - set a docker `--parameter` to a value, omitting the `--`
* `parameters(Map)` - set all docker parameters

### DockerPortMappingContext

Read more at https://mesosphere.github.io/marathon/docs/native-docker.html#bridged-networking-mode

Methods:

* `name(String)`
* `containerPort(int)`
* `hostPort(int)`
* `servicePort(int)`

### HealthCheckContext

Read more at https://mesosphere.github.io/marathon/docs/health-checks.html

Methods:

* `protocol(HealthCheckContext.Protocol)` - returns [HealthCheckContext](#HealthCheckContext)
* `protocol(String)` - returns [HealthCheckContext](#HealthCheckContext)
* `command(String)` - returns [HealthCheckContext](#HealthCheckContext)
* `tcp(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `http(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `http(String)` - returns [HealthCheckContext](#HealthCheckContext)
* `path(String)` - returns [HealthCheckContext](#HealthCheckContext)
* `gracePeriodSeconds(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `intervalSeconds(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `portName(String)` - returns [HealthCheckContext](#HealthCheckContext)
* `portIndex(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `timeoutSeconds(int)` - returns [HealthCheckContext](#HealthCheckContext)
* `maxConsecutiveFailures(int)` - returns [HealthCheckContext](#HealthCheckContext)

### PortDefinitionContext

Read more at https://mesosphere.github.io/marathon/docs/ports.html

Methods:

* `name(String)`
* `port(int)`
* `protocol(String)`

### UpgradeStrategyContext

Read more at https://mesosphere.github.io/marathon/docs/deployments.html#rolling-restarts

Methods:

* `minimumHealthCapacity(double)`
* `maximumOverCapacity(double)`
