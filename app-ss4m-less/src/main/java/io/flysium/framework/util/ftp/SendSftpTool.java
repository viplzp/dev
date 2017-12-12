package io.flysium.framework.util.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.flysium.framework.util.ftp.FTPUtils.FtpBatchResult;
import io.flysium.framework.util.ftp.FTPUtils.FtpConfig;
import io.flysium.framework.util.ftp.FTPUtils.FtpResult;

/**
 * SFTP工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
final class SendSftpTool {

	private static Logger logger = LoggerFactory.getLogger(SendSftpTool.class);

	private SendSftpTool() {
	}

	/**
	 * 登录sftp服务器
	 * 
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public static Session getSftpSession(FtpConfig config) throws IOException {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(config.getUsername(), config.getIpAddress(), config.getPort());
			session.setPassword(config.getPwd());
			session.setTimeout(30000);
			Properties sftpconf = new Properties();
			sftpconf.put("StrictHostKeyChecking", "no");
			session.setConfig(sftpconf);
			session.connect();
		} catch (JSchException e) {
			logger.error(e.getMessage(), e);
		}
		return session;
	}

	/**
	 * 获取sftp会话
	 * 
	 * @param session
	 * @return
	 * @throws IOException
	 */
	public static ChannelSftp getSftpChannel(Session session) throws IOException {
		ChannelSftp channel = null;
		try {
			channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
			channel.connect();
		} catch (JSchException e) {
			logger.error(e.getMessage(), e);
		}
		return channel;
	}

	/**
	 * 发送文件到sftp服务器
	 * 
	 * @param channel
	 * @param config
	 * @param file
	 * @param remoteFileNametemp
	 * @return
	 * @throws IOException
	 */
	public static FtpResult sendToSFTP(ChannelSftp channel, FtpConfig config, File file, String remoteFileNametemp)
			throws IOException {
		FtpResult ftpResult = new FtpResult();
		ftpResult.setSuc(false);
		ftpResult.setRemoteFileNametemp(remoteFileNametemp);
		ftpResult.setLocalFile(file);
		BufferedInputStream buffIn = null;
		FileInputStream fis = null;
		/**
		 * 切换到远程目录，如果不存在，则创建
		 */
		cdInAndMakeDirIfAbsent(channel, config);
		try {
			fis = new FileInputStream(file);
			buffIn = new BufferedInputStream(fis);
			channel.put(buffIn, remoteFileNametemp);
			ftpResult.setSuc(true);
		} catch (Exception e) {
			logger.error("SFTP上传失败", e);
			ftpResult.setSuc(false);
		} finally {
			if (null != buffIn) {
				buffIn.close();
			}
			if (null != fis) {
				fis.close();
			}
			if (ftpResult.isSuc()) {
				logger.info("ip=   " + config.getIpAddress() + "  " + remoteFileNametemp + " 发送成功");
			}
		}
		return ftpResult;
	}

	/**
	 * 从sftp服务器上下载一个文件
	 * 
	 * @param channel
	 * @param config
	 * @param file
	 * @param remoteFileNametemp
	 * @return
	 * @throws IOException
	 */
	public static FtpResult getFromSFTP(ChannelSftp channel, FtpConfig config, File file, String remoteFileNametemp)
			throws IOException {
		FtpResult ftpResult = new FtpResult();
		ftpResult.setSuc(false);
		ftpResult.setRemoteFileNametemp(remoteFileNametemp);
		ftpResult.setLocalFile(file);
		/**
		 * 切换到远程目录
		 */
		cdIn(channel, config);

		BufferedOutputStream buffOut = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			buffOut = new BufferedOutputStream(fos);
			channel.get(remoteFileNametemp, buffOut);
			ftpResult.setSuc(true);
		} catch (Exception e) {
			logger.error("SFTP下载失败", e);
			ftpResult.setSuc(false);
		} finally {
			if (null != buffOut) {
				buffOut.close();
			}
			if (null != fos) {
				fos.close();
			}
			if (ftpResult.isSuc()) {
				logger.info("ip=   " + config.getIpAddress() + "  " + remoteFileNametemp + " 下载成功");
			}
		}
		return ftpResult;
	}

	/**
	 * 从sftp服务器上下载多个文件
	 * 
	 * @param channel
	 * @param config
	 * @param ftpFileFilter
	 * @param rmIfSuc
	 * @param interruptIfError
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static FtpBatchResult batchGetFromSFTP(ChannelSftp channel, FtpConfig config, FTPFileFilter ftpFileFilter,
			boolean rmIfSuc, boolean interruptIfError) throws IOException {
		FtpBatchResult ftpBatchResult = new FtpBatchResult(true, new ArrayList<FtpResult>());
		/**
		 * 切换到远程目录
		 */
		cdIn(channel, config);

		try {
			List fileList = channel.ls(config.getDestDir());
			if (fileList == null) {
				return ftpBatchResult;
			}
			Iterator it = fileList.iterator();
			while (it.hasNext()) {
				LsEntry f = (LsEntry) it.next();
				String remoteFileNametemp = f.getFilename();
				if (ftpFileFilter != null && !ftpFileFilter.filter(remoteFileNametemp)) {
					continue;
				}
				FtpResult ftpResult = getFile(channel, config.getLocalBakDir(), remoteFileNametemp, interruptIfError);
				ftpBatchResult.getResult().add(ftpResult);

				if (ftpResult.isSuc()) {
					logger.info("ip=   " + config.getIpAddress() + "  " + config.getDestDir() + "/" + remoteFileNametemp
							+ " 下载成功");
				}
				if (ftpResult.isSuc() && rmIfSuc) {// 是否文件下载后删除远程ftp文件
					channel.rm(remoteFileNametemp);
				}
			}
		} catch (Exception e) {
			ftpBatchResult.setSuc(false);
			logger.error("SFTP下载失败", e);
		} finally {
			if (ftpBatchResult.isSuc()) {
				logger.info("ip=   " + config.getIpAddress() + "  " + config.getDestDir() + " 下载成功");
			}
		}
		return ftpBatchResult;
	}

	/**
	 * 切换到远程目录，如果不存在，则创建
	 */
	private static void cdInAndMakeDirIfAbsent(ChannelSftp channel, FtpConfig config) throws IOException {
		try {
			if (!"/".equals(config.getDestDir())) {
				channel.cd(config.getDestDir());
			}
		} catch (SftpException e1) {
			logger.error(e1.getMessage(), e1);
			if (e1.id == 2) {
				try {
					channel.mkdir(config.getDestDir());
				} catch (Exception e) {
					logger.error("创建文件夹" + config.getDestDir() + "失败：" + e.getMessage(), e);
					return;
				}
				try {
					channel.cd(config.getDestDir());
				} catch (SftpException e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
	}

	/**
	 * 切换到远程目录
	 */
	private static void cdIn(ChannelSftp channel, FtpConfig config) throws IOException {
		try {
			if (!"/".equals(config.getDestDir())) {
				channel.cd(config.getDestDir());
			}
		} catch (SftpException e1) {
			logger.error(e1.getMessage(), e1);
			if (e1.id == 2) {
				try {
					channel.cd(config.getDestDir());
				} catch (SftpException e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		}
	}

	private static FtpResult getFile(ChannelSftp channel, String localBakDir, String remoteFileNametemp,
			boolean interruptIfError) throws IOException, SftpException {
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
			channel.get(remoteFileNametemp, buffOut);// 文件取到本地
			buffOut.close();
			ftpResult.setSuc(true);
		} catch (Exception e) {
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
