package com.fr.bi.fe.fs.data;

public class FineExcelUserFeedback {

	private int id;
	private String email;
	private long userId;
	private int [] vote;
	private String description;
	private String fullFileName;
	private String picture;
	private int agree;
	private int status;
	private int isPublic;
	private String adminReply;
	private String adminReplyImage;
	private int adminReplyAgree;
	private int adminReplyDisagree;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int[] getVote() {
		return vote;
	}
	public void setVote(int[] vote) {
		this.vote = vote;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFullFileName() {
		return fullFileName;
	}
	public void setFullFileName(String fullFileName) {
		this.fullFileName = fullFileName;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public int getAgree() {
		return agree;
	}
	public void setAgree(int agree) {
		this.agree = agree;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}
	public String getAdminReply() {
		return adminReply;
	}
	public void setAdminReply(String adminReply) {
		this.adminReply = adminReply;
	}
	public String getAdminReplyImage() {
		return adminReplyImage;
	}
	public void setAdminReplyImage(String adminReplyImage) {
		this.adminReplyImage = adminReplyImage;
	}
	public int getAdminReplyAgree() {
		return adminReplyAgree;
	}
	public void setAdminReplyAgree(int adminReplyAgree) {
		this.adminReplyAgree = adminReplyAgree;
	}
	public int getAdminReplyDisagree() {
		return adminReplyDisagree;
	}
	public void setAdminReplyDisagree(int adminReplyDisagree) {
		this.adminReplyDisagree = adminReplyDisagree;
	}
	
	
}