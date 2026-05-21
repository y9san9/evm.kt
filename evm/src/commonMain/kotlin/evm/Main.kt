package evm

private suspend fun main() {
    val infuraUrl = System.getenv("INFURA_URL")
    val client = EvmClient(infuraUrl)
    val result = client.use { executor ->
        executor.getBlockNumber()
    }
    println(result)
}
