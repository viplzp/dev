package io.flysium.framework.message;

/**
 * 回参结果
 */
public class ResponseSuccess extends Response {

	private static final long serialVersionUID = 5257847304399873465L;

	protected boolean success;

	/**
	 * 构造器
	 * 
	 * @param success
	 */
	public ResponseSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean getSuccess() {
		return success;
	}

	/**
	 * 设置是否成功
	 * 
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
