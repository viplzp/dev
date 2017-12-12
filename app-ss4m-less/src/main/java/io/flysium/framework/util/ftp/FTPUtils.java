package io.flysium.framework.util.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * FTP/SFTP通用工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public final class FTPUtils {

	private static Logger logger = LoggerFactory.getLogger(FTPUtils.class);

	private FTPUtils() {
	}

	/**
	 * 发送单个文件到FTP/SFTP
	 * 
	 * @param ftpConfig
	 *            FTP配置信息
	 * @param file
	 *            应用服务器硬盘的文件对象
	 * @param remoteFileNametemp
	 *            远程ftp文件名
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static FtpResult sendToFTP(FtpConfig ftpConfig, File file, String remoteFileNametemp) throws IOException {
		if (ftpConfig == null) {
			return null;
		}
		FtpResult ftpResult = null;
		if (ftpConfig.getFtpType() != null && FtpConfig.FTPTYPE_SFTP.equalsIgnoreCase(ftpConfig.getFtpType())) {
			Session session = SendSftpTool.getSftpSession(ftpConfig);
			if (session == null || !session.isConnected()) {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp会话失败。");
				return ftpResult;
			}
			ChannelSftp channel = SendSftpTool.getSftpChannel(session);
			if (channel != null && channel.isConnected()) {
				ftpResult = SendSftpTool.sendToSFTP(channel, ftpConfig, file, remoteFileNametemp);
				channel.disconnect();
				session.disconnect();
			} else {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp渠道失败。");
				session.disconnect();
			}

		} else {
			FTPClient client = SendFtpTool.loginFtpServer(ftpConfig);
			if (client != null && client.isConnected()) {
				ftpResult = SendFtpTool.sendToFTP(client, ftpConfig, file, remoteFileNametemp);
				client.disconnect();
			} else {
				logger.error("登陆" + ftpConfig.getIpAddress() + "失败。");
			}
		}
		return ftpResult;
	}

	/**
	 * 从FTP/SFTP下载单个文件
	 * 
	 * @param ftpConfig
	 *            FTP配置信息
	 * @param file
	 *            应用服务器硬盘的文件对象
	 * @param remoteFileNametemp
	 *            远程ftp文件名
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static FtpResult getFromFTP(FtpConfig ftpConfig, File file, String remoteFileNametemp) throws IOException {
		if (ftpConfig == null) {
			return null;
		}
		FtpResult ftpResult = null;
		if (ftpConfig.getFtpType() != null && FtpConfig.FTPTYPE_SFTP.equalsIgnoreCase(ftpConfig.getFtpType())) {
			Session session = SendSftpTool.getSftpSession(ftpConfig);
			if (session == null || !session.isConnected()) {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp会话失败。");
				return ftpResult;
			}
			ChannelSftp channel = SendSftpTool.getSftpChannel(session);
			if (channel != null && channel.isConnected()) {
				ftpResult = SendSftpTool.getFromSFTP(channel, ftpConfig, file, remoteFileNametemp);
				channel.disconnect();
				session.disconnect();
			} else {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp渠道失败。");
				session.disconnect();
			}
		} else {
			FTPClient client = SendFtpTool.loginFtpServer(ftpConfig);
			if (client != null && client.isConnected()) {
				ftpResult = SendFtpTool.getFromFTP(client, ftpConfig, file, remoteFileNametemp);
				client.disconnect();
			} else {
				logger.error("登陆" + ftpConfig.getIpAddress() + "失败。");
			}
		}
		return ftpResult;
	}

	/**
	 * 从FTP/SFTP下载某目录多个文件
	 * 
	 * @param ftpConfig
	 *            FTP配置信息
	 * @param ftpFileFilter
	 *            远程ftp文件过滤接口
	 * @param rmIfSuc
	 *            是否文件下载后删除远程ftp文件
	 * @param interruptIfError
	 *            下载过程中，其中一个失败是否中断下载
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 * @return
	 */
	public static FtpBatchResult batchGetFromFTP(FtpConfig ftpConfig, FTPFileFilter ftpFileFilter, boolean rmIfSuc,
			boolean interruptIfError) throws IOException {
		if (ftpConfig == null) {
			return null;
		}
		FtpBatchResult batchResult = null;
		if (ftpConfig.getFtpType() != null && FtpConfig.FTPTYPE_SFTP.equalsIgnoreCase(ftpConfig.getFtpType())) {
			Session session = SendSftpTool.getSftpSession(ftpConfig);
			if (session == null || !session.isConnected()) {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp会话失败。");
				return batchResult;
			}
			ChannelSftp channel = SendSftpTool.getSftpChannel(session);
			if (channel != null && channel.isConnected()) {
				batchResult = SendSftpTool.batchGetFromSFTP(channel, ftpConfig, ftpFileFilter, rmIfSuc,
						interruptIfError);
				channel.disconnect();
				session.disconnect();
			} else {
				logger.error("获取" + ftpConfig.getIpAddress() + " Sftp渠道失败。");
				session.disconnect();
			}
		} else {
			FTPClient client = SendFtpTool.loginFtpServer(ftpConfig);
			if (client != null && client.isConnected()) {
				batchResult = SendFtpTool.batchGetFromFTP(client, ftpConfig, ftpFileFilter, rmIfSuc, interruptIfError);
				client.disconnect();
			} else {
				logger.error("登陆" + ftpConfig.getIpAddress() + "失败。");
			}
		}
		return batchResult;
	}

	/**
	 * FTP配置信息
	 */
	public static class FtpConfig implements java.io.Serializable {

		private static final long serialVersionUID = -1022594555520308580L;

		/** FTP协议 File Transfer Protocol的缩写，文件传送协议 */
		public static final String FTPTYPE_FTP = "FTP";
		/** SFTP协议 Secure File Transfer Protocol的缩写，安全文件传送协议 */
		public static final String FTPTYPE_SFTP = "SFTP";

		/** FTP协议 客户端主动模式PORT模式 客户端主动连服务器端；端口用20 */
		public static final String FTPCLIENTMODE_LOCALACTIVEMODE = "LAM";
		/** FTP协议 客户端被动模式PASV模式 服务器端连客户端；随机打开一个高端端口（端口号大于1024） */
		// 有防火墙用户不能使用主动模式，这是因为防火墙不允许来自网外的主动连接，所以用户必须同使用被动模式
		public static final String FTPCLIENTMODE_LOCALPASSIVEMODE = "LPM";

		private String ipAddress;
		private int port = 21;
		private String username;
		private String pwd;
		private String destDir;
		private String localBakDir;
		private String ftpClientMode = FTPCLIENTMODE_LOCALPASSIVEMODE;
		private String ftpType = FTPTYPE_FTP;

		/**
		 * 构造器
		 */
		public FtpConfig() {
			super();
		}

		/**
		 * 构造器
		 * 
		 * @param ipAddress
		 * @param port
		 * @param username
		 * @param pwd
		 * @param destDir
		 */
		public FtpConfig(String ipAddress, int port, String username, String pwd, String destDir) {
			super();
			this.ipAddress = ipAddress;
			this.port = port;
			this.username = username;
			this.pwd = pwd;
			this.destDir = destDir;
		}

		/**
		 * 构造器
		 * 
		 * @param ipAddress
		 * @param port
		 * @param username
		 * @param pwd
		 * @param destDir
		 * @param localBakDir
		 */
		public FtpConfig(String ipAddress, int port, String username, String pwd, String destDir, String localBakDir) {
			super();
			this.ipAddress = ipAddress;
			this.port = port;
			this.username = username;
			this.pwd = pwd;
			this.destDir = destDir;
			this.localBakDir = localBakDir;
		}

		/** 获取ftp服务器IP地址 */
		public String getIpAddress() {
			return ipAddress;
		}
		/** 设置ftp服务器IP地址 */
		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}
		/** 获取ftp服务器端口 */
		public int getPort() {
			return port;
		}
		/** 设置ftp服务器端口 */
		public void setPort(int port) {
			this.port = port;
		}
		/** 获取ftp登录用户名 */
		public String getUsername() {
			return username;
		}
		/** 设置ftp登录用户名 */
		public void setUsername(String username) {
			this.username = username;
		}
		/** 获取ftp登录密码 */
		public String getPwd() {
			return pwd;
		}
		/** 设置ftp登录密码 */
		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		/** 获取ftp远程目录 */
		public String getDestDir() {
			return destDir;
		}
		/** 设置ftp远程目录 */
		public void setDestDir(String destDir) {
			this.destDir = destDir;
		}
		/** 获取ftp文件本地下载目录 */
		public String getLocalBakDir() {
			return localBakDir;
		}
		/** 设置ftp文件本地下载目录 */
		public void setLocalBakDir(String localBakDir) {
			this.localBakDir = localBakDir;
		}
		/** 获取FTP客户端模式 */
		public String getFtpClientMode() {
			return ftpClientMode;
		}
		/** 设置FTP客户端模式 */
		public void setFtpClientMode(String ftpClientMode) {
			this.ftpClientMode = ftpClientMode;
		}
		/** 获取FTP协议 */
		public String getFtpType() {
			return ftpType;
		}
		/** 设置FTP协议 */
		public void setFtpType(String ftpType) {
			this.ftpType = ftpType;
		}

	}

	/**
	 * FTP操作结果
	 */
	public static class FtpBatchResult {

		private boolean isSuc = false;
		private List<FtpResult> result = null;

		/**
		 * 构造器
		 */
		public FtpBatchResult() {
			super();
		}
		/**
		 * 构造器
		 * 
		 * @param isSuc
		 * @param result
		 */
		public FtpBatchResult(boolean isSuc, List<FtpResult> result) {
			super();
			this.isSuc = isSuc;
			this.result = result;
		}
		/** 是否成功 */
		public boolean isSuc() {
			return isSuc;
		}
		/** 设置是否成功 */
		public void setSuc(boolean isSuc) {
			this.isSuc = isSuc;
		}
		/** 获取结果列表 */
		public List<FtpResult> getResult() {
			return result;
		}
		/** 设置结果列表 */
		public void setResult(List<FtpResult> result) {
			this.result = result;
		}
	}

	/**
	 * FTP操作结果
	 */
	public static class FtpResult {

		private boolean isSuc = false;
		private String remoteFileNametemp;
		private File localFile;

		/**
		 * 构造器
		 */
		public FtpResult() {
			super();
		}
		/**
		 * 构造器
		 * 
		 * @param isSuc
		 * @param remoteFileNametemp
		 * @param localFile
		 */
		public FtpResult(boolean isSuc, String remoteFileNametemp, File localFile) {
			super();
			this.isSuc = isSuc;
			this.remoteFileNametemp = remoteFileNametemp;
			this.localFile = localFile;
		}
		/** 是否成功 */
		public boolean isSuc() {
			return isSuc;
		}
		/** 设置是否成功 */
		public void setSuc(boolean isSuc) {
			this.isSuc = isSuc;
		}
		/** 远程文件名 */
		public String getRemoteFileNametemp() {
			return remoteFileNametemp;
		}
		/** 设置远程文件名 */
		public void setRemoteFileNametemp(String remoteFileNametemp) {
			this.remoteFileNametemp = remoteFileNametemp;
		}
		/** 本地文件 */
		public File getLocalFile() {
			return localFile;
		}
		/** 设置本地文件 */
		public void setLocalFile(File localFile) {
			this.localFile = localFile;
		}
	}

}
