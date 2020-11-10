package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import org.junit.jupiter.api.Test;

public class TopicListenerTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void topicListenerTest() {
        topicListener.startListening();
        topicListener.stopListening();
    }
}
