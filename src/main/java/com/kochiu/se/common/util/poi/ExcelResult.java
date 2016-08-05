package com.kochiu.se.common.util.poi;

/**
 * 
 * @author zhihongp
 *
 */
public class ExcelResult {

	/**
	 * 是否全部成功标志
	 */
	private boolean isSuccess;

	/**
	 * 数据总条数
	 */
	private int totalNum;

	/**
	 * 成功条数
	 */
	private int successNum;

	/**
	 * 失败条数
	 */
	private int failureNum;

	/**
	 * 处理时间
	 */
	private long cost;

	public ExcelResult() {

	}

	public ExcelResult(boolean isSuccess, int totalNum, int successNum, int failureNum) {
		this.isSuccess = isSuccess;
		this.totalNum = totalNum;
		this.successNum = successNum;
		this.failureNum = failureNum;
	}

	public ExcelResult(boolean isSuccess, int totalNum, int successNum, int failureNum, long cost) {
		this.isSuccess = isSuccess;
		this.totalNum = totalNum;
		this.successNum = successNum;
		this.failureNum = failureNum;
		this.cost = cost;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}

	public int getFailureNum() {
		return failureNum;
	}

	public void setFailureNum(int failureNum) {
		this.failureNum = failureNum;
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "ExcelResult [isSuccess=" + isSuccess + ", totalNum=" + totalNum + ", successNum=" + successNum + ", failureNum=" + failureNum + ", cost="
				+ cost + "]";
	}

}
