package com.fr.bi.fe.fs.data;

import java.util.Calendar;
import java.util.Date;

import com.fr.data.dao.DAOBean;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

public class FineExcelUserModel extends DAOBean{

	private String username;
	private String email;

	private String validateCode;
	//默认为未激活状态
	private int status = 0;
	private Date registerDate;
	
	private int importedExcel;
	private int deletedExcel;
	private int newReport;
	private int removeReport;
	
	public FineExcelUserModel() {}
	
	public FineExcelUserModel (long id) {
		this.id = id;
	}
	
	public FineExcelUserModel (String email, String validateCode, int status, Date registerDate) {
		this.email  =email;
		this.status = status;
		this.validateCode = validateCode;
		this.registerDate = registerDate;
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	
	public int getImportedExcel() {
		return importedExcel;
	}

	public void setImportedExcel(int importedExcel) {
		this.importedExcel = importedExcel;
	}

	public int getDeletedExcel() {
		return deletedExcel;
	}

	public void setDeletedExcel(int deletedExcel) {
		this.deletedExcel = deletedExcel;
	}

	public int getNewReport() {
		return newReport;
	}

	public void setNewReport(int newReport) {
		this.newReport = newReport;
	}

	public int getRemoveReport() {
		return removeReport;
	}

	public void setRemoveReport(int removeReport) {
		this.removeReport = removeReport;
	}

	/**
     * 通过JSON对象初始化用户对象
     * @param jo 包含用户信息的json对象
     * @throws com.fr.json.JSONException
     */
	public void parseJSON(JSONObject jo) throws JSONException {
		if (jo.has("id")) {
			this.setId(jo.getLong("id"));
		}
		if (jo.has("validateCode")) {
			this.setValidateCode(jo.getString("validateCode"));
		}
		if (jo.has("status")) {
			this.setStatus(Integer.parseInt(jo.getString("status")));
		}
//		if (jo.has("registerDate")) {
//			this.setRegisterDate(new Date(Date.parse((jo.getString("registerDate")))));
//		}
	}

	@Override
	protected int hashCode4Properties() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((validateCode == null) ? 0 : validateCode.hashCode());
		result = prime * result
				+ ((registerDate == null) ? 0 : registerDate.hashCode());
		result = prime * result + (int) (status ^ (status >>> 32));

		return result;
	}

	@Override
	public boolean equals4Properties(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		FineExcelUserModel ano = (FineExcelUserModel) obj;
		return this.id == ano.id
			&& ComparatorUtils.equals(this.validateCode, ano.validateCode)
			&& ComparatorUtils.equals(this.status, ano.status)
			&& ComparatorUtils.equals(this.registerDate, ano.registerDate);
	}
	
	public Date getLastActivateTime() {
        Calendar cl = Calendar.getInstance();
        cl.setTime(registerDate);
        cl.add(Calendar.DATE , 2);
         
        return cl.getTime();
    }

}