package com.sztx.se.dataaccess.fastdfs.source;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.sztx.se.common.exception.SystemException;
import com.sztx.se.common.util.FileUtil;
import com.sztx.se.common.util.ImageUtil;
import com.sztx.se.dataaccess.fastdfs.client.FastdfsClient;
import com.sztx.se.dataaccess.fastdfs.client.FastdfsClientProxy;
import com.sztx.se.dataaccess.fastdfs.config.FileInfo;
import com.sztx.se.dataaccess.fastdfs.config.ImageInfo;

/**
 * 动态mongodb
 * 
 * @author zhihongp
 * @param <T>
 * 
 */
public class DynamicFastdfsSource {

	private static final String AUTHOR = "berbon";

	private FastdfsClientProxy defaultTargetFastdfsSource;

	private Map<String, FastdfsClientProxy> targetFastdfsSources;

	public FastdfsClientProxy getDefaultTargetFastdfsSource() {
		return defaultTargetFastdfsSource;
	}

	public void setDefaultTargetFastdfsSource(FastdfsClientProxy defaultTargetFastdfsSource) {
		this.defaultTargetFastdfsSource = defaultTargetFastdfsSource;
	}

	public Map<String, FastdfsClientProxy> getTargetFastdfsSources() {
		return targetFastdfsSources;
	}

	public void setTargetFastdfsSources(Map<String, FastdfsClientProxy> targetFastdfsSources) {
		this.targetFastdfsSources = targetFastdfsSources;
	}

	/**
	 * 上传一个文件
	 * 
	 * @param filePath 文件绝对路径
	 * @return
	 */
	public String upload(String filePath, Map<String, String> extraInfo) {
		return upload(filePath, AUTHOR, extraInfo);
	}

	/**
	 * 上传一个文件
	 * 
	 * @param filePath 文件绝对路径
	 * @param author 文件上传者
	 * @return
	 */
	public String upload(String filePath, String author, Map<String, String> extraInfo) {
		FastdfsClient fastdfsClient = getFastdfsClient();

		File file = new File(filePath);

		if (!file.exists()) {
			throw new SystemException("The file: " + filePath + " is not exist");
		}

		if (!file.isFile()) {
			throw new SystemException("The file: " + filePath + " is not a file");
		}

		if (extraInfo == null) {
			extraInfo = new HashMap<String, String>();
		}

		String fileExt = FileUtil.getFileSuffix(filePath);
		long size = FileUtil.getFileSize(filePath);

		if (size > 0l) {
			extraInfo.put(FileInfo.KEY_SIZE, String.valueOf(size));
		}

		if (StringUtils.isNotBlank(author)) {
			extraInfo.put(FileInfo.KEY_SIZE, String.valueOf(size));
		}

		return fastdfsClient.upload(filePath, fileExt, extraInfo);
	}

	/**
	 * 下载一个文件
	 * 
	 * @param fileId
	 * @return
	 */
	public FileInfo download(String fileId) {
		return download(fileId, null);
	}

	/**
	 * 下载一个文件，并将该文件保存到本地
	 * 
	 * @param fileId
	 * @param localFilePath
	 * @return
	 */
	public FileInfo download(String fileId, String localFilePath) {
		FastdfsClient fastdfsClient = getFastdfsClient();
		FileInfo fileInfo = null;

		if (StringUtils.isBlank(localFilePath)) {
			fileInfo = fastdfsClient.download(fileId);
		} else {
			fileInfo = fastdfsClient.download(fileId, localFilePath);
		}

		return fileInfo;
	}

	/**
	 * 删除一个文件
	 * 
	 * @param fileId
	 * @return
	 */
	public boolean delete(String fileId) {
		return delete(fileId, null);
	}

	/**
	 * 删除一个文件
	 * 
	 * @param fileId
	 * @param localFilePath
	 * @return
	 */
	public boolean delete(String fileId, String localFilePath) {
		FastdfsClient fastdfsClient = getFastdfsClient();
		boolean flag = false;

		if (StringUtils.isBlank(localFilePath)) {
			flag = fastdfsClient.deleteFile(fileId);
		} else {
			flag = fastdfsClient.deleteFile(fileId, localFilePath);
		}

		return flag;
	}

	/**
	 * 上传一个图片
	 * 
	 * @param filePath 文件绝对路径
	 * @return
	 */
	public String uploadImage(String filePath, Map<String, String> extraInfo) {
		return uploadImage(filePath, AUTHOR, extraInfo);
	}

	/**
	 * 上传一个图片
	 * 
	 * @param filePath 文件绝对路径
	 * @param author 文件上传者
	 * @return
	 */
	public String uploadImage(String filePath, String author, Map<String, String> extraInfo) {
		FastdfsClient fastdfsClient = getFastdfsClient();

		File file = new File(filePath);

		if (!file.exists()) {
			throw new SystemException("The file: " + filePath + " is not exist");
		}

		if (!file.isFile()) {
			throw new SystemException("The file: " + filePath + " is not a file");
		}

		if (extraInfo == null) {
			extraInfo = new HashMap<String, String>();
		}

		String fileExt = FileUtil.getFileSuffix(filePath);
		long size = FileUtil.getFileSize(filePath);
		int height = 0;
		int width = 0;

		try {
			height = ImageUtil.getHeight(filePath);
			width = ImageUtil.getWidth(filePath);
		} catch (IOException e) {
			throw new SystemException("Get Imgage height or width error", e);
		}

		if (height > 0) {
			extraInfo.put(FileInfo.KEY_HEIGHT, String.valueOf(height));
		}

		if (width > 0) {
			extraInfo.put(FileInfo.KEY_WIDTH, String.valueOf(width));
		}

		if (size > 0l) {
			extraInfo.put(FileInfo.KEY_SIZE, String.valueOf(size));
		}

		if (StringUtils.isNotBlank(author)) {
			extraInfo.put(FileInfo.KEY_SIZE, String.valueOf(size));
		}

		return fastdfsClient.upload(filePath, fileExt, extraInfo);
	}

	/**
	 * 下载一个图片
	 * 
	 * @param fileId
	 * @return
	 */
	public ImageInfo downloadImage(String fileId) {
		return downloadImage(fileId, null);
	}

	/**
	 * 下载一个图片，并将该图片保存到本地
	 * 
	 * @param fileId
	 * @param localFilePath
	 * @return
	 */
	public ImageInfo downloadImage(String fileId, String localFilePath) {
		FastdfsClient fastdfsClient = getFastdfsClient();
		FileInfo fileInfo = null;

		if (StringUtils.isBlank(localFilePath)) {
			fileInfo = fastdfsClient.download(fileId);
		} else {
			fileInfo = fastdfsClient.download(fileId, localFilePath);
		}

		if (fileInfo == null) {
			return null;
		} else {
			Map<String, String> extraInfo = fileInfo.getExtraInfo();
			Map<String, String> imageExtraInfo = null;
			int height = 0;
			int width = 0;

			if (extraInfo != null && extraInfo.size() > 0) {
				imageExtraInfo = new HashMap<String, String>();

				for (Entry<String, String> en : extraInfo.entrySet()) {
					String key = en.getKey();
					String value = en.getValue();

					if (key.equals(FileInfo.KEY_HEIGHT)) {
						height = Integer.valueOf(value);
					} else if (key.equals(FileInfo.KEY_WIDTH)) {
						width = Integer.valueOf(value);
					} else {
						imageExtraInfo.put(key, value);
					}
				}
			}

			ImageInfo imageInfo = new ImageInfo(fileId, fileInfo.getContent(), height, width, fileInfo.getSize(), fileInfo.getAuthor(), imageExtraInfo);
			return imageInfo;
		}
	}

	private FastdfsClient getFastdfsClient() {
		String fastdfsSourceType = FastdfsSourceSwitcher.getFastdfsSourceType();
		FastdfsClient fastdfsClient = null;

		if (StringUtils.isNotBlank(fastdfsSourceType)) {
			FastdfsClientProxy fastdfsClientProxy = targetFastdfsSources.get(fastdfsSourceType);

			if (fastdfsClientProxy != null) {
				fastdfsClient = fastdfsClientProxy.getFastdfsClient();
			}
		}

		if (fastdfsClient == null) {
			fastdfsClient = defaultTargetFastdfsSource.getFastdfsClient();
		}

		if (fastdfsClient == null) {
			throw new SystemException("Can not get a fastdfsClient!");
		}

		return fastdfsClient;
	}

	public void afterPropertiesSet() {
		Set<Entry<String, FastdfsClientProxy>> set = targetFastdfsSources.entrySet();

		for (Map.Entry<String, FastdfsClientProxy> entry : set) {
			FastdfsClientProxy fastdfsClientProxy = entry.getValue();
			FastdfsClient fastdfsClient = fastdfsClientProxy.getFastdfsClient();
			fastdfsClient.afterPropertiesSet();
		}
	}
}
