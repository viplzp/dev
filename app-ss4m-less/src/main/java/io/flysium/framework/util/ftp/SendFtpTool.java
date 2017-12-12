package io.flysium.framework.util.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.flysium.framework.util.ftp.FTPUtils.FtpBatchResult;
import io.flysium.framework.util.ftp.FTPUtils.FtpConfig;
import io.flysium.framework.util.ftp.FTPUtils.FtpResult;

/**
 * FTP工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
final class SendFtpTool {

	private static Logger logger = LoggerFactory.getLogger(SendFtpTool.class);

	private SendFtpTool() {
	}

	/**
	 * 登录ftp服务器
	 * 
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public static FTPClient loginFtpServer(FtpConfig config) throws IOException {
		FTPClient client = new FTPClient();
		// connect
		boolean flag = false;
		try {
			client.setDataTimeout(30000);
			if (-1 != config.getPort()) {
				client.connect(config.getIpAddress(), config.getPort());
			} else {
				client.connect(config.getIpAddress());
			}
		} catch (SocketException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		try {
			flag = client.login(config.getUsername(), config.getPwd());
		} catch (IOException e) {
			flag = false;
			throw e;
		} finally {
			// reply
			if (!flag) {
				logger.error(config.getIpAddress() + "  " + config.getUsername() + "--登陆失败");
			}
		}
		int reply = client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			logger.error(new StringBuilder(config.getIpAddress()).append("  ").append("返回").append(reply)
					.append("--登陆失败").toString());
			client.disconnect();
			return null;
		} else {
			logger.error(config.getIpAddress() + "  " + config.getUsername() + "  --登陆成功");
		}
		return client;
	}

	/**
	 * 发送文件到ftp服务器
	 * 
	 * @param client
	 * @param config
	 * @param file
	 * @param remoteFileNametemp
	 * @return
	 * @throws IOException
	 */
	public static FtpResult sendToFTP(FTPClient client, FtpConfig config, File file, String remoteFileNametemp)
			throws IOException {
		FtpResult ftpResult = new FtpResult();
		ftpResult.setSuc(false);
		ftpResult.setRemoteFileNametemp(remoteFileNametemp);
		ftpResult.setLocalFile(file);
		BufferedInputStream buffIn = null;
		FileInputStream fis = null;
		try {
			/**
			 * 切换到远程目录，如果不存在，则创建
			 */
			cdInAndMakeDirIfAbsent(client, config);

			fis = new FileInputStream(file);
			buffIn = new BufferedInputStream(fis);
			boolean flag = client.storeFile(remoteFileNametemp, buffIn);
			ftpResult.setSuc(flag);
		} catch (IOException e) {
			logger.error("FTP上传失败", e);
			ftpResult.setSuc(false);
			throw e;
		} finally {
			if (null != buffIn) {
				buffIn.close();
			}
			if (null != fis) {
				fis.close();
			}
			if (ftpResult.isSuc()) {
				logger.info(new StringBuilder("ip=   ").append(config.getIpAddress()).append("  ")
						.append(remoteFileNametemp).append(" 发送成功").toString());
			}
		}
		return ftpResult;
	}

	/**
	 * 从ftp服务器上下载一个文件
	 * 
	 * @param client
	 * @param config
	 * @param file
	 * @param remoteFileNametemp
	 * @return
	 * @throws IOException
	 */
	public static FtpResult getFromFTP(FTPClient client, FtpConfig config, File file, String remoteFileNametemp)
			throws IOException {
		FtpResult ftpResult = new FtpResult();
		ftpResult.setSuc(false);
		ftpResult.setRemoteFileNametemp(remoteFileNametemp);
		ftpResult.setLocalFile(file);
		BufferedOutputStream buffOut = null;
		FileOutputStream fos = null;
		try {
			/**
			 * 切换到远程目录
			 */
			cdIn(client, config);

			fos = new FileOutputStream(file);
			buffOut = new BufferedOutputStream(fos);
			client.retrieveFile(remoteFileNametemp, buffOut);
			ftpResult.setSuc(true);
		} catch (IOException e) {
			logger.error("FTP下载失败", e);
			ftpResult.setSuc(false);
		} finally {
			if (null != buffOut) {
				buffOut.close();
			}
			if (null != fos) {
				fos.close();
			}
			if (ftpResult.isSuc()) {
				logger.info(new StringBuilder("ip=   ").append(config.getIpAddress()).append("  ")
						.append(remoteFileNametemp).append(" 下载成功").toString());
			}
		}
		return ftpResult;
	}

	/**
	 * 从ftp服务器上下载多个文件
	 * 
	 * @param client
	 * @param config
	 * @param ftpFileFilter
	 * @param rmIfSuc
	 * @param interruptIfError
	 * @return
	 * @throws IOException
	 */
	public static FtpBatchResult batchGetFromFTP(FTPClient client, FtpConfig config, FTPFileFilter ftpFileFilter,
			boolean rmIfSuc, boolean interruptIfError) throws IOException {
		FtpBatchResult ftpBatchResult = new FtpBatchResult(true, new ArrayList<FtpResult>());
		try {
			/**
			 * 切换到远程目录
			 */
			cdIn(client, config);

			FTPFile[] fileList = client.listFiles(config.getDestDir());
			if (fileList == null) {
				return ftpBatchResult;
			}
			for (int i = 0; i < fileList.length; i++) {
				FTPFile f = fileList[i];
				String remoteFileNametemp = f.getName();
				if (ftpFileFilter != null && !ftpFileFilter.filter(remoteFileNametemp)) {
					continue;
				}

				FtpResult ftpResult = getFile(client, config.getLocalBakDir(), remoteFileNametemp, interruptIfError);
				ftpBatchResult.getResult().add(ftpResult);

				if (ftpResult.isSuc()) {
					logger.info(new StringBuilder("ip=   ").append(config.getIpAddress()).append("  ")
							.append(remoteFileNametemp).append(" 下载成功").toString());
				}
				if (ftpResult.isSuc() && rmIfSuc) {// 是否文件下载后删除远程ftp文件
					client.deleteFile(remoteFileNametemp);
				}
			}
		} catch (IOException e) {
			ftpBatchResult.setSuc(false);
			logger.error("FTP下载失败", e);
		} finally {
			if (ftpBatchResult != null && ftpBatchResult.isSuc()) {
				logger.info("ip=   " + config.getIpAddress() + "  " + config.getDestDir() + " 下载成功");
			}
		}
		return ftpBatchResult;
	}

	/**
	 * 切换到远程目录，如果不存在，则创建
	 */
	private static void cdInAndMakeDirIfAbsent(FTPClient client, FtpConfig config) throws IOException {
		client.setFileType(FTP.BINARY_FILE_TYPE);

		String ftpClientMode = config.getFtpClientMode();
		if (ftpClientMode != null && FtpConfig.FTPCLIENTMODE_LOCALPASSIVEMODE.equalsIgnoreCase(ftpClientMode)) {
			client.enterLocalPassiveMode();
		}

		// 设置上传目录
		if (!"/".equals(config.getDestDir()) && !client.changeWorkingDirectory(config.getDestDir())) {
			if (!client.makeDirectory(config.getDestDir())) {
				throw new IOException("创建文件夹" + config.getDestDir() + "失败");
			}
			client.changeWorkingDirectory(config.getDestDir());// 再设置一遍
		}
	}

	/**
	 * 切换到远程目录
	 */
	private static void cdIn(FTPClient client, FtpConfig config) throws IOException {
		client.setFileType(FTP.BINARY_FILE_TYPE);

		String ftpClientMode = config.getFtpClientMode();
		if (ftpClientMode != null && FtpConfig.FTPCLIENTMODE_LOCALPASSIVEMODE.equalsIgnoreCase(ftpClientMode)) {
			client.enterLocalPassiveMode();
		}

		// 设置上传目录
		if (!"/".equals(config.getDestDir()) && !client.changeWorkingDirectory(config.getDestDir())) {
			client.changeWorkingDirectory(config.getDestDir());// 再设置一遍
		}
	}

	private static FtpResult getFile(FTPClient client, String localBakDir, String remoteFileNametemp,
			boolean interruptIfError) throws IOException {
		File localFile = new File(localBakDir, remoteFileNametemp);
		FtpResult ftpResult = new FtpResult();
		ftpResult.setSuc(false);
		ftpResult.setRemoteFileNametemp(remoteFileNametemp);
		ftpResult.setLocalFile(localFile);
		BufferedOutputStream buffOut = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(localFile);
			buffOut = new BufferedOutputStream(fos);
			client.retrieveFile(remoteFileNametemp, buffOut);
			buffOut.close();
			ftpResult.setSuc(true);
		} catch (IOException e) {
			ftpResult.setSuc(false);
			if (interruptIfError) {
				throw e;
			}
		} finally {
			if (null != buffOut) {
				buffOut.close();
			}
			if (null != fos) {
				fos.close();
			}
		}
		return ftpResult;
	}

}
