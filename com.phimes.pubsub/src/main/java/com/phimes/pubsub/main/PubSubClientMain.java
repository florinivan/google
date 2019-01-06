package com.phimes.pubsub.main;

import com.google.cloud.ServiceOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.phimes.common.Conversioni;
import com.phimes.pubsub.GoogleTopicDto;
import com.phimes.pubsub.PublisherMessage;
import com.phimes.pubsub.SubscribeMessage;

public class PubSubClientMain {

	// use the default project id
	private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

	public static void main(String... args) throws Exception {
		// topic id, eg. "my-topic"

		GoogleTopicDto topicInfo = new GoogleTopicDto(PROJECT_ID, "TopicId");
		// ByteString data = ByteString.copyFromUtf8(message);
		String imageBase64 = Conversioni.convertFromImage("C:\\Users\\madad\\Pictures\\CartaIdentità.jpg");

		String idMessage2 = "ONEPLUSX12";
		String idMessage = "DEVICEID";
		ByteString data = ByteString.copyFromUtf8(imageBase64);
		PubsubMessage pubsubMessage = PubsubMessage.newBuilder().putAttributes(idMessage, idMessage2).setData(data)
				.build();
		PublisherMessage pbM = PublisherMessage.getInstance(topicInfo);
		pbM.publish(pubsubMessage);

		SubscribeMessage sm = SubscribeMessage.getInstance(topicInfo, "SubscriptionId");
		sm.startSubscribe();
		try {
			while (true) {

				PubsubMessage message = sm.getMessage();
				System.out.println("Message Id: " + message.getMessageId());
				System.out.println("Device: " + message.getAttributesOrDefault("DEVICEID", null));
				System.out.println("Detected text " + message.getAttributesOrDefault("DOCUMENT_TEXT_DETECTION", null));
			}
		} finally {
			sm.stopSubscribe();
		}
	}

}
