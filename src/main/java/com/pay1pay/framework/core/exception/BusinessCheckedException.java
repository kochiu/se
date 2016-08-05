/**
 * 
 */
package com.pay1pay.framework.core.exception;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pay1pay.framework.core.ApplicationContextUtils;

/**
 * checked异常类
 * 
 * @author jetdu
 *
 */
public class BusinessCheckedException extends Exception implements ExceptionMsg {

	/**
     * 
     */
	private static final long serialVersionUID = -7606507321039906181L;

	/**
	 * 错误码
	 */
	private String errCode;

	/**
	 * 错误信息
	 */
	private String errMsg;

	/**
	 * 附加信息。如果校验支付密码时，还有几次可登录信息
	 */
	private String attach;

	@JsonIgnore
	private transient ExceptionMsg businessException;

	public BusinessCheckedException(String errCode, String errMsg) {
		super(String.format("[%s]%s", errCode, errMsg));
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public BusinessCheckedException(String errCode, String message, String attach) {
		super(String.format("[%s]%s", errCode, message));
		this.errCode = errCode;
		this.errMsg = message;
		this.attach = attach;
	}

	public BusinessCheckedException(String errCode, String message, Throwable cause) {
		super(String.format("[%s]%s", errCode, message), cause);
		this.errCode = errCode;
		this.errMsg = message;
	}

	public BusinessCheckedException(String errCode, String message, String attach, Throwable cause) {
		super(String.format("[%s]%s", errCode, message), cause);
		this.errCode = errCode;
		this.errMsg = message;
		this.attach = attach;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attach == null) ? 0 : attach.hashCode());
		result = prime * result + ((errCode == null) ? 0 : errCode.hashCode());
		result = prime * result + ((errMsg == null) ? 0 : errMsg.hashCode());
		return result;
	}

	/**
	 * 获取外部错误码描述 <功能详细描述>
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */

	public String getOutErrDesc() {
		return getOutErrMsg(this.errCode);
	}

	/**
	 * 获取外部错误码描述 <功能详细描述>
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */

	@Override
	@JsonIgnore
	public String getOutErrMsg(String errCode) {
		if (businessException == null) {
			ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
			if (applicationContext == null) {
				return null;
			}
			try {
				businessException = applicationContext.getBean(ExceptionMsg.class);
			} catch (Exception e) {
				return null;
			}
			if (businessException == null) {
				return null;
			}
		}

		return StringUtils.hasText(attach) ? (businessException.getOutErrMsg(errCode) + "，" + attach) : businessException.getOutErrMsg(errCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessCheckedException other = (BusinessCheckedException) obj;
		if (attach == null) {
			if (other.attach != null)
				return false;
		} else if (!attach.equals(other.attach))
			return false;
		if (errCode == null) {
			if (other.errCode != null)
				return false;
		} else if (!errCode.equals(other.errCode))
			return false;
		if (errMsg == null) {
			if (other.errMsg != null)
				return false;
		} else if (!errMsg.equals(other.errMsg))
			return false;
		return true;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public ExceptionMsg getBusinessException() {
		return businessException;
	}

	public void setBusinessException(ExceptionMsg businessException) {
		this.businessException = businessException;
	}

}
