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
package com.obadaro.jinah.mail.send;

import static com.obadaro.jinah.common.util.Preconditions.checkArgument;
import static com.obadaro.jinah.common.util.Strings.ifBlank;
import static com.obadaro.jinah.common.util.Strings.isAllBlank;
import static com.obadaro.jinah.common.util.Strings.isNotBlank;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import com.obadaro.jinah.common.util.Nulls;
import com.obadaro.jinah.common.util.Strings;
import com.obadaro.jinah.mail.Attachment;
import com.obadaro.jinah.mail.Attachment.Disposition;
import com.obadaro.jinah.mail.Body;
import com.obadaro.jinah.mail.Mail;
import com.obadaro.jinah.mail.MailException;
import com.obadaro.jinah.mail.TextMail;

/**
 * @author Roberto Badaro
 *
 */
public class MailMessages {

    protected static final String UTF8 = "UTF-8";
    protected static final Disposition DEFAULT_DISPOSITION = Disposition.attachment;

    public static MimeMessage createMessage(Session session, Mail mail) throws MailException {

        MimeMessage msg = createBaseMessage(session, mail);
        applyHeaderValues(msg, mail.getHeader());

        try {
            MimeMultipart multipart = null;
            List<Attachment> attachments = mail.getAttachments();

            if (attachments != null && !attachments.isEmpty()) {
                multipart = createAttachments(session, msg, attachments);
            } else {
                multipart = new MimeMultipart();
            }

            MimeBodyPart primaryPart = createMimeBodyPart(mail);
            multipart.addBodyPart(primaryPart, 0);
            msg.setContent(multipart);

            return msg;

        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public static MimeMultipart createAttachments(Session session,
                                                  MimeMessage message,
                                                  List<Attachment> attachments) throws MailException {

        checkArgument(session != null, "session");
        checkArgument(message != null, "message");

        if (attachments == null || attachments.isEmpty()) {
            return null;
        }

        final MimeMultipart mmp = new MimeMultipart();

        try {
            for (Attachment attachment : attachments) {
                mmp.addBodyPart(createMimeBodyPart(attachment));
            }

            return mmp;

        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public static MimeBodyPart createMimeBodyPart(Body body) throws MailException {

        MimeBodyPart bodyPart = new MimeBodyPart();
        configureMimePart(body, bodyPart);
        return bodyPart;
    }

    public static void configureMimePart(Body body, MimePart bodyPart) throws MailException {

        try {
            boolean primaryBody = (body instanceof Mail);

            if (body instanceof TextMail) {
                TextMail textmail = (TextMail) body;
                bodyPart.setText(textmail.getText(), textmail.getCharset(), textmail.getSubtype());

            } else {
                bodyPart.setContent(body.getContent(), body.getContentType());
            }

            if (body instanceof Attachment) {
                Attachment a = (Attachment) body;
                bodyPart.setDisposition(Nulls.nvl(a.getDisposition(), DEFAULT_DISPOSITION).name());
            }

            if (!primaryBody && isNotBlank(body.getLabel())) {
                bodyPart.setFileName(body.getLabel());
            }

        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public static void applyHeaderValues(MimeMessage message, Map<String, String> headers)
            throws MailException {

        checkArgument(message != null, "message");

        if (headers == null || headers.isEmpty()) {
            return;
        }

        try {
            for (Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                if (Strings.isNotBlank(value)) {
                    message.setHeader(entry.getKey(), value);
                }
            }
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public static MimeMessage createBaseMessage(Session session, Mail mail) throws MailException {

        checkArgument(session != null, "session");
        checkArgument(mail != null, "mail");

        MimeMessage msg =
                createBaseMessage(session, mail.getSender(), mail.getFrom(), mail.getReplyTo(), mail.getTo(),
                    mail.getCc(), mail.getBcc(), mail.getSubject(), mail.getLabelCharset());

        return msg;
    }

    public static MimeMessage createBaseMessage(Session session,
                                                String sender,
                                                String from,
                                                String replyTo,
                                                String to,
                                                String cc,
                                                String bcc,
                                                String subject,
                                                String subjectCharset) throws MailException {

        checkArgument(session != null, "session");
        checkArgument(isNotBlank(from), "from");
        checkArgument(!isAllBlank(to, cc, bcc), "No destinatary found (to, cc or bcc).");
        checkArgument(isNotBlank(subject), "subject");

        try {
            InternetAddress[] senderAddress = parseAddress(sender);
            InternetAddress[] fromAddress = parseAddress(from);
            InternetAddress[] replyToAddress = parseAddress(replyTo);
            InternetAddress[] toAddress = parseAddress(to);
            InternetAddress[] ccAddress = parseAddress(cc);
            InternetAddress[] bccAddress = parseAddress(bcc);

            subjectCharset = ifBlank(subjectCharset, UTF8);

            //@formatter:off
            return createBaseMessage(session, 
                (senderAddress != null ? senderAddress[0] : null),
                (fromAddress != null ? fromAddress[0] : null), 
                replyToAddress, toAddress, ccAddress, bccAddress, subject, subjectCharset);
            //@formatter:on

        } catch (AddressException e) {
            throw new MailException(e);
        }
    }

    public static MimeMessage createBaseMessage(Session session,
                                                InternetAddress sender,
                                                InternetAddress from,
                                                InternetAddress[] replyTo,
                                                InternetAddress[] to,
                                                InternetAddress[] cc,
                                                InternetAddress[] bcc,
                                                String subject,
                                                String subjectCharset) throws MailException {

        try {
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(from);
            msg.setSubject(subject, subjectCharset);

            if (sender != null) {
                msg.setSender(sender);
            }
            if (replyTo != null && replyTo.length > 0) {
                msg.setReplyTo(replyTo);
            }
            if (to != null && to.length > 0) {
                msg.setRecipients(RecipientType.TO, to);
            }
            if (cc != null && cc.length > 0) {
                msg.setRecipients(RecipientType.CC, cc);
            }
            if (bcc != null && bcc.length > 0) {
                msg.setRecipients(RecipientType.BCC, bcc);
            }

            return msg;

        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    protected static InternetAddress[] parseAddress(String address) throws AddressException {

        return (isNotBlank(address) ? InternetAddress.parse(address) : null);
    }

}
