/* 
 * @# Attachment.java 
 *
 * Copyright 2012 Roberto Badaro
 */
package com.obadaro.jinah.mail;


/**
 * Class used to represent a mail attachment.
 * 
 * @author Roberto Badaro
 * 
 */
public class Attachment extends Body {

    public static final String OCTET_STREAM = "application/octet-stream";

    protected Disposition disposition = Disposition.attachment;


    {
        contentType = Attachment.OCTET_STREAM;
    }

    /**
     * No-arg constructor.
     */
    public Attachment() {
        // NOOP
    }

    /**
     * Creates an attachment.
     * 
     * @param content
     *            Attachment content.
     * @param contentType
     *            Attachment content type. If not provided, "application/octet-stream" will be used.
     * @param label
     *            Used as the attachment filename.
     */
    public Attachment(Object content, String contentType, String label) {

        setContent(content);
        this.contentType = contentType;
        this.label = label;
    }

    /**
     * Describes the content disposition.
     */
    @Override
    public String toString() {
        return "Content-Disposition = " + disposition +
                (label != null ? "; filename = \"" + label + "\"" : "");
    }

    /**
     * 
     * @param content
     *            Attachment content.
     * @param type
     *            Attachment content type. If not provided, "application/octet-stream" will be used.
     */
    public void setContent(Object content, String type) {

        this.content = content;
        contentType = type;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    // Inner classes


    public enum Disposition {
        inline, attachment
    }
}
