package com.phimes.pubsub.main;

import java.util.List;

import com.google.cloud.ServiceOptions;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.phimes.common.Conversioni;
import com.phimes.pubsub.GoogleTopicDto;
import com.phimes.pubsub.PublisherMessage;
import com.phimes.pubsub.SubscribeMessage;

public class PubSubServerMain {

	private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

	public static void main(String... args) throws Exception {

		GoogleTopicDto gtd = new GoogleTopicDto(PROJECT_ID, "TopicId");
		SubscribeMessage sm = SubscribeMessage.getInstance(gtd, "SubscriptionId");

		try {

			sm.startSubscribe();

			// Continue to listen to messages
			while (true) {

				PubsubMessage message = sm.getMessage();
				System.out.println("Message Id: " + message.getMessageId());
				System.out.println("Device: " + message.getAttributesOrDefault("DEVICEID", null));
				System.out.println("Data: " + message.getData().toStringUtf8());

				ByteString byteStringForOcr = Conversioni.covertFromDataToByteString(message.getData().toStringUtf8());
				List<EntityAnnotation> listAnnotation = Conversioni.getAnnotationFromGoogleFeatures(byteStringForOcr,
						Type.DOCUMENT_TEXT_DETECTION);

				for (EntityAnnotation annotation : listAnnotation) {
					//System.out.println("Text: %s\n" + annotation.getDescription());
					message = message.toBuilder().putAttributes("DOCUMENT_TEXT_DETECTION", annotation.getDescription()).build();
					break;
					// System.out.println("Position : %s\n" + annotation.getBoundingPoly());
				}

				PublisherMessage pm = PublisherMessage.getInstance(gtd);
				
				System.out.println("DOCUMENT_TEXT_DETECTION: " + message.getAttributesOrDefault("DOCUMENT_TEXT_DETECTION", null));
				pm.publish(message);
			}
		} finally {
			sm.stopSubscribe();
		}
		
	}

}
