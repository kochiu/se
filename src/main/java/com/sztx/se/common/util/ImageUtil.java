package com.sztx.se.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import com.sztx.se.common.exception.SystemException;

/**
 * 
 * @author zhihongp
 *
 */
public class ImageUtil {

	/**
	 * 获取图片高度
	 *
	 * @param file 图片文件
	 * @return 高度
	 * @throws IOException
	 */
	public static int getHeight(String filePath) throws IOException {
		InputStream is = null;
		BufferedImage src = null;
		int ret = 0;

		try {
			is = new FileInputStream(new File(filePath));
			src = ImageIO.read(is);

			if (src == null) {
				throw new SystemException("The file :" + filePath + " is not a image");
			}

			ret = src.getHeight();
		} finally {
			is.close();
		}

		return ret;
	}

	/**
	 * 获取图片宽度
	 *
	 * @param file 图片文件
	 * @return 宽度
	 * @throws IOException
	 * @throws Exception
	 */
	public static int getWidth(String filePath) throws IOException {
		InputStream is = null;
		BufferedImage src = null;
		int ret = 0;

		try {
			is = new FileInputStream(new File(filePath));
			src = ImageIO.read(is);

			if (src == null) {
				throw new SystemException("The file :" + filePath + " is not a image");
			}

			ret = src.getWidth();
		} finally {
			is.close();
		}

		return ret;
	}

	/**
	 * 获得图片大小
	 * 
	 * @param imagePath 图片绝对路径
	 * @return 图片大小(单位:字节)
	 */
	public static long getSize(String imagePath) {
		return FileUtil.getFileSize(imagePath);
	}

	/**
	 * 获得图片路径(不包含图片名)
	 * 
	 * @param imagePath 图片绝对路径
	 * @return 图片名字
	 */
	public static String getPath(String imagePath) {
		return FileUtil.getFilePath(imagePath);
	}

	/**
	 * 获得图片名字(包含后缀名)
	 * 
	 * @param imagePath 图片绝对路径
	 * @return 图片名字
	 */
	public static String getName(String imagePath) {
		return FileUtil.getFileName(imagePath);
	}

	/**
	 * 获得图片后缀
	 * 
	 * @param imagePath 图片绝对路径
	 * @return 图片后缀
	 */
	public static String getSuffix(String imagePath) {
		return FileUtil.getFileSuffix(imagePath);
	}

	/**
	 * 获取图片格式
	 * 
	 * @param file 图片路径
	 * @return 图片格式
	 * @throws IOException
	 */
	public static String getImageFormatName(String filePath) throws IOException {
		String formatName = null;
		ImageInputStream iis = null;

		try {
			iis = ImageIO.createImageInputStream(new File(filePath));
			Iterator<ImageReader> imageReader = ImageIO.getImageReaders(iis);

			if (imageReader.hasNext()) {
				ImageReader reader = imageReader.next();
				formatName = reader.getFormatName();
			}
		} finally {
			if (iis != null) {
				iis.close();
			}
		}

		return formatName;
	}

	/**
	 * 裁剪图片
	 *
	 * @param srcImagePath 源图片路径
	 * @param newImagePath 处理后图片路径
	 * @param x 起始X坐标
	 * @param y 起始Y坐标
	 * @param width 裁剪宽度
	 * @param height 裁剪高度
	 * @return 返回true说明裁剪成功,否则失败
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static boolean cutImage(String srcImagePath, String newImagePath, int x, int y, int width, int height) throws IOException, InterruptedException,
			IM4JavaException {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.crop(width, height, x, y);
		op.addImage(newImagePath);
		ConvertCmd convert = new ConvertCmd(true);
		convert.run(op);
		return true;
	}

	/**
	 * 原图压缩质量
	 * 
	 * @param srcImagePath 源图片路径
	 * @param newImagePath 处理后图片路径
	 * @param quality 图片质量
	 * @return 返回true说明缩放成功,否则失败
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 */
	public static boolean zoomImage(String srcImagePath, String newImagePath, double quality) throws IOException, InterruptedException, IM4JavaException {
		Integer width = getWidth(srcImagePath);
		Integer height = getHeight(srcImagePath);
		return zoomImage(srcImagePath, newImagePath, width, height, quality);
	}

	/**
	 * 根据尺寸缩放压缩图片[等比例缩放:参数height为null,按宽度缩放比例缩放;参数width为null,按高度缩放比例缩放]
	 *
	 * @param srcImagePath 源图片路径
	 * @param newImagePath 处理后图片路径
	 * @param width 缩放后的图片宽度
	 * @param height 缩放后的图片高度
	 * @param quality 图片质量
	 * @return 返回true说明缩放成功,否则失败
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static boolean zoomImage(String srcImagePath, String newImagePath, Integer width, Integer height, double quality) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);

		if (width == null) {
			op.resize(null, height);
		} else if (height == null) {
			op.resize(width);
		} else {
			op.resize(width, height);
		}

		op.quality(quality);
		op.addImage(newImagePath);
		ConvertCmd convert = new ConvertCmd(true);
		convert.run(op);
		return true;
	}

	/**
	 * 图片旋转
	 *
	 * @param imagePath 源图片路径
	 * @param newPath 处理后图片路径
	 * @param degree 旋转角度
	 * @return 返回true说明缩放成功,否则失败
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static boolean rotateImage(String srcImagePath, String newImagePath, double degree) throws IOException, InterruptedException, IM4JavaException {
		degree = degree % 360;

		if (degree <= 0) {
			degree = 360 + degree;
		}

		IMOperation op = new IMOperation();
		op.addImage(srcImagePath);
		op.rotate(degree);
		op.addImage(newImagePath);
		ConvertCmd convert = new ConvertCmd(true);
		convert.run(op);
		return true;
	}

	/**
	 * 给图片加文字
	 * 
	 * @param srcImagePath 源图片路径
	 * @param newImagePath 处理后的图片路径
	 * @param text 文字内容
	 * @return 返回true说明缩放成功,否则失败
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static boolean addImgText(String srcImagePath, String newImagePath, String text) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.font("Arial");
		op.gravity("southeast");
		op.pointsize(18).fill("#BCBFC8").draw("text 0,0 " + text);
		op.addImage();
		op.addImage();
		ConvertCmd convert = new ConvertCmd(true);
		convert.run(op, srcImagePath, srcImagePath);
		return true;
	}

	/**
	 * 给图片加水印
	 * 
	 * @param srcImagePath 源图片路径
	 * @param waterImagePath 水印路径
	 * @param newImagePath 处理后的图片路径
	 * @param gravity 图片位置
	 * @param dissolve 水印透明度
	 * @return 返回true说明缩放成功,否则失败
	 * @throws IM4JavaException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static boolean waterMark(String srcImagePath, String waterImagePath, String newImagePath, String gravity, int dissolve) throws IOException,
			InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.addImage(waterImagePath);
		op.gravity(gravity);
		op.dissolve(dissolve);
		op.addImage(srcImagePath);
		op.addImage(newImagePath);
		CompositeCmd convert = new CompositeCmd(true);
		convert.run(op);
		return true;
	}

	public static void main(String[] args) throws Exception {
		//ImageUtil.imageMagickPath = "C:/Program Files/GraphicsMagick-1.3.23-Q16";
		String imagePath = "E:/code/PNG图片/IMG_3124.PNG";
		// String waterImagePath = "E:/code/tulips.jpg";
		// String newImagePath1 = "E:/apps/haha1.jpg";
		// String newImagePath2 = "E:/apps/haha2.jpg";
		// String newImagePath3 = "E:/apps/haha3.jpg";
		// // String newImagePath4 = "E:/apps/haha4.jpg";
		// String newImagePath5 = "E:/apps/haha5.jpg";
		String newImagePath6 = "E:/apps/PNG图片/IMG_3124.PNG";
		// String newImagePath7 = "E:/apps/123.gif";
		int width = getWidth(imagePath);
		int height = getHeight(imagePath);
		long size = getSize(imagePath);
		String path = getPath(imagePath);
		String newPath = getPath(newImagePath6);
		String name = getName(imagePath);
		String suffix = getSuffix(imagePath);
		String formatName = getImageFormatName(imagePath);
		System.out.println("name=" + name);
		System.out.println("path=" + path);
		System.out.println("newPath=" + newPath);
		System.out.println("suffix=" + suffix);
		System.out.println("formatName=" + formatName);
		System.out.println("width=" + width);
		System.out.println("height=" + height);
		System.out.println("size=" + size);
		// // boolean cutImageFlag = cutImage(imagePath, newImagePath1, 32, 105,
		// // 200, 200);
		boolean zoomImageFlag = zoomImage(imagePath, newImagePath6, width, height, 35);
		// // boolean rotateImageFlag = rotateImage(imagePath, newImagePath3,
		// 90);
		// // // boolean addImgTextFlag = addImgText(imagePath, newImagePath4,
		// // // "神州通行");
		// // boolean waterMarkFlag = waterMark(imagePath, waterImagePath,
		// // newImagePath5, "southeast", 30);
		// // boolean createThumbnailFlag = createThumbnail(imagePath,
		// // newImagePath6, 300, 600, 20);
		// // System.out.println("size=" + size);
		// // System.out.println("cutImageFlag=" + cutImageFlag);
		System.out.println("zoomImageFlag=" + zoomImageFlag);
		// // System.out.println("rotateImageFlag=" + rotateImageFlag);
		// // // System.out.println("addImgTextFlag=" + addImgTextFlag);
		// // System.out.println("waterMarkFlag=" + waterMarkFlag);
		// // System.out.println("createThumbnailFlag=" + createThumbnailFlag);
	}
}
