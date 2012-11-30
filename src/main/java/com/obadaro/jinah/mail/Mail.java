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

import static com.obadaro.jinah.common.util.Preconditions.checkArgument;
import static com.obadaro.jinah.common.util.Strings.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to configure a mail to be sent.
 * 
 * @author Roberto Badaro
 */
public class Mail extends Body {

    protected Map<String, String> header;
    protected String sender;
    protected String from;
    protected StringBuilder _replyTo;
    protected StringBuilder _to;
    protected StringBuilder _cc;
    protected StringBuilder _bcc;

    protected List<Attachment> attachments;

    /**
     * Appends a "reply-to" mail address.
     * 
     * @param mailAddress
     * @return A reference to this object.
     */
    public Mail addReplyTo(String mailAddress) {
        _replyTo = append(_replyTo, mailAddress);
        return this;
    }

    /**
     * Appends a "To" mail address.
     * 
     * @param mailAddress
     * @return A reference to this object.
     */
    public Mail addTo(String mailAddress) {
        _to = append(_to, mailAddress);
        return this;
    }

    /**
     * Appends a "Cc" mail address.
     * 
     * @param mailAddress
     * @return A reference to this object.
     */
    public Mail addCc(String mailAddress) {
        _cc = append(_cc, mailAddress);
        return this;
    }

    /**
     * Appends a "Bcc" mail address.
     * 
     * @param mailAddress
     * @return A reference to this object.
     */
    public Mail addBcc(String mailAddress) {
        _bcc = append(_bcc, mailAddress);
        return this;
    }

    /**
     * Adds an attachment.
     * 
     * @param attachment
     * @return A reference to this object.
     */
    public Mail attach(Attachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<Attachment>(3);
        }
        attachments.add(attachment);

        return this;
    }

    /**
     * Removes an attachment from the mail.
     * 
     * @param attachment
     *            Attachment reference to remove.
     * @return A reference to this object.
     */
    public Mail detach(Attachment attachment) {
        if (attachments != null) {
            while (attachments.remove(attachment)) {
                // Removes all existent occurrences.
            }
        }
        return this;
    }

    /**
     * Sets a mail header value.
     * 
     * @param name
     *            Header name.
     * @param value
     *            Header value.
     * @return A reference to this object.
     */
    public Mail setHeaderValue(String name, Object value) {

        checkArgument(isNotBlank(name), "name");

        if (header == null) {
            header = new HashMap<String, String>(3);
        }

        String svalue = null;

        if (value != null) {
            if (value instanceof String) {
                svalue = (String) value;
            } else {
                svalue = value.toString();
            }
        }

        if (isNotBlank(svalue)) {
            header.put(name, svalue);
        } else {
            header.remove(name);
        }

        return this;
    }

    /**
     * Returns the header value or {@code null} if the header name not exists.
     * 
     * @param name
     *            Header name.
     * @return The header value or {@code null} if the header name not exists.
     */
    public String getHeaderValue(String name) {
        return (header != null ? header.get(name) : null);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> headerValues) {
        header = headerValues;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return toStringOrNull(_replyTo);
    }

    public void setReplyTo(String replyTo) {
        _replyTo = toStringBuilderOrNull(replyTo);
    }

    public String getTo() {
        return toStringOrNull(_to);
    }

    public void setTo(String to) {
        _to = toStringBuilderOrNull(to);
    }

    public String getCc() {
        return toStringOrNull(_cc);
    }

    public void setCc(String cc) {
        _cc = toStringBuilderOrNull(cc);
    }

    public String getBcc() {
        return toStringOrNull(_bcc);
    }

    public void setBcc(String bcc) {
        _bcc = toStringBuilderOrNull(bcc);
    }

    public String getSubject() {
        return label;
    }

    public void setSubject(String subject) {
        label = subject;
    }

    public void setSubject(String subject, String charset) {
        label = subject;
        labelCharset = charset;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    //
    // Internal methods
    //

    protected String toStringOrNull(StringBuilder sb) {

        return (sb != null ? sb.toString() : null);
    }

    protected StringBuilder toStringBuilderOrNull(String s) {

        return (isNotBlank(s) ? new StringBuilder(s) : null);
    }

    protected StringBuilder append(StringBuilder target, String address) {

        checkArgument(isNotBlank(address), "address");

        if (target == null) {
            target = new StringBuilder();
        }
        if (target.length() > 0) {
            target.append(",");
        }

        return target.append(address);
    }
}
