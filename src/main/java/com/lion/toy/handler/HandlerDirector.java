package com.lion.toy.handler;

import com.lion.toy.handler.impl.LHHandler;
import com.lion.toy.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class HandlerDirector {

    private static Map<String, IHandler> handlerDirector = new HashMap<String, IHandler>();

    static {
        handlerDirector.put(Constants.KEY_WORD_LH, new LHHandler());
    }

    public static IHandler getHandler(String content) {
        if (StringUtils.isEmpty(content)) {
            return handlerDirector.get(Constants.KEY_WORD_XH);
        }
        return handlerDirector.get(content.toLowerCase());
    }

}
