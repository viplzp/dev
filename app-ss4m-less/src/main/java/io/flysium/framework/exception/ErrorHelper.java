package io.flysium.framework.exception;

import io.flysium.framework.message.CodeInfo;

/**
 * 抛出包含错误编码的异常
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @date 1.0
 */
public class ErrorHelper {

	private ErrorHelper() {
	}

	/**
	 * 抛出异常
	 * 
	 * @param code
	 * @param message
	 */
	public static void throwError(String code, String message) {
		throw new BizException(code, message);
	}

	/**
	 * 抛出异常
	 * 
	 * @param codeInfo
	 */
	public static void throwError(CodeInfo codeInfo) {
		throw new BizException(codeInfo);
	}
}