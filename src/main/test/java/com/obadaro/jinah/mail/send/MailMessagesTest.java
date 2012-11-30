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

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.obadaro.jinah.mail.Attachment;
import com.obadaro.jinah.mail.TextMail;

/**
 * MailMessages test class.
 * 
 * @author Roberto Badaro
 */
public class MailMessagesTest {

    private Session session;

    @Before
    public void prepare() {
        Properties cfg = new Properties();
        session = Session.getInstance(cfg);
    }

    @Test
    public void tCreateMessage() throws Exception {

        TextMail mail = new TextMail();

        mail.setFrom("from@email.com");
        mail.addTo("to@email.com").addTo("to2@email.com");
        mail.setSubject("Teste");
        mail.setText("Mail content body.");

        MimeMessage msg = MailMessages.createBaseMessage(session, mail);
        MailMessages.configureMimePart(mail, msg);

        Assert.assertTrue(msg != null);

        Address[] address = msg.getFrom();
        Assert.assertTrue(address != null && address.length == 1 &&
                address[0].toString().equals(mail.getFrom()));

        address = msg.getRecipients(RecipientType.TO);
        Assert.assertTrue(address != null && address.length == 2);

        Assert.assertTrue(msg.getSubject().equals(mail.getSubject()));
        Assert.assertTrue(msg.getContent().equals(mail.getText()));
    }

    @Test
    public void tCreateMessageWithAttachment() throws Exception {

        TextMail mail = new TextMail();

        String body = "Mail content body.";
        String body2 = "<data><section>section</section></data>";

        mail.setFrom("from@email.com");
        mail.addTo("to@email.com").addTo("to2@email.com");
        mail.setSubject("Test");
        mail.setText(body);

        Attachment attachment = new Attachment(body2, "application/xml", "data.xml");
        mail.attach(attachment);

        MimeMessage msg = MailMessages.createMessage(session, mail);

        Assert.assertTrue(msg != null);

        Address[] address = msg.getFrom();
        Assert.assertTrue(address != null && address.length == 1 &&
                address[0].toString().equals(mail.getFrom()));

        address = msg.getRecipients(RecipientType.TO);
        Assert.assertTrue(address != null && address.length == 2);

        Assert.assertTrue(msg.getSubject().equals(mail.getSubject()));
        Assert.assertTrue(msg.getContent() instanceof MimeMultipart);

        MimeMultipart mmp = (MimeMultipart) msg.getContent();

        Assert.assertTrue(mmp.getCount() == 2);
        Assert.assertTrue(mmp.getBodyPart(0).getContent().equals(body));
        Assert.assertTrue(mmp.getBodyPart(1).getContent().equals(body2));
    }
}
