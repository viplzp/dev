package io.flysium.framework.exception;

import io.flysium.framework.message.CodeInfo;

/**
 * 业务异常
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @date 1.0
 */
public class BizException extends RuntimeException {

	private static final long serialVersionUID = -7937967220741740242L;

	protected final String code;

	/**
	 * 构造器
	 * 
	 * @param code
	 * @param message
	 */
	public BizException(String code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 构造器
	 * 
	 * @param codeInfo
	 */
	public BizException(CodeInfo codeInfo) {
		super(codeInfo.getMessage());
		this.code = codeInfo.getCode();
	}

	/**
	 * 获取错误编码
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

}