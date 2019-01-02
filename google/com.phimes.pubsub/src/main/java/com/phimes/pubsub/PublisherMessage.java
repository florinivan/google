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
// [START pubsub_quickstart_publisher]

//import com.github.simplesteph.protobuf.SimpleMessage;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

public class PublisherMessage {

	private static PublisherMessage instance= null;
	private GoogleTopicDto googleTopicDto;

	private PublisherMessage(GoogleTopicDto googleTopicDto) {
		this.googleTopicDto=googleTopicDto;
		
	}

	public synchronized static PublisherMessage getInstance(GoogleTopicDto googleTopicDto) {
		if (instance == null) {
			return new PublisherMessage(googleTopicDto);
		}
		return instance;
	}

	public void publish(PubsubMessage pubsubMessage) throws Exception {
		Publisher publisher = null;
		try {
			// Create a publisher instance with default settings bound to the topic
			ProjectTopicName topicName = ProjectTopicName.of(googleTopicDto.getProjectId(), googleTopicDto.getTopicId());
			publisher = Publisher.newBuilder(topicName).build();

			// schedule a message to be published, messages are automatically batched
			ApiFuture<String> future = publisher.publish(pubsubMessage);

			// add an asynchronous callback to handle success / failure
			ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

				@Override
				public void onFailure(Throwable throwable) {
					if (throwable instanceof ApiException) {
						ApiException apiException = ((ApiException) throwable);
						// details on the API exception
						System.out.println(apiException.getStatusCode().getCode());
						System.out.println(apiException.isRetryable());
					}
				}

				@Override
				public void onSuccess(String messageId) {
					// Once published, returns server-assigned message ids (unique within the topic)
					System.out.println("Messaggio "+messageId+ " pubblicato con successo");
				}
			});

		} finally {
			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				publisher.shutdown();
			}
		}

	}

}
// [END pubsub_quickstart_quickstart]
