package io.drevezerezh.scylla.advanced.domain.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*

@Service
@Qualifier("defaultIdProvider")
class DefaultIdProvider : IdProvider{

    override fun createId(): String {
        return UUID.randomUUID().toString()
    }
}