package com.kochiu.se.common.util.poi;

/**
 * @author: chenggui.huang
 * @date: 2014-02-13 15:06
 */
public class CellDefine {
	/**
	 * 列的标示(注意要和POJO的属性名一致)
	 */
    private String cellName;
    
    /**
     * 列的描述(即excel的表头信息)
     */
    private String cellDescr;

    public CellDefine() {
    }

    public CellDefine(String cellName, String cellDescr) {

        this.cellName = cellName;
        this.cellDescr = cellDescr;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getCellDescr() {
        return cellDescr;
    }

    public void setCellDescr(String cellDescr) {
        this.cellDescr = cellDescr;
    }
}
