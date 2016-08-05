package com.kochiu.se.dataaccess.fastdfs.impl;

import java.util.Map;

import com.kochiu.se.dataaccess.fastdfs.BaseFastdfsDAO;
import com.kochiu.se.dataaccess.fastdfs.config.FileInfo;
import com.kochiu.se.dataaccess.fastdfs.source.DynamicFastdfsSource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseFastdfsDAOImpl implements BaseFastdfsDAO {

	@Autowired(required = false)
	protected DynamicFastdfsSource fastdfsClient;

	@Override
	public String upload(String filePath, String author, Map<String, String> extraInfo, boolean isImage) {
		String fileId = null;

		if (isImage) {
			fileId = fastdfsClient.uploadImage(filePath, author, extraInfo);
		} else {
			fileId = fastdfsClient.upload(filePath, author, extraInfo);
		}

		return fileId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends FileInfo> T download(String fileId, String localFilePath, boolean isImage) {
		T obj = null;

		if (isImage) {
			obj = (T) fastdfsClient.downloadImage(fileId, localFilePath);
		} else {
			obj = (T) fastdfsClient.download(fileId, localFilePath);
		}

		return obj;
	}

	@Override
	public boolean delete(String fileId, String localFilePath) {
		boolean flag = fastdfsClient.delete(fileId, localFilePath);
		return flag;
	}
}
