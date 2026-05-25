package evm

private suspend fun main() {
    val infuraUrl = System.getenv("INFURA_URL")
    val client = EvmClient(infuraUrl)
    val number = EvmNumber.orThrow("600")
    println(number.toHexString())
    val result = client.use { executor ->
        executor.getBlockNumber()
    }
    println(result)
}
