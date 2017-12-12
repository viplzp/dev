package io.flysium.framework.message;

import java.io.Serializable;
import java.util.regex.Matcher;

import org.springframework.util.StringUtils;

/**
 * 编码规范
 */
public class CodeInfo implements Serializable {

	private static final long serialVersionUID = 626097569525594507L;

	private String code;
	private String message;
	private String desc;

	/**
	 * 构造器
	 */
	public CodeInfo() {
		super();
	}

	/**
	 * 构造器
	 * 
	 * @param code
	 * @param message
	 * @param desc
	 */
	public CodeInfo(String code, String message, String desc) {
		this.code = code;
		this.message = message;
		this.desc = desc;
	}

	/** 获取编码 */
	public String getCode() {
		return code;
	}

	/** 设置编码 */
	public void setCode(String code) {
		this.code = code;
	}

	/** 获取编码信息 */
	public String getMessage() {
		return message;
	}

	/** 设置编码信息 */
	public void setMessage(String message) {
		this.message = message;
	}

	/** 获取描述 */
	public String getDesc() {
		return desc;
	}

	/** 设置描述 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/** 替换编码信息 */
	public CodeInfo replaceMessage(String regex, String replacement) {
		if (StringUtils.isEmpty(message)) {
			return this;
		}
		String quoteReplacement = (replacement == null) ? "null" : Matcher.quoteReplacement(replacement);
		String messageTemp = message.replaceAll(toRealRegex(regex), quoteReplacement);
		return new CodeInfo(code, messageTemp, desc);
	}

	/** 替换编码信息 */
	public CodeInfo replaceMessage(String regex, Throwable e) {
		if (StringUtils.isEmpty(message)) {
			return this;
		}
		String replacement = e.getMessage();
		if (!StringUtils.isEmpty(replacement) && e instanceof NullPointerException) {
			replacement = "NullPointerException";
		}
		replacement = replacement == null ? "null" : replacement;
		// 特殊字符进行处理
		String quoteReplacement = Matcher.quoteReplacement(replacement);
		String messageTemp = message.replaceAll(toRealRegex(regex), quoteReplacement);
		return new CodeInfo(code, messageTemp, desc);
	}

	/** 替换描述 */
	public CodeInfo replaceDesc(String regex, String replacement) {
		if (StringUtils.isEmpty(desc)) {
			return this;
		}
		String quoteReplacement = null;
		if (replacement != null) {
			// 特殊字符进行处理
			quoteReplacement = Matcher.quoteReplacement(replacement);
		}
		String descTemp = desc.replaceAll(toRealRegex(regex), quoteReplacement);
		return new CodeInfo(code, message, descTemp);
	}

	private String toRealRegex(String regex) {
		return new StringBuilder("\\$\\{").append(regex).append("\\}").toString();
	}

}