package com.heypudu.heypudu.network

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun retrofitClient_isNotNull() {
        // Assert
        assertThat(RetrofitClient.retrofit).isNotNull()
    }

    @Test
    fun retrofitClient_hasCorrectBaseUrl() {
        // Assert
        assertThat(RetrofitClient.retrofit.baseUrl().toString())
            .isEqualTo("https://newsapi.org/")
    }

    @Test
    fun createService_returnsService() {
        // Act
        val service = RetrofitClient.createService<ApiService>()

        // Assert
        assertThat(service).isNotNull()
        assertThat(service).isInstanceOf(ApiService::class.java)
    }

    @Test
    fun createService_createsValidInstance() {
        // Act
        val service1 = RetrofitClient.createService<ApiService>()
        val service2 = RetrofitClient.createService<ApiService>()

        // Assert
        assertThat(service1).isNotNull()
        assertThat(service2).isNotNull()
    }
}

