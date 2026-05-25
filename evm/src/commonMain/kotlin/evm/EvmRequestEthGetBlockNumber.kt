package evm

public object EvmRequestEthGetBlockNumber {
    public fun create(): EvmRequest<EvmNumber> = buildEvmRequest {
        method = "eth_getBlockNumber"
        handler {
            response(ResponseConverter)
        }
    }

    private object ResponseConverter : EvmRequest.ResponseConverter<EvmHexSerializable, EvmNumber> {
        override fun convert(serializable: EvmHexSerializable) =
            serializable.typed()
    }
}
