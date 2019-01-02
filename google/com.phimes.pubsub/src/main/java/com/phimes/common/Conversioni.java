package com.phimes.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Base64;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

public class Conversioni {

	/*
	 * try {
	 * 
	 * // Encode using basic encoder String base64encodedString =
	 * Base64.getEncoder().encodeToString("TutorialsPoint?java8".getBytes("utf-8"));
	 * System.out.println("Base64 Encoded String (Basic) :" + base64encodedString);
	 * 
	 * // Decode byte[] base64decodedBytes =
	 * Base64.getDecoder().decode(base64encodedString);
	 * 
	 * System.out.println("Original String: " + new String(base64decodedBytes,
	 * "utf-8")); base64encodedString =
	 * Base64.getUrlEncoder().encodeToString("TutorialsPoint?java8".getBytes("utf-8"
	 * )); System.out.println("Base64 Encoded String (URL) :" +
	 * base64encodedString);
	 * 
	 * StringBuilder stringBuilder = new StringBuilder();
	 * 
	 * for (int i = 0; i < 10; ++i) {
	 * stringBuilder.append(UUID.randomUUID().toString()); }
	 * 
	 * byte[] mimeBytes = stringBuilder.toString().getBytes("utf-8"); String
	 * mimeEncodedString = Base64.getMimeEncoder().encodeToString(mimeBytes);
	 * System.out.println("Base64 Encoded String (MIME) :" + mimeEncodedString);
	 * 
	 * } catch (UnsupportedEncodingException e) { System.out.println("Error :" +
	 * e.getMessage()); }
	 */
	public static String convertFromImage(String path) throws IOException {
		File file = new File(path);
		FileInputStream fileInputStreamReader = null;
		try {
			fileInputStreamReader = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = new byte[(int) file.length()];
		fileInputStreamReader.read(bytes);
		// Encode the image.
		byte[] imageData = Base64.encodeBase64(bytes);
		String base64ImgString = new String(imageData);
		return base64ImgString;
	}

	public static ByteString covertFromDataToByteString(String inputBase64Img) {

		byte[] byteArrayImg = Base64.decodeBase64(inputBase64Img);

		ByteString byteStringImg = ByteString.copyFrom(byteArrayImg);

		return byteStringImg;
	}

	public static java.util.List<EntityAnnotation> getAnnotationFromGoogleFeatures(ByteString imgBytes, Type feature)
			throws Exception {
		
		List <EntityAnnotation> retdAnnotationList = new ArrayList<>(); 
		List<AnnotateImageRequest> requests = new ArrayList<>();

		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(feature).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					throw new Exception("Error Annotate Image Response");
				}
				retdAnnotationList.addAll(res.getTextAnnotationsList());
			}
			return retdAnnotationList;
		}
	}

}
