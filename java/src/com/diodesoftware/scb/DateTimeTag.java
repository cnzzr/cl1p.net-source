package com.diodesoftware.scb;

import org.apache.log4j.*;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.io.IOException;
import java.sql.Date;

public class DateTimeTag
extends TagSupport
{
    private String epoch;
    private String yearField = epoch + "year";
    private String monthField = epoch + "month";
    private String dayField = epoch + "day";
    private String hourField = epoch + "hourField";
    private String minuteField = epoch + "minuteField";
    private static final String GET_DATE_FUNCTION_NAME = "populateEpoch";
    private static final String ENABLED_INPUT_FIELDS = "enabledInputFields";
    private static final String JAVASCRIPT_RENDERED_KEY = "javascriptRenderedForDateTime";
    private String dropDownClass = null;
    private Logger log = Logger.getLogger(DateTimeTag.class);

    public void setEpoch(String epoch)
    {
        this.epoch = epoch;
        yearField = epoch + "year";
        monthField = epoch + "month";
        dayField = epoch + "day";
        hourField = epoch + "hour";
        minuteField = epoch + "minute";
    }

    public int doStartTag()
    {
        StringBuffer sb = new StringBuffer();
        Calendar cal = getTime();
        Object o = pageContext.getAttribute(JAVASCRIPT_RENDERED_KEY, PageContext.REQUEST_SCOPE);
        if(o == null)
        {
            pageContext.setAttribute(JAVASCRIPT_RENDERED_KEY, new Object(), PageContext.REQUEST_SCOPE);
            renderJavaScript(sb);
        }
        renderInputFields(sb, cal);
        try
        {
            pageContext.getOut().print(sb.toString());
        }catch(IOException e)
        {
            log.error(e);
        }
        return SKIP_BODY;
    }

    private void renderInputFields(StringBuffer sb, Calendar cal)
    {

        int startYear =2005;
        int endYear = 2010;
        sb.append("<table border=\"0\"><tr>");
        sb.append("<td>Year</td>");
        sb.append("<td>Month</td>");
        sb.append("<td>Day</td>");
        sb.append("<td>Hour</td>");
        sb.append("<td>Minute</td>");
        sb.append("</tr><tr>");
        renderInputField(sb, yearField, startYear, endYear, cal.get(Calendar.YEAR), false);
        renderInputField(sb, monthField, 0, 11, cal.get(Calendar.MONTH), true);
        renderInputField(sb, dayField, 1, 31, cal.get(Calendar.DAY_OF_MONTH), false);
        renderInputField(sb, hourField, 0, 23, cal.get(Calendar.HOUR_OF_DAY), false);
        renderInputField(sb, minuteField, 0,59, cal.get(Calendar.MINUTE), false);
        sb.append("<input type=\"hidden\" name=\"").append(epoch).append("\" id=\"");
        sb.append(epoch).append("\" value=\"").append(cal.getTimeInMillis()).append("\">");
        sb.append("</tr></table>");
    }

    private void renderInputField(StringBuffer sb, String name, int rangeStart, int rangeEnd, int value, boolean isMonth)
    {
        sb.append("<td><select id=\"" + name + "\" name=\"").append(name).append("\" class=\"").append(dropDownClass);
        sb.append("\" onchange=\"").append(GET_DATE_FUNCTION_NAME).append("('").append(epoch).append("');\">\n");
        int end = rangeEnd + 1;
        for(int i = rangeStart; i <end; i++)
        {
            boolean selected = value == i;
            sb.append("<option value=\"").append(i).append("\"");
            if(selected)
                sb.append(" SELECTED ");
            sb.append(">");
            int displayValue = i;
            if(isMonth)
                displayValue++;
            sb.append(displayValue);
            sb.append("</option>\n");
        }
        sb.append("</select></td>");
    }

    private void renderJavaScript(StringBuffer sb)
    {
        sb.append("<script language=\"javascript\">\n");
        sb.append("function ").append(GET_DATE_FUNCTION_NAME).append("( baseID )\n{\n");
        renderJavaScriptFields(sb, true);
        sb.append("var dateValue = new Date(year, month, day, hour, minute, 0, 0);\n");
        sb.append("var destField = document.getElementById(baseID);\n");
        sb.append("destField.value = dateValue.getTime();\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("function ").append(ENABLED_INPUT_FIELDS).append("( baseID, b )\n{\n");
        renderJavaScriptFields(sb, false);
        sb.append(" year.disabled = b;\n");
        sb.append(" month.disabled = b;\n");
        sb.append(" day.disabled = b;\n");
        sb.append(" hour.disabled = b;\n");
        sb.append(" minute.disabled = b;\n");
        sb.append("}\n");
        sb.append("</script>\n");

    }

    private void renderJavaScriptFields(StringBuffer sb, boolean value)
    {
        renderJavaScriptGetField(sb, "year", value);
        renderJavaScriptGetField(sb, "month", value);
        renderJavaScriptGetField(sb, "day", value);
        renderJavaScriptGetField(sb, "hour", value);
        renderJavaScriptGetField(sb, "minute", value);
    }

    private void renderJavaScriptGetField(StringBuffer sb, String id, boolean value)
    {
        String s = "var " + id  + " = document.getElementById(baseID + '"  + id + "');\n";
        if(value)
            s = s + id + " = " + id + ".value;\n";
        sb.append(s);
    }

    private Calendar getTime()
    {
        String current = pageContext.getRequest().getParameter(epoch);
        Calendar cal = null;
        if(current != null)
        {
            try
            {
                long l = Long.parseLong(current);
                Date date = new Date(l);
                cal = Calendar.getInstance();
                cal.setTime(date);


            }catch(NumberFormatException ignore){}
        }
        if(cal == null)
        {
            cal = getEpochValue((HttpServletRequest)pageContext.getRequest());
        }
         String key = getKey((HttpServletRequest)pageContext.getRequest());
         pageContext.getSession().setAttribute(key, cal);
        return cal;
    }

    private Calendar getEpochValue(HttpServletRequest request)
    {
        Calendar cal = (Calendar)request.getSession().getAttribute(getKey(request));
        if(cal != null)
        {
            return cal;
        }
        return Calendar.getInstance();
    }

    private String getKey(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        int i = uri.indexOf('?');
        if(i > 0)
            uri = uri.substring(0, i);
        String key = epoch + uri;
        return key;
    }


}
