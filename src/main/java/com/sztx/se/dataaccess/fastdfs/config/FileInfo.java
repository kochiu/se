package com.sztx.se.dataaccess.fastdfs.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class FileInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3442991289784754380L;

	public static final String KEY_HEIGHT = "height";

	public static final String KEY_WIDTH = "width";

	public static final String KEY_SIZE = "size";

	public static final String KEY_AUTHOR = "author";

	private String fileId;

	/**
	 * 
	 */
	private byte[] content;

	private long size;

	private Map<String, String> extraInfo;

	private String author;

	public FileInfo() {

	}

	public FileInfo(String fileId, byte[] content, long size, String author, Map<String, String> extraInfo) {
		this.fileId = fileId;
		this.content = content;
		this.size = size;
		this.author = author;
		this.extraInfo = extraInfo;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Map<String, String> getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(Map<String, String> extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "FileInfo [fileId=" + fileId + ", content=" + Arrays.toString(content) + ", size=" + size + ", extraInfo=" + extraInfo + ", author=" + author
				+ "]";
	}

}
