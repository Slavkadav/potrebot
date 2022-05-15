package ru.abe.slaves.potrebot

import lombok.RequiredArgsConstructor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@RequiredArgsConstructor
open class PotrebotApplication

fun main(args: Array<String>) {
    runApplication<PotrebotApplication>(*args)
}
