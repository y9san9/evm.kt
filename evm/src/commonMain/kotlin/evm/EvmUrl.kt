package evm

@JvmInline
public value class EvmUrl(
    public val string: String,
) {
    public operator fun div(string: String): EvmUrl {
        require(!string.startsWith("/"))
        val prefix = if (this.string.endsWith("/")) "" else "/"
        return EvmUrl("${this.string}$prefix$string")
    }
}
