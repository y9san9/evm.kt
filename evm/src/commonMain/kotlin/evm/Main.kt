package evm

public suspend fun main() {
    val infuraUrl = System.getenv("INFURA_URL")
    val client = EvmClient(infuraUrl)
    val number = EvmNumber.orThrow("600")
    println(number.hex().stringWithPrefix)
    val result = client.use { executor -> main(executor) }
    print("Main result: ")
    println(result)
}

// https://sepolia.etherscan.io/address/0xfFf9976782d46CC05630D1f6eBAb18b2324d6B14
private suspend fun main(executor: EvmExecutor) {
    val contract = EvmAddress(
        hex = EvmHex.orThrow("0xfFf9976782d46CC05630D1f6eBAb18b2324d6B14"),
    )
    val call = EvmCall(
        to = contract,
    )
    val result = executor.call(call, Latest)
    println(result)
}
