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

import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import com.obadaro.jinah.common.internals.Logger;
import com.obadaro.jinah.mail.Mail;
import com.obadaro.jinah.mail.MailException;

/**
 * 
 * @author Roberto Badaro
 */
public class Postman {

    private static final Logger LOG = Logger.getLogger(Postman.class.getName());

    public static void sendMail(Session session, Mail mail) throws MailException {

        sendMail(session, false, mail);
    }

    public static void sendMail(Session session, boolean silentFail, Mail... mails) throws MailException {

        checkArgument(session != null, "session");
        checkArgument(mails != null, "mails");

        if (mails.length == 0) {
            return;
        }

        Date sentDate = new Date();
        Transport transport = null;

        try {
            transport = getConnectedTransport(session);
            for (Mail mail : mails) {
                sendMail(session, transport, mail, sentDate, silentFail);
            }

        } catch (Exception e) {
            if (silentFail) {
                LOG.warn(e, "Error sending mail.");
                return;
            }

            if (e instanceof MailException) {
                throw (MailException) e;
            } else {
                throw new MailException(e);
            }

        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (final Exception e) {
                    // NOOP
                }
            }
        }
    }

    public static Transport getConnectedTransport(Session session) throws MailException {

        checkArgument(session != null, "session");

        Properties cfg = session.getProperties();
        String protocol = ifBlank(cfg.getProperty("mail.transport.protocol"), "smtp");
        boolean needsAuth = "true".equalsIgnoreCase(cfg.getProperty("mail." + protocol + ".auth"));

        try {
            Transport transport = session.getTransport();

            if (needsAuth) {
                String usr = cfg.getProperty("mail." + protocol + ".username");
                String pwd = cfg.getProperty("mail." + protocol + ".password");
                transport.connect(usr, pwd);
            } else {
                transport.connect();
            }

            return transport;

        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    protected static boolean sendMail(Session session,
                                      Transport transport,
                                      Mail mail,
                                      Date sentDate,
                                      boolean silentFail) throws MailException {

        try {
            MimeMessage msg = MailMessages.createMessage(session, mail);
            msg.setSentDate(sentDate);

            transport.sendMessage(msg, msg.getAllRecipients());
            return true;

        } catch (Exception e) {
            if (!silentFail) {
                if (e instanceof MailException) {
                    throw (MailException) e;
                } else {
                    throw new MailException(e);
                }
            } else {
                LOG.warn(e, "Error sending mail.");
                return false;
            }
        }
    }

}
