package com.fr.bi.fe.fs.data;

import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.data.pool.DBCPConnectionPoolAttr;
import com.fr.data.pool.MemoryConnection;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.internal.util.WebUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class FineExcelUserService {
	
	private static FineExcelUserService fe;

	private static final String FINEEXCELCONNECTIONURL = "jdbc:mysql://finebidemo.mysql.rds.aliyuncs.com:3306/bifinedata?useUnicode=false&amp;characterEncoding=gbk";
	private static final String FINEEXCELCONNECTIONUSER = "finedata";
	private static final String FINEEXCELCONNECTIONPWD = "finedata";
	private static final String FINEEXCELCONNECTIONDRIVER = "com.mysql.jdbc.Driver";
	/**
	 * 唯一实例化对象
	 * @return
	 */
	public static synchronized FineExcelUserService getInstance () {
		if (fe == null) {
			fe = new FineExcelUserService();
		}
		return fe;
	}

	public boolean saveFineExcelUser2DB(FineExcelUserModel user) throws Exception{
		Connection conn = this.getRemoteConn();
		Statement statement = null;
		StringBuffer sql = new StringBuffer();
		FineExcelUserModel feUser = this.getFEUserById(user.getId());
		System.out.println();
		if(feUser == null){
			sql.append("insert into fe_user_info_test (id,username, email, validateCode, "
					+ "status, registerDate) values ( " + user.getId() +",'"+ user.getUsername() + "','" + user.getEmail() + "','"   
					+ user.getValidateCode() + "', "+ user.getStatus() +", '" + new Timestamp(user.getRegisterDate().getTime())
					+"')");
			if(conn != null){
				try {
					statement = conn.createStatement();
					statement.executeUpdate(sql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}finally{
					try {
						statement.close();
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
						return false;
					}
				}
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	public FineExcelUserModel getFEUserByValidateCode(String validateCode){
		Connection conn = getRemoteConn();
		Statement statement = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from fe_user_info_test where validateCode='" + validateCode + "'");
		FineExcelUserModel user = new FineExcelUserModel();
		if(conn != null){
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(rs.next()){
					user.setId(rs.getInt(1));
					user.setUsername(rs.getString(2));
					user.setEmail(rs.getString(3));
					user.setStatus(rs.getInt(4));
					user.setValidateCode(rs.getString(5));
					user.setRegisterDate(rs.getTimestamp(6));
				}else{
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}finally{
				try {
					rs.close();
					statement.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}else{
			return null;
		}
		return user;
	}
	
	public void updateFEUserStatus(final Long id) throws SQLException{
		new Thread(){
			@Override
			public void run(){
				Connection conn = getRemoteConn();
				Statement statement = null;
				String sql = "update fe_user_info_test set status = 1 where id=" + id + "";
				if(conn != null){
					try {
						statement = conn.createStatement();
						statement.executeUpdate(sql);
					} catch (SQLException e) {
						e.printStackTrace();
					}finally{
						try {
							statement.close();
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}
	
	//这里使用fr的数据连接池来创建链接，避免连接数过多的问题
	public Connection getRemoteConn(){
		JDBCDatabaseConnection connection = null;
		Connection conn = null;
		try{
		  	String url= FINEEXCELCONNECTIONURL;
		  	String user= FINEEXCELCONNECTIONUSER;
		  	String pwd= FINEEXCELCONNECTIONPWD;
		  	String driver = FINEEXCELCONNECTIONDRIVER;

//			String url="jdbc:mysql://localhost:3306/bifinedata?useUnicode=false&amp;characterEncoding=gbk";
//			String user="root";
//			String pwd="123456";

//			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = MemoryConnection.create(driver, url, user, pwd, new DBCPConnectionPoolAttr());
		}catch (Exception ex){
		    	return null;
		}
		return conn;
	}
     
    public static boolean send(String toEmail , String content, String subject) throws Exception {
        try {
        	Properties props = new Properties();
            props.setProperty("mail.host", "smtp.qq.com");
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.auth", "true");
            Session session = Session.getInstance(props);
            session.setDebug(true);//发送过程打到控制台
            //1、得到发送邮件的对象
            Transport ts =  session.getTransport();
            //2、连接服务器
            ts.connect("smtp.qq.com", "robin@finereport.com", "shanlanjie123");//邮箱名，密码
            //3、创建邮件
            MimeMessage message = makeMessage(session, content, toEmail, subject);
            //4、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            //5、关闭邮件发送对象
            ts.close();
        }catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
        return true;
    }
    
    private static MimeMessage makeMessage(Session session, String content, String to, String subject) throws Exception{
        MimeMessage message = new MimeMessage(session);//创建了一封邮件
        message.setFrom(new InternetAddress("robin@finereport.com"));//设置发件人
        message.setRecipients(Message.RecipientType.TO,to);//设置收件人的邮箱
        if(subject.equals("register")){
        	message.setSubject("FineExcel注册验证");
        }else{
        	message.setSubject("FineExcel密码重置");;
        }
        //创建正文：文本
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(content, "text/html;charset=UTF-8");
         
        //描述两者之间的关系
        MimeMultipart mmp = new MimeMultipart();
        mmp.addBodyPart(text);

        message.setContent(mmp);//加到邮件对象中取
        message.saveChanges();//邮件就创建出来了
        return message;
    }
    
    /**
     * 处理激活
     * @throws Exception 
     * @throws Exception
     */
      ///传递激活码和email过来
    public String processActivate(String email , String validateCode) throws Exception {  
         //数据访问层，通过email获取用户信息
        FineExcelUserModel user = FineExcelUserService.getInstance().getFEUserByValidateCode(validateCode);
        //验证用户是否存在 
        if(user!=null) {  
            //验证用户激活状态  
            if(user.getStatus()==0) { 
                ///没激活
                Date currentTime = new Date();//获取当前时间  
                //验证链接是否过期 
                currentTime.before(user.getRegisterDate());
                if(currentTime.before(user.getLastActivateTime())) {  
                    //验证激活码是否正确  
                    if(validateCode.equals(user.getValidateCode())) {  
                        //激活成功， //并更新用户的激活状态，为已激活 
                        System.out.println("==sq==="+user.getStatus());
                        user.setStatus(1);//把状态改为激活
                        System.out.println("==sh==="+user.getStatus());
                        FineExcelUserService.getInstance().updateFEUserStatus(user.getId());
                        return "激活成功";
                    } else {  
                    	return "激活码不正确";
                    }  
                } else { 
                	return "激活码已过期！";  
                }  
            } else {
               return "邮箱已激活，请登录！";  
            }  
        } else {
            return "该邮箱未注册（邮箱地址不存在）！";  
        }  
           
    } 
    
    /**
     * 将源字符串使用MD5加密为字节数组
     * @param source
     * @return
     */
    public static byte[] encode2bytes(String source) {
        byte[] result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(source.getBytes("UTF-8"));
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
         
        return result;
    }
    
    /**
     * 将源字符串使用MD5加密为32位16进制数
     * @param source
     * @return
     */
    public static String encode2hex(String source) {
        byte[] data = encode2bytes(source);
 
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xff & data[i]);
             
            if (hex.length() == 1) {
                hexString.append('0');
            }
             
            hexString.append(hex);
        }
         
        return hexString.toString();
    }
     
    /**
     * 验证字符串是否匹配
     * @param unknown 待验证的字符串
     * @param okHex 使用MD5加密过的16进制字符串
     * @return  匹配返回true，不匹配返回false
     */
    public static boolean validate(String unknown , String okHex) {
        return okHex.equals(encode2hex(unknown));
    }
    
    public boolean getUserStatusById(long id){
    	Connection conn = getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	int status = 0;
    	String sql = "select status from fe_user_info_test where id=" + id ;
    	if(conn != null){
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(rs.next()){
					status = rs.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}finally{
				try {
					rs.close();
					statement.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(status == 1){
				return true;
			}else{
				return false;
			}
    	}else{
    		return false;
    	}
    }
    
    public FineExcelUserModel getFEUserByEmail(String email){
    	Connection conn = getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	FineExcelUserModel feUser = new FineExcelUserModel();
    	
    	String sql = "select * from fe_user_info_test where email='" + email + "'";
    	if(conn != null){
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(sql);
				if(rs.next()){
					feUser.setId(rs.getInt(1));
					feUser.setUsername(rs.getString(2));
					feUser.setEmail(rs.getString(3));
					feUser.setStatus(rs.getInt(4));
					feUser.setValidateCode(rs.getString(5));
					feUser.setRegisterDate(rs.getTimestamp(6));
				}else{
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}finally{
				try {
					rs.close();
					statement.close();
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return feUser;
    	}else{
    		return null;
    	}
    }
    
    public FineExcelUserModel getFEUserById(long userId) throws Exception{
    	Connection conn = getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	FineExcelUserModel feUser = new FineExcelUserModel();
    	String sql = "select * from fe_user_info_test where id=" + userId ;
    	if(conn != null){
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(sql);
			} catch (SQLException e) {
				return null;
			}
			try {
				if(rs.next()){
					feUser.setId(userId);
					feUser.setUsername(rs.getString(2));
					feUser.setEmail(rs.getString(3));
					feUser.setStatus(rs.getInt(4));
					feUser.setValidateCode(rs.getString(5));
					feUser.setRegisterDate(rs.getTimestamp(6));
				}else{
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}finally{
				try {
					rs.close();
					statement.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
			}
			if(feUser.getId() == userId){
				return feUser;
			}else{
				return null;
			}
    	}else{
    		return null;
    	}
    }
    
    public boolean saveUserFeedback(FineExcelUserFeedback feedback){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	
    	String uploadFilePath = "D:\\FineExcel\\uploadFiles\\"+ feedback.getUserId() + File.separator + "feedback_file"+ File.separator + feedback.getFullFileName();
    	String screenCapture = "D:\\FineExcel\\uploadFiles\\"+ feedback.getUserId() + File.separator + "screen_capture" + File.separator + feedback.getPicture();
    	String filePath = uploadFilePath.replace("\\", "\\\\");
    	String screenCapturePath = screenCapture.replace("\\", "\\\\");
    	String sql_vote = "insert into fe_vote (userId, vote_error, vote_use, vote_ugly, vote_slow, vote_other) values ("
    				 + feedback.getUserId() + "," + feedback.getVote()[0] + "," + feedback.getVote()[1] +"," + feedback.getVote()[2] 
    				 +","+ feedback.getVote()[3] +","+ feedback.getVote()[4] +")";
    	if(connection != null){
    		try {
    			statement = connection.createStatement();
    			statement.executeUpdate(sql_vote);
    		} catch (Exception e) {
    			try {
    				statement.close();
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					return false;
				}
    			return false;
    		}
    		
    		String sql_voteId = "select id from fe_vote order by id desc limit 1";
    		int voteId = 0;
    		ResultSet rs = null;
    		try {
				rs = statement.executeQuery(sql_voteId);
				while(rs.next()){
					voteId = rs.getInt(1);
				}
			} catch (Exception e) {
				try {
					rs.close();
    				statement.close();
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return false;
			}
    		
    		String sql_feedback = "insert into fe_feedback (email, userId, vote, description, uploadfile, picture, status, ispublic) values ('"
    								+ feedback.getEmail() + "'," + feedback.getUserId() +"," + voteId + ",'"+ feedback.getDescription() 
    								+"','"+ filePath +"','"+ screenCapturePath +"'," + "0,"+ feedback.getIsPublic() +")";
    		try {
				statement.executeUpdate(sql_feedback);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally{
					try {
						rs.close();
						statement.close();
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public String getValidateCodeByUserId(long userId){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	String sql = "select validateCode from fe_user_info_test where id=" + userId;
    	String validateCode = "";
    	try {
    		statement = connection.createStatement();
    		rs = statement.executeQuery(sql);
    		while(rs.next()){
    			validateCode = rs.getString(1);
    		}
    		return validateCode;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}finally{
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
    }
    
    public void saveUserLoginRecord(final long userId, final String email){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	String sql = "insert into fe_login_record (userId, email, login_time) values (" + userId + ",'" + email + "','" + new Timestamp(new Date().getTime()) + "')";
    	    	try {
    	    		statement = connection.createStatement();
    	    		statement.execute(sql);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				try {
    					statement.close();
    					connection.close();
    				} catch (SQLException e1) {
    					e1.printStackTrace();
    				}
    			}
    		}
    	}.start();
    }
    
    public void updateImportedExcel(final String username){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	Statement statement2 = null;
    	    	ResultSet rs = null;
    	    	int count = 0;
    	    	String sql1 = "select imported_excel from fe_user_info_test where username='" + username + "'";
    	    	if(connection != null){
    	    		try {
    	    			statement = connection.createStatement();
    	    			rs = statement.executeQuery(sql1);
    	    			while(rs.next()){
    	    				count = rs.getInt(1) + 1;
    	    				statement2 = connection.createStatement();
    	    				String sql2 = "update fe_user_info_test set imported_excel =" + count+ " where username ='" + username + "'";
    	    				statement2.execute(sql2);
    	    			}
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    		}finally{
    	    			try {
    	    				rs.close();
                            if(statement != null){
                                statement.close();
                            }
                            if(statement2 != null){
                                statement2.close();
                            }
    	    				connection.close();
    	    			} catch (SQLException e) {
    	    				e.printStackTrace();
    	    			}
    	    		}
    	    	}
    		}
    	}.start();
    }
    
    public void updateDeletedExcel(final String username){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	Statement statement2 = null;
    	    	ResultSet rs = null;
    	    	int count = 0;
    	    	String sql1 = "select deleted_excel from fe_user_info_test where username='" + username + "'";
    	    	try {
    	    		statement = connection.createStatement();
    	    		rs = statement.executeQuery(sql1);
    	    		while(rs.next()){
    	    			count = rs.getInt(1) + 1;
    	    			statement2 = connection.createStatement();
    	    			String sql2 = "update fe_user_info_test set deleted_excel =" + count+ " where username='" + username + "'";
    	    			statement2.execute(sql2);
    	    		}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				try {
    					rs.close();
    					statement.close();
    					statement2.close();
    					connection.close();
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}.start();
    }
    
    public void updateNewReport(final String email){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	Statement statement2 = null;
    	    	ResultSet rs = null;
    	    	int count = 0;
    	    	String sql1 = "select new_report from fe_user_info_test where email ='" + email + "'";
    	    	try {
    	    		statement = connection.createStatement();
    	    		rs = statement.executeQuery(sql1);
    	    		while(rs.next()){
    	    			count = rs.getInt(1) + 1;
    	    			String sql2 = "update fe_user_info_test set new_report =" + count+ " where email ='" + email + "'";
    	    			statement2 = connection.createStatement();
    	    			statement2.execute(sql2);
    	    		}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				try {
    					rs.close();
    					statement.close();
    					statement2.close();
    					connection.close();
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}.start();
    }
    
    public void updateRemoveReport(final String email){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	Statement statement2 = null;
    	    	ResultSet rs = null;
    	    	int count = 0;
    	    	String sql1 = "select remove_report from fe_user_info_test where email ='" + email + "'";
    	    	try {
    	    		statement = connection.createStatement();
    	    		rs = statement.executeQuery(sql1);
    	    		while(rs.next()){
    	    			count = rs.getInt(1) + 1;
    	    			statement2 = connection.createStatement();
    	    			String sql2 = "update fe_user_info_test set remove_report =" + count + " where email ='" + email + "'";
    	    			statement2.execute(sql2);
    	    		}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				try {
    					rs.close();
    					statement.close();
    					statement2.close();
    					connection.close();
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}.start();
    }
    
    public JSONArray getFeedbackByPage(int page){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	JSONArray ja = new JSONArray();
    	long userId = 1l;
    	String sql = "select * from fe_feedback where description != '' and ispublic=1 order by agree desc ";
    	try {
    		statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			while(rs.next()){
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getInt(1));
				jo.put("email", rs.getString(2));
				userId = rs.getInt(3);
				jo.put("userId", userId);
				jo.put("description", rs.getString(5));
				if(rs.getString(6) != null && !rs.getString(6).equals("")){
					JSONArray filePaths = this.getPicturePath(rs.getString(6));
					jo.put("filePath", filePaths);
				}else{
					jo.put("filePath", "");
				}
//				jo.put("filePath", rs.getString(6));
				jo.put("picturePath", rs.getString(7));
				jo.put("agree", rs.getInt(8));
				jo.put("status", rs.getInt(9));
				jo.put("isPublic", rs.getInt(10));
				jo.put("adminReply", rs.getString(11));
				jo.put("adminReplyImage", rs.getString(12));
				jo.put("adminReplyAgree", rs.getInt(13));
				jo.put("adminReplyDisagree", rs.getInt(14));
				User user = UserControl.getInstance().getUser(userId);
				if(user!=null){
					jo.put("username", UserControl.getInstance().getUser(userId).getUsername());
				}else{
					jo.put("username", "");
				}
				ja.put(jo);
			}
			return ja;
		} catch (Exception e) {
			e.printStackTrace();
			return ja;
		}finally{
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
    public void saveFeedbackAgree(final int id, final int count){
            Connection connection = getRemoteConn();
            Statement statement = null;
            String sql = "update fe_feedback set agree="+ count + " where id=" + id;
            try {
                statement = connection.createStatement();
                statement.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
    }
    
    public void saveFeedbackAgreeByUser(final int feedbackId, final String username, final int status){
            Connection connection = getRemoteConn();
            Statement statement = null;
            ResultSet rs = null;
            String sql = "select * from fe_user_agreement_to_feedback where username='" + username + "' and feedbackId=" + feedbackId;
            String insert_sql = "insert into fe_user_agreement_to_feedback (feedbackId, username, status) values (" + feedbackId + ",'" + username + "'," + status + ")";
            String update_sql = "update fe_user_agreement_to_feedback set status="+ status + " where username='" + username + "' and feedbackId=" + feedbackId;
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery(sql);
                if(rs.next()){
                    statement.execute(update_sql);
                }else{
                    statement.execute(insert_sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    rs.close();
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
    }
    
    public JSONArray getFeedbackByKeyword(String keyword){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	JSONArray ja = new JSONArray();
    	long userId = 1l;
    	String sql = "select * from fe_feedback where description != '' and description like '%"+ keyword +"%' order by agree desc";
    	try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			while(rs.next()){
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getInt(1));
				jo.put("email", rs.getString(2));
				userId = rs.getInt(3);
				jo.put("userId", userId);
				jo.put("description", rs.getString(5));
				if(rs.getString(6) != null && !rs.getString(6).equals("")){
					JSONArray filePaths = this.getPicturePath(rs.getString(6));
					jo.put("filePath", filePaths);
				}else{
					jo.put("filePath", "");
				}
//				JSONArray filePaths = this.getPicturePath(rs.getString(6));
//				jo.put("filePath", filePaths);
				jo.put("picturePath", rs.getString(7));
				jo.put("agree", rs.getInt(8));
				jo.put("status", rs.getInt(9));
				jo.put("isPublic", rs.getInt(10));
				jo.put("adminReply", rs.getString(11));
				jo.put("adminReplyImage", rs.getString(12));
				jo.put("adminReplyAgree", rs.getInt(13));
				jo.put("adminReplyDisagree", rs.getInt(14));
				User user = UserControl.getInstance().getUser(userId);
				if(user!=null){
					jo.put("username", UserControl.getInstance().getUser(userId).getUsername());
				}else{
					jo.put("username", "");
				}
				ja.put(jo);
			}
			return ja;
		} catch (Exception e) {
			e.printStackTrace();
			return ja;
		}finally{
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
    public JSONArray getFeedbackByUser(long userId){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	JSONArray ja = new JSONArray();
    	String sql = "select * from fe_feedback where description != '' and userId ="+ userId +" order by agree desc";
    	try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			while(rs.next()){
				JSONObject jo = new JSONObject();
				jo.put("id", rs.getInt(1));
				jo.put("email", rs.getString(2));
				userId = rs.getInt(3);
				jo.put("userId", userId);
				jo.put("description", rs.getString(5));
				if(rs.getString(6) != null && !rs.getString(6).equals("")){
					JSONArray filePaths = this.getPicturePath(rs.getString(6));
					jo.put("filePath", filePaths);
				}else{
					jo.put("filePath", "");
				}
//				JSONArray filePaths = this.getPicturePath(rs.getString(6));
//				jo.put("filePath", filePaths);
				jo.put("picturePath", rs.getString(7));
				jo.put("agree", rs.getInt(8));
				jo.put("status", rs.getInt(9));
				jo.put("isPublic", rs.getInt(10));
				jo.put("adminReply", rs.getString(11));
				jo.put("adminReplyImage", rs.getString(12));
				jo.put("adminReplyAgree", rs.getInt(13));
				jo.put("adminReplyDisagree", rs.getInt(14));
				User user = UserControl.getInstance().getUser(userId);
				if(user!=null){
					jo.put("username", UserControl.getInstance().getUser(userId).getUsername());
				}else{
					jo.put("username", "");
				}
				ja.put(jo);
			}
			return ja;
		} catch (Exception e) {
			e.printStackTrace();
			return ja;
		}finally{
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
    public JSONArray getPicturePath(String filePath){
    	
		String filePathSplit [] = filePath.split("\\\\");
		String fileNames = filePathSplit[filePathSplit.length - 1];
		String mainPath = filePath.substring(0, filePath.length() - fileNames.length());
		JSONArray ja = new JSONArray();
		JSONArray ja_;
		try {
			if(fileNames != null && !fileNames.equals("null") && !fileNames.equals("")){
				ja_ = new JSONArray(fileNames);
				String fileName = "";
				for(int i = 0; i < ja_.length(); i++){
					fileName = ja_.optString(i);
					if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith("jpeg")){
						ja.put(mainPath + fileName);
					}
				}
			}else{
				return ja;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return ja;
		}
    	
    	return ja;
    }
    
    public void saveAdminReplyAgree(final int feedbackId,final String username,final int adminAgree,final int status){
    	new Thread(){
    		@Override
			public void run(){
    			Connection connection = getRemoteConn();
    	    	Statement statement = null;
    	    	String sql ="";
    	    	if(status == 1){
    	    		sql = "update fe_feedback set admin_reply_agree="+ adminAgree + " where id=" + feedbackId;
    	    	}else{
    	    		sql = "update fe_feedback set admin_reply_disagree="+ adminAgree + " where id=" + feedbackId;
    	    	}
    	    	String sql4Agree = "insert into  fe_user_agreement_to_admin_reply (feedbackId, username, status) values (" + feedbackId + ",'" + username + "'," + status + ")";
    	    	try {
    				statement = connection.createStatement();
    				statement.execute(sql);
    				statement.execute(sql4Agree);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}finally{
    				try {
    					statement.close();
    					connection.close();
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}.start();
    }
    
    public boolean getAdminReplyAgreeByUser(int feedbackId, String username){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	String sql = "select * from fe_user_agreement_to_admin_reply where feedbackId=" + feedbackId + " and username='" + username + "'";
    	try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			if(rs.next()){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}finally{
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
    public JSONArray getFeedbackAdd1ByUser(String username){
    	Connection connection = this.getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	String sql = "select * from fe_user_agreement_to_feedback where username ='" + username + "'";
    	JSONArray ja = new JSONArray();
    	try {
    		statement = connection.createStatement();
    		rs = statement.executeQuery(sql);
    		while(rs.next()){
    			JSONObject jo = new JSONObject();
    			jo.put("feedbackId", rs.getInt(2));
    			jo.put("username", rs.getString(3));
    			jo.put("status", rs.getInt(4));
    			ja.put(jo);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    	return ja;
    }
    
    public int checkIsFirstTimeByUserId(long userId){
    	Connection connection = getRemoteConn();
    	Statement statement = null;
    	ResultSet rs = null;
    	String sql = "select count(*) from fe_login_record where userId=" + userId;
    	try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			while(rs.next()){
				int result = rs.getInt(1);
				if(result == 1)
					return 1;
				else return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}finally{
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    	return 0;
    }

    public JSONObject getUserVote() throws JSONException{
    	JSONObject jo = new JSONObject();
    	
    	Connection connection = getRemoteConn();
    	Statement statement = null;
    	String vote_error = "select count(*) from fe_vote where vote_error=1";
    	String vote_use = "select count(*) from fe_vote where vote_use=1";
    	String vote_ugly = "select count(*) from fe_vote where vote_ugly=1";
    	String vote_slow = "select count(*) from fe_vote where vote_slow=1";
    	String vote_other = "select count(*) from fe_vote where vote_other=1";
    	String vote_all = "select count(*) from fe_vote";
    	String [] voteName = new String [] {"出错了", "不好用", "丑丑丑", "卡卡卡", "其他问题"};
    	jo.put("投票名称", voteName);
    	int [] voteNum = new int [5];
    	try {
			statement  = connection.createStatement();
			ResultSet rsAll = null;
			rsAll = statement.executeQuery(vote_all);
			while(rsAll.next()){
//				voteNum[0] = rsAll.getInt(1);
			}
			rsAll.close();
			ResultSet rsError = null;
			rsError = statement.executeQuery(vote_error);
			while(rsError.next()){
				voteNum[0] = rsError.getInt(1);
			}
			rsError.close();
			ResultSet rsUse = null;
			rsUse = statement.executeQuery(vote_use);
			while(rsUse.next()){
				voteNum[1] = rsUse.getInt(1);
			}
			rsUse.close();
			ResultSet rsUgly = null;
			rsUgly = statement.executeQuery(vote_ugly);
			while(rsUgly.next()){
				voteNum[2] = rsUgly.getInt(1);
			}
			rsUgly.close();
			ResultSet rsSlow = null;
			rsSlow = statement.executeQuery(vote_slow);
			while(rsSlow.next()){
				voteNum[3] = rsSlow.getInt(1);
			}
			rsSlow.close();
			ResultSet rsOther = null;
			rsOther = statement.executeQuery(vote_other);
			while(rsOther.next()){
				voteNum[4] = rsOther.getInt(1);
			}
			rsOther.close();
			jo.put("投票数量", voteNum);
		} catch (Exception e) {
			e.printStackTrace();
			return jo;
		}finally{
			try {
				statement.close();
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    	
    	return jo;
    }
    
    public JSONObject getUserFeedbackStatus() throws JSONException{
    	Connection connection = getRemoteConn();
    	Statement statement = null;
    	ResultSet rs1 = null;
    	ResultSet rs2 = null;
    	String sql1 = "select count(*) from fe_feedback where status=1";
    	String sql2 = "select count(*) from fe_feedback where status!=1 and description!=''";
    	
    	JSONObject jo = new JSONObject();
    	jo.put("状态", new String [] {"已解决","未解决"});
    	int num [] = new int[2];
    	try {
			statement = connection.createStatement();
			rs1 = statement.executeQuery(sql1);
			while(rs1.next()){
				num[0] = rs1.getInt(1);
			}
			rs2 = statement.executeQuery(sql2);
			while(rs2.next()){
				num[1] = rs2.getInt(1);
			}
			jo.put("数量", num);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				rs1.close();
				rs2.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return jo;
    }
    
    //正式环境中的URL
    public static final String url = "http://gw.api.taobao.com/router/rest";
    
    //创建应用时，TOP颁发的唯一标识，TOP通过App Key来鉴别应用的身份。调用接口时必须传入的参数。
    public static final String appkey = "23025387";
    
    //App Secret是TOP给应用分配的密钥，开发者需要妥善保存这个密钥，这个密钥用来保证应用来源的可靠性，防止被伪造。
    public static final String secret = "b4a25a49e7c399f8ab5cad23b15ab2b3";
    
    //得到用户Nick
    public JSONObject getTaobaoUserNick(String taobaoCode) throws JSONException, ApiException{
		String url="https://oauth.taobao.com/token";
	      Map<String,String> props=new HashMap<String,String>();
	      props.put("grant_type","authorization_code");
	      /*测试时，需把test参数换成自己应用对应的值*/
	      props.put("code",taobaoCode);
	      props.put("client_id",appkey);
	      props.put("client_secret",secret);
	      props.put("redirect_uri","http://120.27.40.246:8080/WebReport/ReportServer?op=fbi");
	      props.put("view","web");
	      String s="";
	      String access_token = "";
	      JSONObject user = new JSONObject();
	      try{
	    	  s = WebUtils.doPost(url, props, 30000, 30000);
	    	  JSONObject json = new JSONObject(s);
	    	  access_token = json.getString("access_token");
	    	  user.put("nick", json.get("taobao_user_nick"));
	    	  user.put("userId", json.get("taobao_user_id"));
	    	  System.out.println(access_token);
	      }catch(IOException e){
	          e.printStackTrace();
	      }
//	      TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
//	      UserSellerGetRequest req=new UserSellerGetRequest();
//	      req.setFields("nick");
//	      UserSellerGetResponse response = client.execute(req , access_token);
//	      com.taobao.api.domain.User user = response.getUser();
//	      String nick = user.getNick();
	      return user;
	}

    //明道
    public static final String AUTHURL="https://api.mingdao.com/oauth2/authorize?";
    public static final String ACCEURL="https://api.mingdao.com/oauth2/access_token?";
    public static final String USEURL="https://api.mingdao.com/passport/detail?";

    public static final  String APPKEY="E8D10832FF09";//应用的appkey
    public static final  String APPSECRET="BBFF43ED8668953BA661E4577E1BE6C";//应用的appSecret
    public static final  String RESPONSE_TYPE="token";//token或者code
    public static final  String REDIRECT_URI="http://120.27.40.246:8080/WebReport/ReportServer?op=fbi";//应用设置的回调地址

    public String getAuthorizeUrl(){
        String url=AUTHURL
                +"app_key="+APPKEY
                +"&redirect_uri="+REDIRECT_URI
                +"&response_type="+RESPONSE_TYPE;
        return url;
    }

    public String getAccessTokenUrl(String code){
        String url=ACCEURL
                +"app_key="+APPKEY
                +"&app_secret="+APPSECRET
                +"&grant_type=authorization_code"
                +"&format=json"
                +"&redirect_uri="+REDIRECT_URI
                +"&code="+code;
        return url;
    }

    public String getUserDetail (String access_token, String format){
        String url = USEURL + "access_token=" + access_token + "&format=" + format;
        String result = MingDaoHttpUtil.httpByGet2StringSSL(url, null, null);
        return result;
    }

    public String getAccessTokenByCode(String code){
        String url =getAccessTokenUrl(code);
        String result= MingDaoHttpUtil.httpByGet2StringSSL(url, null, null);
        String accessToken = "";
        try{
            JSONObject jo = new JSONObject(result);
            accessToken = jo.optString("access_token");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return accessToken;
    }

    public JSONObject getMingDaoUser (String code){

        String mingDaoUserJSON = getUserDetail(getAccessTokenByCode(code), "json");
        JSONObject jo = new JSONObject();
        try{
            jo = new JSONObject(mingDaoUserJSON);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jo;
    }

	private static final String SENDCOULDURL = "https://sendcloud.sohu.com/webapi/mail.send_template.json";
	private static final String SENDCOULDAPIUSER = "hihidatauserconfig";
	private static final String SENDCOULDAPIKEY = "xY3Qad97D3bQoYwp";
	private static final String SENDCOULDEMAILSERVICE = "Hihidata Service";
	private static final String SENDCOULDINVITE = "Hihidata Invite";


	/**
	 *  通过 sendcould 发送邮件
	 * @param emailList 邮件列表
	 * @param sub 模版参数
	 * @param subject 主题
	 * @param from 发送地址
	 * @return
	 * @throws Exception
	 */
	public static boolean sendEmailBySendCould(JSONArray emailList, JSONObject sub, String subject, String from, int item) throws Exception{
		JSONObject jo = new JSONObject();
		jo.put("to", emailList);
		jo.put("sub", sub);
		String url=SENDCOULDURL;
		Map<String,String> props=new HashMap<String,String>();
		props.put("api_user", SENDCOULDAPIUSER);
		props.put("api_key", SENDCOULDAPIKEY);
		props.put("substitution_vars", jo.toString());
		props.put("template_invoke_name", item == 0 ? SENDCOULDINVITE : SENDCOULDEMAILSERVICE);
		props.put("subject", subject);
		props.put("from", from);
		String result = WebUtils.doPost(url, props, 30000, 30000);
		JSONObject re = new JSONObject(result);
		if(re.getString("message").equals("success"))
			return true;
		return false;
	}

	public static void main(String [] args) throws Exception{
//		sendEmailBySendCouldNormal();
		JSONArray email = new JSONArray();
		email.put("shanlanjie@gmail.com");
		email.put("shanlanjie@foxmail.com");

		JSONArray username = new JSONArray();
		username.put("young");
		username.put("young");
		JSONArray link = new JSONArray();
		link.put("http://baidu.com");
		link.put("http://baidu.com");
		JSONObject sub = new JSONObject();
		sub.put("%username%", username);
		sub.put("%hihidatalink%", link);
		String subject = "service";
		String from = "service@hihidata.com";

//		System.out.print(sendEmailBySendCould(email, sub, subject, from));
	}

	public static void sendEmailBySendCouldNormal() throws Exception{
		String url = "https://sendcloud.sohu.com/webapi/mail.send.xml";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);

		List nvps = new ArrayList();
		nvps.add(new BasicNameValuePair("api_user", SENDCOULDAPIUSER)); 						//使用api_user和api_key进行验证
		nvps.add(new BasicNameValuePair("api_key", SENDCOULDAPIKEY));
		nvps.add(new BasicNameValuePair("from", "service@fineexcel.com")); 						// 发信人，用正确邮件地址替代
		nvps.add(new BasicNameValuePair("to", "993810612@qq.com"));			 					// 收件人地址，用正确邮件地址替代，多个地址用';'分隔
		nvps.add(new BasicNameValuePair("subject", "SendCloud java webapi example"));
		nvps.add(new BasicNameValuePair("html", "你太棒了！你已成功的从SendCloud发送了一封测试邮件，接下来快登录前台去完善账户信息吧！"));
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

		HttpResponse response = httpclient.execute(httpost);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
			System.out.println(EntityUtils.toString(response.getEntity()));
		} else {
			System.err.println("error");
		}
	}

	/**
	 * sina weibo
	 *
	 */
	public JSONObject getSinaWebUser(String code) throws Exception{
//		Oauth oauth = new Oauth();
//		AccessToken accessToken = oauth.getAccessTokenByCode(code);
//		Account account = new Account(accessToken.getAccessToken());
//		Users users = new Users(accessToken.getAccessToken());
//		weibo4j.model.User user = users.showUserById(account.getUid().getString("uid"));
		JSONObject jo = new JSONObject();
//		jo.put("id", user.getId());
//		jo.put("username", user.getName());
		return  jo;
	}

	/**
	 * 检查微博用户是否存在
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean checkSinaWebUser(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select * from fe_user_info_test where weibo_id='" + id.split("_")[1] + "'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return false;
	}

	/**
	 * 根据 weibo id 获取 用户 id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int getUserIdByWeiboId(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select id from fe_user_info_test where weibo_id='" + id + "'";
		int userId = 0;
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				userId = resultSet.getInt(1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return userId;
	}

	/**
	 * 插入 weibo 用户到数据库
	 */
	public void insertWeiboUser2DB(long id, String username) throws Exception{
		//如果没有返回，继续执行 插入
		StringBuffer sql1 = new StringBuffer();
		sql1.append("insert into fe_user_info_test (id, username, registerDate, weibo_id) values ('")
				.append(id).append("','").append(username).append("','")
				.append(new Timestamp(Calendar.getInstance().getTimeInMillis())).append("','")
				.append(username.split("_")[1]).append("')");
		Connection connection = getRemoteConn();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(sql1.toString());
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			statement.close();
			connection.close();
		}
	}

	/**
	 * QQ access token
	 */
	public String getQQAccessToken(String code){
		String url="https://graph.qq.com/oauth2.0/token";
		Map<String,String> props=new HashMap<String,String>();
		props.put("grant_type","authorization_code");
	      /*测试时，需把test参数换成自己应用对应的值*/
		props.put("code",code);
		props.put("client_id","101186100");
		props.put("client_secret","08de481cc61593a2db31476aadbc3076");
		props.put("redirect_uri","hihidata.com");
		props.put("view","web");
		String s;
		String access_token="";
		try{
			s = WebUtils.doPost(url, props, 30000, 30000);
			String a = s.split("&")[0];
			access_token = a.split("=")[1];
			System.out.println(access_token);
		}catch(IOException e){
			e.printStackTrace();
		}
		return access_token;
	}

	public String getQQOpenId(String accessToken){
		String url="https://graph.qq.com/oauth2.0/me";
		Map<String,String> props=new HashMap<String,String>();
		props.put("access_token",accessToken);
		String s;
		String openId="";
		try{
			s = WebUtils.doPost(url, props, 30000, 30000);
			String t = s.split("\\(")[1];
			String temp = t.split("\\)")[0];
			JSONObject jo = new JSONObject(temp);
			openId = jo.getString("openid");
			System.out.println(openId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return openId;
	}

	/**
	 * 获取 QQ 用户信息
	 */
	public JSONObject getQQUserbyCode(String code){
		String accessToken = getQQAccessToken(code);
		String openId = getQQOpenId(accessToken);
		String url="https://graph.qq.com/user/get_user_info";
		Map<String,String> props=new HashMap<String,String>();
		props.put("oauth_consumer_key", "101186100");
		props.put("access_token", accessToken);
		props.put("openid", openId);
		props.put("format", "json");
		String s;
		JSONObject jo = new JSONObject();
		try{
			s = WebUtils.doPost(url, props, 30000, 30000);
			JSONObject jo_ = new JSONObject(s);
			jo.put("username", jo_.getString("nickname"));
			jo.put("openid", openId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jo;
	}

	/**
	 * 检查 QQ 用户是否存在
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean checkQQUser(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select * from fe_user_info_test where qq_id='" + id + "'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return false;
	}

	/**
	 * 根据 qq id 获取 用户 id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int getUserIdByQQId(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select id from fe_user_info_test where qq_id='" + id + "'";
		int userId = 0;
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				userId = resultSet.getInt(1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return userId;
	}

	/**
	 * 插入 QQ 用户到数据库
	 */
	public void insertQQUser2DB(long id, String username) throws Exception{
		//如果没有返回，继续执行 插入
		StringBuffer sql1 = new StringBuffer();
		sql1.append("insert into fe_user_info_test (id, username, registerDate, qq_id) values ('")
				.append(id).append("','").append(username).append("','")
				.append(new Timestamp(Calendar.getInstance().getTimeInMillis())).append("','")
				.append(username).append("')");
		Connection connection = getRemoteConn();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.execute(sql1.toString());
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			statement.close();
			connection.close();
		}
	}


	/**
	 * 判断是否绑定qq 、weibo
	 */
	public JSONObject checkIfBindWeiBOandQQ(long userId) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		JSONObject jo = new JSONObject();
		String sql = "select weibo_id, qq_id from fe_user_info_test where id='" + userId + "'";
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				jo.put("weibo_id", resultSet.getString(1)==null ? "" : resultSet.getString(1));
				jo.put("qq_id", resultSet.getString(2)==null ? "" : resultSet.getString(2));
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return  jo;
	}

	/**
	 * 检查微博是否已被绑定
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean checkWeiboBinded(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select * from fe_user_info_test where weibo_id='" + id + "'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return true;
	}

	/**
	 * 微博绑定
	 * @param userId
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public boolean bindWeibo(long userId, String code) throws Exception{
		JSONObject jo = getSinaWebUser(code);
		String id = jo.getString("id");
		if(!checkWeiboBinded(id))
			return false;
		String sql = "update fe_user_info_test set weibo_id='" + id + "' where id='" + userId + "'";
		Connection connection = getRemoteConn();
		Statement statement = null;
		try{
			statement = connection.createStatement();
			statement.execute(sql);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			statement.close();
			connection.close();
		}
		return true;
	}

	/**
	 * 检查 QQ 是否已被绑定
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean checkQQBinded(String id) throws Exception{
		Connection connection = getRemoteConn();
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = "select * from fe_user_info_test where qq_id='" + id + "'";
		try{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			resultSet.close();
			statement.close();
			connection.close();
		}
		return true;
	}

	/**
	 * QQ 绑定
	 * @param userId
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public boolean bindQQ(long userId, String code) throws Exception{
		JSONObject jo = getSinaWebUser(code);
		String id = jo.getString("id");
		if(!checkQQBinded(id))
			return false;
		String sql = "update fe_user_info_test set qq_id='" + id + "' where id='" + userId + "'";
		Connection connection = getRemoteConn();
		Statement statement = null;
		try{
			statement = connection.createStatement();
			statement.execute(sql);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			statement.close();
			connection.close();
		}
		return true;
	}
}