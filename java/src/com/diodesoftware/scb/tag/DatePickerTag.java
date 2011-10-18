package com.diodesoftware.scb.tag;

import org.apache.log4j.Logger;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 14, 2007
 * Time: 9:35:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatePickerTag extends TagSupport {
    private Logger log = Logger.getLogger(DatePickerTag.class);
    private String parameterName;
    private String style;
    private String styleClass;
    private String currentValue;

    public static DateFormat dateFormat  = new SimpleDateFormat("yyyy/MM/dd");
    



    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public int doStartTag()
    {
        try{
        StringBuffer sb = new StringBuffer();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        currentValue = request.getParameter(parameterName);
        String inputId = parameterName + "_i";
        String triggerId = parameterName + "_p";
        sb.append("<input type='text' name='").append(parameterName).append("'");
        sb.append(" id='").append(inputId).append("'");
        if(currentValue != null){
            sb.append(" value='").append(currentValue).append("'");
        }
        if(style != null){
            sb.append(" style='").append(style).append("'");
        }
        if(styleClass != null){
            sb.append(" class='").append(styleClass).append("'");
        }
        sb.append("/>");
        sb.append("<img\n" +
                "src=\"/cl1p-inc-rgdm/calendar/img.gif\" id=\"" + triggerId + "\" style=\"cursor: pointer; border: 1px solid\n" +
                "red;\" title=\"Date selector\" onmouseover=\"this.style.background='red';\"\n" +
                "onmouseout=\"this.style.background=''\" />");
        sb.append("<script type=\"text/javascript\">\n" +
                "    Calendar.setup({\n" +
                "        inputField     :    \"" +  inputId +"\",     // id of the input field\n" +
                "        ifFormat       :    \"%m/%d/%Y\",     // format of the input field (even if hidden, this format will be honored)\n" +
                "        displayArea    :    \"show_e\",       // ID of the span where the date is to be shown\n" +
                "        daFormat       :    \"%A, %B %d, %Y\",// format of the displayed date\n" +
                "        button         :    \"" + triggerId + "\",  // trigger button (well, IMG in our case)\n" +
                "        align          :    \"Tl\",           // alignment (defaults to \"Bl\")\n" +
                "        singleClick    :    true\n" +
                "    });\n" +
                "</script>");
        try
        {
            pageContext.getOut().print(sb.toString());
        }catch(IOException e)
        {
            log.error(e);
        }
        return SKIP_BODY;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
