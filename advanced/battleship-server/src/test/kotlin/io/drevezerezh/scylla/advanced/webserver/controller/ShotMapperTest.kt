package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.lang.ShotResult
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShotReportJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShotMapperTest {

    @Test
    fun `toDto shall convert a domain shot report to DTO`() {
        assertThat(
            ShotMapper.toDto(ShotReport(ShotResult.SUNK, true))
        ).isEqualTo(
            ShotReportJson(ShotResult.SUNK, true)
        )
    }
}