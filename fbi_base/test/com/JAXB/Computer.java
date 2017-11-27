package com.JAXB;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
  
/** 
 * 电脑类 
 *  
 * @author kary
 *  
 */  
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "Computer")  
//@XmlType(propOrder = { "serialNumber", "brandName", "productDate", "price" })
public class Computer implements Serializable {  
    private static final long serialVersionUID = 1L;  
  
    // 序列号  
    private String serialNumber;  
    // 品牌名  
    private String brandName;  
    // 生成日期  
    private Date productDate;  
    // 价格  
    private double price;  
  
    public Computer() {  
        super();  
    }  
  
    public Computer(String serialNumber, String brandName, Date productDate,  
            double price) {  
        super();  
        this.serialNumber = serialNumber;  
        this.brandName = brandName;  
        this.productDate = productDate;  
        this.price = price;  
    }  
  
    public String getSerialNumber() {  
        return serialNumber;  
    }  
  
    public void setSerialNumber(String serialNumber) {  
        this.serialNumber = serialNumber;  
    }  
  
    public String getBrandName() {  
        return brandName;  
    }  
  
    public void setBrandName(String brandName) {  
        this.brandName = brandName;  
    }  
  
    public Date getProductDate() {  
        return productDate;  
    }  
  
    public void setProductDate(Date productDate) {  
        this.productDate = productDate;  
    }  
  
    public double getPrice() {  
        return price;  
    }  
  
    public void setPrice(double price) {  
        this.price = price;  
    }  
  
}  