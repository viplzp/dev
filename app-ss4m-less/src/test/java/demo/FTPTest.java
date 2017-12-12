package demo;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import io.flysium.framework.util.ftp.FTPFileFilter;
import io.flysium.framework.util.ftp.FTPUtils;
import io.flysium.framework.util.ftp.FTPUtils.FtpBatchResult;
import io.flysium.framework.util.ftp.FTPUtils.FtpConfig;
import io.flysium.framework.util.ftp.FTPUtils.FtpResult;

/**
 * FTP测试
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class FTPTest {

	private FtpConfig ftpConfig;

	@Before
	public void before() {
		File localDir = new File("/tmp/ftp");
		if (!localDir.exists())
			localDir.mkdirs();
		ftpConfig = new FtpConfig("10.45.47.90", 22, "root", "root123", "/root", localDir.getAbsolutePath());
		ftpConfig.setFtpType(FtpConfig.FTPTYPE_SFTP);
	}

	/**
	 * 从FTP/SFTP下载某目录多个文件
	 * 
	 * @throws Exception
	 */
	@Test
	public void batchGetFromFTP() throws Exception {
		FtpBatchResult batchResult = FTPUtils.batchGetFromFTP(ftpConfig, new FTPFileFilter() {

			public boolean filter(String remoteFileNametemp) {
				if (remoteFileNametemp != null) {
					if (remoteFileNametemp.endsWith(".txt") || remoteFileNametemp.endsWith(".TXT")) {
						return true;
					}
				}
				return false;
			}
		}, false, true);
		if (batchResult == null || !batchResult.isSuc()) {
			throw new Exception("从FTP/SFTP下载到本地服务器临时目录失败，请联系管理员。");
		}
		for (FtpResult result : batchResult.getResult()) {
			System.out.println(
					"下载成功，远程文件：" + result.getRemoteFileNametemp() + "，本地文件：" + result.getLocalFile().getAbsolutePath());
		}
	}

}
