package com.piashcse.appscheduler.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppInfoTest {

    @Test
    fun `toSerializable converts AppInfo correctly`() {
        // Given
        val appInfo = AppInfo(
            packageName = "com.test.app",
            appName = "Test App",
            icon = null
        )

        // When
        val serializable = appInfo.toSerializable()

        // Then
        assertThat(serializable.packageName).isEqualTo("com.test.app")
        assertThat(serializable.appName).isEqualTo("Test App")
    }

    @Test
    fun `toAppInfo converts SerializableAppInfo correctly`() {
        // Given
        val serializableAppInfo = SerializableAppInfo(
            packageName = "com.test.app",
            appName = "Test App"
        )

        // When
        val appInfo = serializableAppInfo.toAppInfo()

        // Then
        assertThat(appInfo.packageName).isEqualTo("com.test.app")
        assertThat(appInfo.appName).isEqualTo("Test App")
        assertThat(appInfo.icon).isNull()
    }

    @Test
    fun `conversion round trip maintains data integrity`() {
        // Given
        val originalAppInfo = AppInfo(
            packageName = "com.example.test",
            appName = "Example Test App",
            icon = null
        )

        // When
        val convertedBack = originalAppInfo.toSerializable().toAppInfo()

        // Then
        assertThat(convertedBack.packageName).isEqualTo(originalAppInfo.packageName)
        assertThat(convertedBack.appName).isEqualTo(originalAppInfo.appName)
        assertThat(convertedBack.icon).isNull() // Icon is always null after conversion
    }
}