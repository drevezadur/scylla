package io.drevezerezh.scylla.advanced.domain.impl

interface IdProvider {

    fun createId() : String

}