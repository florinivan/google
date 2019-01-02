/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phimes.pubsub;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

public class SubscribeMessage {

	private static SubscribeMessage instance = null;
	private Subscriber subscriber;
	private PubsubMessage message;

	private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

	static class MessageReceiverExample implements MessageReceiver {

		@Override
		public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
			messages.offer(message);
			consumer.ack();
		}
	}

	private SubscribeMessage(GoogleTopicDto googleTopicDto, String subscriptionId) {
		// create a subscriber bound to the asynchronous message receiver

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(googleTopicDto.getProjectId(),
				subscriptionId);
		this.subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
	}

	public synchronized static SubscribeMessage getInstance(GoogleTopicDto googleTopicDto, String subscriptionId) {
		if (instance == null) {
			return new SubscribeMessage(googleTopicDto, subscriptionId);
		}
		return instance;

	}

	public void startSubscribe() {
		subscriber.startAsync().awaitRunning();
	}

	public void stopSubscribe() throws Exception {
		if (subscriber != null) {
			subscriber.stopAsync();
		} else
			throw (new Exception("subscriber is null"));

	}

	public PubsubMessage getMessage() throws InterruptedException {
		message = messages.take();
		return message;
	}

}
// [END pubsub_quickstart_subscriber]
