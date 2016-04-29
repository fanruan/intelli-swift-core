package com.fr.bi.web.base.services;

import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONObject;
import com.fr.parser.FRLexer;
import com.fr.parser.FRParser;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;

/**
 * Created by 小灰灰 on 2015/9/28.
 */
public class BICheckValidationOfExpressionAction extends AbstractBIBaseAction {


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        JSONObject jo = new JSONObject();
        try {
            String expression = WebUtils.getHTTPRequestParameter(req, "expression");
            StringReader in = new StringReader(expression);

            FRLexer lexer = new FRLexer(in);
            FRParser parser = new FRParser(lexer);
            parser.parse();
            jo.put("validation","valid");
        } catch (Exception e) {
            jo.put("validation","invalid");

        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "check_validation_of_expression";
    }

}