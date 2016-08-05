package com.kochiu.se.dataaccess.fastdfs;

import java.util.Map;

import com.kochiu.se.dataaccess.fastdfs.config.FileInfo;

/**
 * 
 * @author zhihongp
 *
 */
public interface BaseFastdfsDAO {

	/**
	 * 上传文件到fastdfs中
	 * 
	 * @param filePath
	 * @param author
	 * @param extraInfo
	 * @param isImage
	 * @return
	 */
	String upload(String filePath, String author, Map<String, String> extraInfo, boolean isImage);

	/**
	 * 从fastdfs中下载文件
	 * 
	 * @param fileId
	 * @param localFilePath
	 * @param isImage
	 * @return
	 */
	<T extends FileInfo> T download(String fileId, String localFilePath, boolean isImage);

	/**
	 * 从fastdfs中删除文件
	 * 
	 * @param fileId
	 * @param localFilePath
	 * @param isImage
	 * @return
	 */
	boolean delete(String fileId, String localFilePath);
}
