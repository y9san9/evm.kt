package evm

public suspend fun main() {
    val infuraUrl = System.getenv("INFURA_URL")
    val client = EvmClient(infuraUrl)
    val number = EvmInteger(600)
    println(number.toHexString())
    println(EvmKeccak256.digest(EvmHex.orThrow("0x")))
    val result = main(client)
    print("Main result: ")
    println(result)
}

// https://sepolia.etherscan.io/address/0xfFf9976782d46CC05630D1f6eBAb18b2324d6B14
private suspend fun main(executor: EvmClient): EvmTry<Unit> {
    val contract =
        EvmAddress.orThrow("0xfFf9976782d46CC05630D1f6eBAb18b2324d6B14")
    val data = EvmAbi.encodeCallData(
        signature = EvmAbi.signature("balanceOf(address)"),
        parameters = listOf(EvmAbi.Parameter.Address(contract)),
    )
    // EvmCallData.orThrow("0x70a08231")
    println(data)
    val call = EvmCall(
        to = contract,
        data = data,
    )
    val result = executor.call(call, Latest).or {
        return EvmTry.Fail(it)
    }
    println(EvmAbi.decodeCallResult(result, EvmAbi.Descriptor.UInt256))
    return EvmTry.Ok(Unit)
}
