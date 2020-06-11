package com.kimleysoft.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

	@GetMapping("toUploadPage")
	public String toUploadPage() {
		return "upload";
	}

	@PostMapping("upload")
	public String upload(MultipartFile[] files, String id) {
		try {
			for (MultipartFile file : files) { // 循环保存文件
				uploadFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "i18n";
	}

	public String uploadFile(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		String path = "d:\\images";// 设置文件保存路径
		File tempFile = new File(path, String.valueOf(fileName));
		if (!tempFile.getParentFile().exists()) {// 创建文件夹
			tempFile.getParentFile().mkdir();
		}
		if (!tempFile.exists()) {
			tempFile.createNewFile();
		}
		file.transferTo(tempFile);
		return "images/" + tempFile.getName();
	}
}
