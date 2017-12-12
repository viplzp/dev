package io.flysium.framework.util.ftp;

/**
 * 远程ftp文件过滤接口定义
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public interface FTPFileFilter {

	/**
	 * 根据文件名过滤，决定是否进行下一步要ftp操作
	 * 
	 * @param remoteFileNametemp
	 * @return
	 */
	public boolean filter(String remoteFileNametemp);
}
