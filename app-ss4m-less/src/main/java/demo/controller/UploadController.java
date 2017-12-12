package demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import io.flysium.framework.Consts;
import io.flysium.framework.message.ResponseResult;

/**
 * 上传Controller样例
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
@RestController // 标识为restful风格，适合回参不跳转页面的ajax模式
@RequestMapping
public class UploadController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * SpringMVC包装类上传文件+ajaxfileupload（推荐）
	 */
	@RequestMapping
	public ResponseResult ajaxUpload(HttpServletRequest request,
			@RequestParam(value = "params_json", required = false) String paramsJson) throws IOException {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);

		/***
		 * 获取参数： String jsons = paramsJson.replaceAll("\\?", "\""); Map<String,
		 * String> params = JSON.toJavaObject(JSON.parseObject(jsons),
		 * Map.class);
		 */

		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (!multipartResolver.isMultipart(request)) {
			responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_90002);
			return responseResult;
		}
		// 转换成多部分request
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		// 取得request中的所有文件名
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			// 取得上传文件
			MultipartFile file = multiRequest.getFile(iter.next());
			if (file == null) {
				continue;
			}
			// 记录上传过程起始时的时间，用来计算上传时间
			long start = System.currentTimeMillis();
			// 取得当前上传文件的文件名称
			String originalFilename = file.getOriginalFilename();
			// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
			if (!StringUtils.isEmpty(originalFilename)) {
				originalFilename = originalFilename.trim();
			}
			if (!StringUtils.isEmpty(originalFilename)) {
				// 定义上传路径
				StringBuilder path = new StringBuilder();
				path.append(request.getServletContext().getRealPath("/"));
				path.append("ajaxdemo_").append(System.currentTimeMillis()).append("_").append(originalFilename);

				try {
					File localFile = new File(path.toString());
					file.transferTo(localFile);
					// 记录上传该文件后的时间
					long end = System.currentTimeMillis();
					logSuc(originalFilename, end - start);
				} catch (Exception e) {
					logger.error("上传文件" + originalFilename + "出错", e);
				}
			}
		}
		return responseResult;
	}

	/**
	 * SpringMVC包装类上传文件（推荐）
	 */
	@RequestMapping
	public ResponseResult upload(HttpServletRequest request) throws IOException {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);

		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (!multipartResolver.isMultipart(request)) {
			responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_90002);
			return responseResult;
		}
		// 转换成多部分request
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		// 取得request中的所有文件名
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			// 取得上传文件
			MultipartFile file = multiRequest.getFile(iter.next());
			if (file == null) {
				continue;
			}
			// 记录上传过程起始时的时间，用来计算上传时间
			long start = System.currentTimeMillis();
			// 取得当前上传文件的文件名称
			String originalFilename = file.getOriginalFilename();
			// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
			// 如果名称不为“”,说明该文件存在，否则说明该文件不存在
			if (!StringUtils.isEmpty(originalFilename)) {
				originalFilename = originalFilename.trim();
			}
			if (!StringUtils.isEmpty(originalFilename)) {
				// 定义上传路径
				StringBuilder path = new StringBuilder();
				path.append(request.getServletContext().getRealPath("/"));
				path.append("demo_").append(System.currentTimeMillis()).append("_").append(originalFilename);

				try {
					File localFile = new File(path.toString());
					file.transferTo(localFile);

					// 记录上传该文件后的时间
					long end = System.currentTimeMillis();
					logSuc(originalFilename, end - start);
				} catch (Exception e) {
					logger.error("上传文件" + originalFilename + "出错", e);
				}
			}
		}
		return responseResult;
	}

	/**
	 * SpringMVC字节流输入上传文件
	 */
	@RequestMapping
	public ResponseResult upload0(@RequestParam("file") CommonsMultipartFile[] files, HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);

		for (int i = 0; i < files.length; i++) {
			if (files[i].isEmpty()) {
				continue;
			}
			long start = System.currentTimeMillis();
			// 取得当前上传文件的文件名称
			String originalFilename = files[i].getOriginalFilename();
			FileOutputStream os = null;
			FileInputStream in = null;
			try {
				// 拿到输出流，同时重命名上传的文件
				StringBuilder path = new StringBuilder();
				path.append(request.getServletContext().getRealPath("/"));
				path.append("demo0_").append(System.currentTimeMillis()).append("_").append(originalFilename);
				os = new FileOutputStream(path.toString());
				// 拿到上传文件的输入流
				in = (FileInputStream) files[i].getInputStream();

				// 以写字节的方式写文件
				int b = 0;
				while ((b = in.read()) != -1) {
					os.write(b);
				}
				os.flush();

				// 记录上传该文件后的时间
				long end = System.currentTimeMillis();
				logSuc(originalFilename, end - start);
			} catch (Exception e) {
				logger.error("上传文件" + originalFilename + "出错", e);
			} finally {
				try {
					if (os != null)
						os.close();
				} catch (IOException e2) {
					logger.error(e2.getMessage(), e2);
				}
				try {
					if (in != null)
						in.close();
				} catch (IOException e2) {
					logger.error(e2.getMessage(), e2);
				}
			}

		}
		return responseResult;
	}

	private void logSuc(String originalFilename, long millSeconds) {
		logger.info("上传文件：" + originalFilename + "成功，耗时：" + (millSeconds) + "ms");
	}
}
