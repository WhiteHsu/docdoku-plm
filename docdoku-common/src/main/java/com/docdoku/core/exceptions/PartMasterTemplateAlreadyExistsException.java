/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2013 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Affero General Public License for more details.  
 *  
 * You should have received a copy of the GNU Affero General Public License  
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.  
 */

package com.docdoku.core.exceptions;

import com.docdoku.core.exceptions.ApplicationException;
import com.docdoku.core.product.PartMasterTemplate;
import java.text.MessageFormat;
import java.util.Locale;

/**
 *
 * @author Morgan Guimard
 */
public class PartMasterTemplateAlreadyExistsException extends ApplicationException {

    private PartMasterTemplate mPartMTemplate;


    public PartMasterTemplateAlreadyExistsException(String pMessage) {
        super(pMessage);
    }


    public PartMasterTemplateAlreadyExistsException(Locale pLocale, PartMasterTemplate pPartMTemplate) {
        this(pLocale, pPartMTemplate, null);
    }

    public PartMasterTemplateAlreadyExistsException(Locale pLocale, PartMasterTemplate pPartMTemplate, Throwable pCause) {
        super(pLocale, pCause);
        mPartMTemplate=pPartMTemplate;
    }

    public String getLocalizedMessage() {
        String message = getBundleDefaultMessage();
        return MessageFormat.format(message,mPartMTemplate);
    }
}