package io.drevezadur.scylla.teacher.client.service.model

import io.drevezadur.scylla.teacher.restserver.lang.ShotResult

data class ShotReportPojo(
    val shotResult: ShotResult,
    val winner: Boolean = false
)