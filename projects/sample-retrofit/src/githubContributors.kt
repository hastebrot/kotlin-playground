import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import okhttp3.Headers
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

fun main(args: Array<String>) {
    // retrofit instance.
    val retrofit = buildGithubRetrofit()
    val mockRetrofit = buildGithubRetrofitMock(retrofit)

    // api service and behaviour delegate.
    val service = retrofit.create(GitHubService::class.java)
    val serviceDelegate = mockRetrofit.create(GitHubService::class.java)

    sampleRetrofitService(service)
    sampleRetrofitServiceDelegate(serviceDelegate)
}

private fun sampleRetrofitService(service: GitHubService) {
    val call = service.contributors("square", "retrofit")

    val response = call.execute()
    response.body().forEach { println(it) }
}

private fun sampleRetrofitServiceDelegate(serviceDelegate: BehaviorDelegate<GitHubService>) {
    val responseBody = listOf(
        Contributor("user1", 1),
        Contributor("user2", 2)
    )
    val call = serviceDelegate
        .returning(Calls.response(
            Response.success(responseBody, Headers.of("key", "value"))
        ))
        .contributors("owner", "repo")

    val response = call.execute()
    println("response code: " + response.code())
    println("response header key: " + response.raw().header("key"))
    response.body().forEach { println(it) }
}

private interface GitHubService {
    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(@Path("owner") owner: String,
                     @Path("repo") repo: String): Call<List<Contributor>>
}

private data class Contributor(
    var login: String? = null,
    var contributions: Int? = null
)

private fun buildGithubRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}

private fun buildGithubRetrofitMock(retrofit: Retrofit): MockRetrofit {
    val networkBehavior = NetworkBehavior.create().apply {
        setFailurePercent(0)
        setDelay(0, TimeUnit.MILLISECONDS)
        setVariancePercent(0)
    }
    return MockRetrofit.Builder(retrofit)
        .networkBehavior(networkBehavior)
        .backgroundExecutor(Executors.newSingleThreadExecutor())
        .build()
}
