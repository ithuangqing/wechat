package com.ithuangqing.wechat.constants;

import org.apache.http.message.BasicHeader;

public class HttpConst {
    public static final BasicHeader[] REQUEST_HEADER = new BasicHeader[]{
            new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
            new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"),
            new BasicHeader("Accept-Encoding", "gzip, deflate"),
            new BasicHeader("Accept-Accept-Language", "en,zh-CN;q=0.8,zh;q=0.6")
    };

    public static final String SOUGOU_SEARCH_KEY_WORD = "KEY_WORD";
    public static final String SOUGOU_SEARCH_PAGE_NUM = "PAGE_NUM";
}
