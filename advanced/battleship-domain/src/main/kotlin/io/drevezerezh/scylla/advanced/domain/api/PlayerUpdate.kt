package io.drevezerezh.scylla.advanced.domain.api

data class PlayerUpdate(
    val name: String? = null
) {
    fun isEmpty(): Boolean {
        return name == null
    }
}
