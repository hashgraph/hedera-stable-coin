package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicResponse;
import com.hedera.hashgraph.stablecoin.proto.IncreaseAllowanceTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Instant;

public class TopicListenerTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void topicListenerTest() {
        topicListener.startListening();
        topicListener.stopListening();
    }
}
