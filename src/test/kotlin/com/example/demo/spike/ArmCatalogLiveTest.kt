package com.example.demo.spike

import com.example.demo.ArmCatalog
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Tag("live")
@SpringBootTest
class ArmCatalogLiveTest {
    @Autowired lateinit var catalog: ArmCatalog

    @Test fun `arms resolve from the real environment`() {
        println("ARMS enabled=${catalog.arms.map { it.id }} disabled=${catalog.disabled()}")
    }
}
