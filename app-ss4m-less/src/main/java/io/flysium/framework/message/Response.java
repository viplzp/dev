package io.flysium.framework.message;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import io.flysium.framework.Consts;
import io.flysium.framework.exception.BizException;

/**
 * 回参
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
class Response implements Serializable {

	private static final long serialVersionUID = -1392106691999940819L;

	private static Logger logger = LoggerFactory.getLogger(Response.class);

	protected String res_code;
	protected String res_message;

	/**
	 * 构造器
	 */
	public Response() {
		super();
	}

	/**
	 * 构造器
	 * 
	 * @param resultCode
	 * @param resultMessage
	 */
	public Response(String resultCode, String resultMessage) {
		this.res_code = resultCode;
		this.res_code = resultMessage;
	}

	/**
	 * 构造器
	 * 
	 * @param codeInfo
	 */
	public Response(CodeInfo codeInfo) {
		if (codeInfo != null) {
			res_code = codeInfo.getCode();
			res_message = codeInfo.getMessage();
		}
	}

	/**
	 * 设置编码
	 * 
	 * @param codeInfo
	 */
	public void setCodeInfo(CodeInfo codeInfo) {
		this.res_code = codeInfo.getCode();
		this.res_message = codeInfo.getMessage();
	}

	/**
	 * 设置异常信息
	 * 
	 * @param e
	 */
	public void setException(Throwable e) {
		if (e instanceof BizException) {
			BizException codeException = (BizException) e;
			this.res_code = codeException.getCode();
			this.res_message = codeException.getMessage();
		} else {
			String message = parseThrowable(e);

			CodeInfo codeInfo = null;
			try {
				codeInfo = Consts.CodeInfoSet.CODE_90001.replaceMessage("message", message);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				codeInfo = Consts.CodeInfoSet.CODE_90002;
			}

			setCodeInfo(codeInfo);
		}
	}

	private String parseThrowable(Throwable e) {
		if (StringUtils.isEmpty(e.getMessage())) {
			return "null";
		}
		if (e instanceof NullPointerException) {
			return "NullPointerException";
		} else if (e instanceof InvocationTargetException) {
			InvocationTargetException targetException = (InvocationTargetException) e;
			if (targetException.getCause() != null) {
				return targetException.getCause().getMessage();
			} else if (targetException.getTargetException() != null) {
				return targetException.getTargetException().getMessage();
			}
		}
		return null;
	}

	/** 获取回参编码 */
	public String getRes_code() {
		return res_code;
	}

	/** 设置回参编码 */
	public void setRes_code(String res_code) {
		this.res_code = res_code;
	}

	/** 获取回参编码信息 */
	public String getRes_message() {
		return res_message;
	}

	/** 设置回参编码信息 */
	public void setRes_message(String res_message) {
		this.res_message = res_message;
	}

}