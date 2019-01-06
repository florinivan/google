package com.phimes.pubsub;

public class GoogleTopicDto {

	private String projectId;
	private String topicId = "TopicId";

	public GoogleTopicDto(String projectId, String topicId) {
		super();
		this.projectId = projectId;
		this.topicId = topicId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

}
