package org.wkh.bateman.fetch;

import java.util.List;
import java.io.IOException;
import java.util.Arrays;
import org.apache.http.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.wkh.bateman.trade.TimeSeries;

public abstract class QuoteFetcher
{
    
    abstract public String fetchQuotes(String symbol, int days, int interval)
        throws Exception;
        
    abstract public List<Quote> parseQuotes(String quoteList, int interval);
    
    // 协调两个api股票代码不一致的情况
    abstract public String parseSymbol(String symbol);
    
    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param symbol
     * @param days 时长
     * @param interval 颗粒度单位秒
     * @return
     * @throws Exception [参数说明]
     *             
     * @return TimeSeries [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public TimeSeries fetchAndParse(String symbol, int days, int interval)
        throws Exception
    {
        symbol = parseSymbol(symbol);
        String requestResult = fetchQuotes(symbol, days, interval);
        List<Quote> parsed = parseQuotes(requestResult, interval);
        
        QuoteCollection qc = new QuoteCollection();
        
        return qc.convertQuoteToTimeSeries(parsed);
    }
    
    protected String fetchURLasString(String url)
        throws IOException, ParseException
    {
        System.out.println("URL-->" + url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        EntityUtils.consume(entity);
        httpGet.releaseConnection();
        return body;
    }
    
    protected String[] dropLines(String quoteList, int n)
    {
        String[] lines = quoteList.split("\n");
        lines = Arrays.copyOfRange(lines, n, lines.length);
        return lines;
    }
}
