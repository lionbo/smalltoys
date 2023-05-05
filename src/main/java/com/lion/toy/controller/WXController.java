package com.lion.toy.controller;

import com.lion.toy.bo.WXMsg;
import com.lion.toy.handler.HandlerDirector;
import com.lion.toy.handler.IHandler;
import com.lion.toy.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/wx")
public class WXController {

    private static Logger logger = LoggerFactory.getLogger(WXController.class);

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    @ResponseBody
    public String get(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                      @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {

        logger.info(signature + "," + timestamp + "," + nonce + "," + echostr);

        /**
         * 1. 将token、timestamp、nonce三个参数进行字典序排序
         * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
         * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
         */
        return echostr;
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    @ResponseBody
    public String text(HttpServletRequest httpRequest) {
        try {
            InputStream is = httpRequest.getInputStream();

            try {
                byte[] body = new byte[httpRequest.getContentLength()];
                is.read(body, 0, httpRequest.getContentLength());
                String str = new String(body);
                WXMsg wxMsg = this.extractWxMsg(str);
                WXMsg reMsg = new WXMsg();
                reMsg.setFromUserName(wxMsg.getToUserName());
                reMsg.setToUserName(wxMsg.getFromUserName());
                IHandler handler = HandlerDirector.getHandler(wxMsg.getContent());

                if (handler == null) {
                    reMsg.setContent("你的OpenId是" + wxMsg.getFromUserName());
                } else {
                    reMsg.setContent(handler.process(wxMsg.getContent()));
                }

                logger.info(wxMsg.toString());
                String res = this.changeWxMsgToXML(reMsg);
                logger.info("change finish-------------");
                logger.info(res);
                return res;
            } catch (DocumentException e) {
                logger.error("DocumentException", e);
                return Constants.REQUEST_FAIL;
            } catch (Exception e) {
                logger.error("Exception", e);
                return Constants.REQUEST_FAIL;
            } finally {
                is.close();
            }
        } catch (IOException e) {
            logger.error("IOException", e);
            return Constants.REQUEST_FAIL;
        }
    }

    private Map<String, String> getClList() {
        Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    //    public static void main(String[] args) {
    //        WXMsg reMsg = new WXMsg();
    //        reMsg.setToUserName("aaa");
    //        reMsg.setFromUserName("bbb");
    //        reMsg.setContent("test");
    //        System.out.println(WXController.changeWxMsgToXML(reMsg));
    //    }

    private String changeWxMsgToXML(WXMsg reMsg) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("xml");
        Element ToUserName = root.addElement("ToUserName");
        Element FromUserName = root.addElement("FromUserName");
        Element CreateTime = root.addElement("CreateTime");
        Element MsgType = root.addElement("MsgType");
        Element Content = root.addElement("Content");

        ToUserName.addCDATA(reMsg.getToUserName());
        FromUserName.addCDATA(reMsg.getFromUserName());
        CreateTime.setText(new Date().getTime() + "");
        MsgType.addCDATA("text");
        Content.addCDATA(reMsg.getContent());
        return document.asXML();
    }

    private WXMsg extractWxMsg(String text) throws DocumentException {
        Document document = DocumentHelper.parseText(text);
        WXMsg msg = new WXMsg();
        Element root = document.getRootElement();
        msg.setToUserName(root.elementTextTrim("ToUserName"));
        msg.setFromUserName(root.elementTextTrim("FromUserName"));
        msg.setMsgType(root.elementTextTrim("MsgType"));
        msg.setContent(root.elementTextTrim("Content"));
        if (root.elementTextTrim("CreateTime") != null) {
            msg.setCreateTime(Long.valueOf(root.elementTextTrim("CreateTime")));
        }
        if (root.elementTextTrim("MsgId") != null) {
            msg.setMsgId(Long.valueOf(root.elementTextTrim("MsgId")));
        }
        return msg;

    }

}
