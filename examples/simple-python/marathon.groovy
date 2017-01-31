def exposeExternally(String serviceName) {
    
}

app("/simple-python-example") {
    cpus 0.1
    mem 256.0
    container {
        docker {
            image "10.240.240.10/example/simple-python:${gitHash()}"
            portMapping {
                containerPort 8080
            }
        }
    }
    healthCheck {
        http "/"
        timeoutSeconds 10
    }
    upgradeStrategy {
        minimumHealthCapacity 1.0
        maximumOverCapacity 1.0
    }
}
