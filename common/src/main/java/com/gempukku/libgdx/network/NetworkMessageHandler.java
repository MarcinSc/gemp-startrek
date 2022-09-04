package com.gempukku.libgdx.network;

public interface NetworkMessageHandler<T> {
    NetworkMessage<T> convertToNetworkMessage(Object data);
}
