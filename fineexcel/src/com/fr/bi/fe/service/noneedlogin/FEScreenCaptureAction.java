package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

public class FEScreenCaptureAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "create_screen_capture";
	}

	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);
//		File parentFile = new File( FRContext.getCurrentEnv().getPath() + BIBaseConstant.CUBEDATA.CUBE_DATA_PATH + File.separator + userId + File.separator + "screen_capture" );
//		if( !parentFile.exists() ) {
//			parentFile.mkdirs();
//		}
//		final File file = new File( parentFile, DateUtils.getDate2AllIncludeSSS(new Date()) + ".jpg" );
//		new Thread(){
//			public void run(){
//				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//				Rectangle screenRectangle = new Rectangle(screenSize);
//				Robot robot;
//				try {
//					robot = new Robot();
//					BufferedImage image = robot.createScreenCapture(screenRectangle);
//					ImageIO.write(image, "jpg", file);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
	    JSONObject jo = new JSONObject();
	    jo.put("picture_name", "fe");
	    WebUtils.printAsJSON(res, jo);
	}

}