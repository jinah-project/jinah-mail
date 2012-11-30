/* 
 * JINAH Project - Java Is Not A Hammer
 * http://obadaro.com/jinah
 *
 * Copyright 2010-2012 Roberto Badaro 
 * and individual contributors by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obadaro.jinah.mail;

/**
 * @author Roberto Badaro
 * 
 */
public abstract class Body {

    protected String label;
    protected String labelCharset;
    protected Object content;
    protected String contentType;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelCharset() {
        return labelCharset;
    }

    public void setLabelCharset(String labelCharset) {
        this.labelCharset = labelCharset;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
