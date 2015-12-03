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
    
    // Э������api��Ʊ���벻һ�µ����
    abstract public String parseSymbol(String symbol);
    
    /**
     * <һ�仰���ܼ���> <������ϸ����>
     * 
     * @param symbol
     * @param days ʱ��
     * @param interval �����ȵ�λ��
     * @return
     * @throws Exception [����˵��]
     *             
     * @return TimeSeries [��������˵��]
     * @exception throws [Υ������] [Υ��˵��]
     * @see [�ࡢ��#��������#��Ա]
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
