package com.diodesoftware.scb.tags.table;

import javax.servlet.jsp.tagext.TagSupport;


/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jun 23, 2007
 * Time: 10:27:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class TableTag extends TagSupport {




    public int doStartTag()
    {


        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag(){

        return EVAL_BODY_AGAIN;
    }

}
