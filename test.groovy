app('/foobar') {
    container(DOCKER) {
        docker {
            image "foo/bar:1.0"
        }
    }
    healthCheck { command '/bin/true' }
    instances 1
    cpus 0.1
    mem 256
    env LOG_LEVEL: 'INFO'
}
