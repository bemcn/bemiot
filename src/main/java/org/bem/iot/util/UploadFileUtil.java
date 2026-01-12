package org.bem.iot.util;

import cn.hutool.core.util.StrUtil;
import org.bem.iot.entity.BaseFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UploadFileUtil {
	private static final String SUFFIX_ARRAY = ".jpg,.jpeg,.png,.bmp,.gif,.mp3,.mp4,.flv,.wav,.wma,.wmv,.mid,.avi,.mpg,.asf,.rm,.rmvb,.pdf,.exe,.psd,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.htm,.html,.txt,.zip,.rar,.7z,.gz,.bz2";

	/**
	 * 生成上传对象（Base64）
	 * @param fileBase base64文件上传数据
	 * @return 后缀
	 */
	public static BaseFile createUploadConfig(String fileBase) throws Exception {
		if(StrUtil.isEmpty(fileBase)) {
			throw new Exception("文件数据不能为空");
		}

		String[] baseArray = fileBase.split("base64,");
		if (baseArray.length != 2) {
			throw new Exception("上传文件数据格式错误");
		}

		String dataPrefix = baseArray[0];
		String fileData = baseArray[1];
		String fileName = UploadFileUtil.getImgBase64Name(dataPrefix);
		if(StrUtil.isEmpty(fileName)) {
			throw new Exception("无法识别文件类型");
		}

		Map<String, String> dirMap = UploadFileUtil.getStoragePath();
		String url = dirMap.get("path") + "/" + fileName;
		String saveDir = dirMap.get("savePath");
		String saveUrl = saveDir + "/" + fileName;


		fileData = fileData.replaceAll(" ", "+");
		return new BaseFile(fileData, url, saveDir, fileName, saveUrl);
	}

	/**
	 * 生成上传对象（文件）
	 * @param file 上传文件对象
	 * @return 后缀
	 */
	public static BaseFile createUploadConfig(MultipartFile file) throws Exception {
		if(file == null) {
			throw new Exception("文件数据不能为空");
		}

		String originalFilename = file.getOriginalFilename();
		if(!Objects.requireNonNull(originalFilename).contains(".")) {
			throw new Exception("上传文件名称错误");
		}

		String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		if (!SUFFIX_ARRAY.contains(suffix)) {
			throw new Exception("上传文件类型不支持，请查阅API文档");
		}

		long maxSize = 100 * 1024 * 1024;
		long size = file.getSize();
		if (size > maxSize) {
			throw new Exception("文件大小超过100MB");
		}

		//生成新文件名及后缀
		String fileName = UploadFileUtil.creatFileName() + suffix;

		Map<String, String> dirMap = UploadFileUtil.getStoragePath();
		String url = dirMap.get("path") + "/" + fileName;
		String saveDir = dirMap.get("savePath");
		String saveUrl = saveDir + "/" + fileName;

		return new BaseFile("", url, saveDir, fileName, saveUrl);
	}

	/**
	 * 生成上传驱动（文件）
	 * @param file 上传文件对象
	 * @return 后缀
	 */
	public static BaseFile createUploadDrive(MultipartFile file) throws Exception {
		if(file == null) {
			throw new Exception("文件数据不能为空");
		}

		String fileName = file.getOriginalFilename();
		if(!Objects.requireNonNull(fileName).contains(".")) {
			throw new Exception("上传文件名称错误");
		}

		String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
		if (!".jar".equals(suffix)) {
			throw new Exception("上传文件类型不支持");
		}

		long maxSize = 100 * 1024 * 1024;
		long size = file.getSize();
		if (size > maxSize) {
			throw new Exception("文件大小超过100MB");
		}

		Map<String, String> dirMap = UploadFileUtil.getStorageDrivePath();
		String url = dirMap.get("path") + "/" + fileName;
		String saveDir = dirMap.get("savePath");
		String saveUrl = saveDir + "/" + fileName;

		return new BaseFile("", url, saveDir, fileName, saveUrl);
	}

	/**
	 * 批量生成上传对象（文件）
	 * @param files 上传文件对象
	 * @return 返回批量上传对象
	 * @throws Exception 批量上传异常
	 */
	public static List<BaseFile> createUploadConfigList(MultipartFile[] files) throws Exception {
		if(files.length == 0) {
			throw new Exception("文件不能为空，至少1个文件");
		}
		List<BaseFile> list = new ArrayList<>();
		boolean error = false;
		String errorMessage = "";
		for (MultipartFile file : files) {
			try {
				BaseFile baseFile = createUploadConfig(file);
				list.add(baseFile);
			} catch (Exception e) {
				error = true;
				errorMessage = e.getMessage();
				break;
			}
		}
		if(error) {
			throw new Exception(errorMessage);
		} else {
			return list;
		}
	}

	/**
	 * 生成Excel错误文件对象
	 * @param name 数据表中文名称
	 * @param suffix 文件后缀名
	 * @return 文件对象
	 */
	public static BaseFile initByErrorFile(String name, String suffix) {
		//生成新文件名及后缀
		Date date = new Date();
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd-HHmmss");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		String fileName = dateFormat1.format(date) + "-" + name + "-导入错误" + suffix;
		String fileKey = dateFormat2.format(date);

		//检查存储目录是否存在
		String saveFileDir = "";
		try {
			saveFileDir = new File("/storage").getCanonicalPath();
		} catch (IOException ignored) {
		}
		File dir = new File(saveFileDir);
		if(!dir.exists()){
			try {
				boolean isMkdirs = dir.mkdirs();
				if(isMkdirs) {
					System.out.println("创建文件夹" + saveFileDir);
				}
			} catch (Exception ignored) {
			}
		}
		saveFileDir = "/storage/excel";
		try {
			saveFileDir = new File(saveFileDir).getCanonicalPath();
		} catch (IOException ignored) {
		}
		dir = new File(saveFileDir);
		if(!dir.exists()){
			try {
				boolean isMkdirs = dir.mkdirs();
				if(isMkdirs) {
					System.out.println("创建文件夹" + saveFileDir);
				}
			} catch (Exception ignored) {
			}
		}

		// 遍历目录
		File directory = new File(saveFileDir);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				String subFileName = file.getName();
				if (!subFileName.startsWith(fileKey)) {
					boolean isDelete = file.delete();
					if (isDelete) {
						System.out.println("删除文件" + subFileName);
					}
				}
			}
		}

		String url = "/storage/excel/" + fileName;
		String saveUrl = saveFileDir + "/" + fileName;
		return new BaseFile("", url, saveFileDir, fileName, saveUrl);
	}

	/**
	 * 检查存储目录
	 * @param dirPath 目录路径
	 */
	public static void verifyDir(String dirPath) {
		File directory = new File(dirPath);
		if (!directory.exists() || !directory.isDirectory()) {
			boolean created = directory.mkdirs();
			if (!created) {
				System.err.println("无法创建目录: " + dirPath);
			}
		}
	}

	/**
     * 获取Base文件名
     * @param dataPrix base64前缀
     * @return 后缀
     */
    private static String getImgBase64Name(String dataPrix) {
    	String imgName;
        if ("data:image/jpg;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".jpg";
        } else if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".jpg";
        } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".ico";
        } else if ("data:image/gif;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".gif";
        } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".png";
        } else if ("data:image/bmp;".equalsIgnoreCase(dataPrix)) {
        	imgName = creatFileName() + ".bmp";
        } else {
        	imgName = "";
        }
        return imgName;
    }


	/**
	 * 生成随机文件名
	 * @return 返回文件名
	 */
	private static String creatFileName() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timp = dateFormat.format(date);
		int rmd = (int)((Math.random()*9+1)*1000);
		return timp + "_" + rmd;
	}

	/**
	 * 获取存储路径
	 * @return 存储路径
	 */
	public static Map<String, String> getStoragePath() {
    	Map<String, String> map = new HashMap<>(16);
    	String saveFileDir = "";
    	String fileDir;
		try {
			saveFileDir = new File("/storage").getCanonicalPath();
		} catch (IOException ignored) {
		}
		File dir = new File(saveFileDir);
    	if(!dir.exists()){
			try {
				boolean isMkdirs = dir.mkdirs();
				if(isMkdirs) {
					System.out.println("创建文件夹" + saveFileDir);
				}
			} catch (Exception ignored) {
			}
        }
    	
    	Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String subDir = dateFormat.format(date);
		fileDir = "/storage/" + subDir;
		saveFileDir += "/" + subDir;
		try {
			saveFileDir = new File(saveFileDir).getCanonicalPath();
		} catch (IOException ignored) {
		}
		dir = new File(saveFileDir);
    	if(!dir.exists()){
			try {
				boolean isMkdirs = dir.mkdirs();
				if(isMkdirs) {
					System.out.println("创建文件夹" + saveFileDir);
				}
			} catch (Exception ignored) {
			}
        }
    	
    	map.put("path", fileDir);
    	map.put("savePath", saveFileDir);
		return map;
    }

	/**
	 * 获取存储路径
	 * @return 存储路径
	 */
	private static Map<String, String> getStorageDrivePath() {
		Map<String, String> map = new HashMap<>(16);
		String saveFileDir = "";
		try {
			saveFileDir = new File("/drive").getCanonicalPath();
		} catch (IOException ignored) {
		}
		File dir = new File(saveFileDir);
		if(!dir.exists()){
			try {
				boolean isMkdirs = dir.mkdirs();
				if(isMkdirs) {
					System.out.println("创建文件夹" + saveFileDir);
				}
			} catch (Exception ignored) {
			}
		}

		map.put("path", "/drive");
		map.put("savePath", saveFileDir);
		return map;
	}
}
