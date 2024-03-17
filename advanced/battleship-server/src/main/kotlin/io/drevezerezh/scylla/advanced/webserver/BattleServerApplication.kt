package io.drevezerezh.scylla.advanced.webserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.drevezerezh.scylla.advanced"])
class WebServerApplication

fun main(args: Array<String>) {
    runApplication<WebServerApplication>(*args)
}
