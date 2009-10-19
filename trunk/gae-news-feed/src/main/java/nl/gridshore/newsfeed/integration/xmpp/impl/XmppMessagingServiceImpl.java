package nl.gridshore.newsfeed.integration.xmpp.impl;

import com.google.appengine.api.xmpp.*;
import nl.gridshore.newsfeed.domain.ReceivedMessageService;
import nl.gridshore.newsfeed.integration.xmpp.XmppMessagingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jettro Coenradie
 */
@Service
public class XmppMessagingServiceImpl implements XmppMessagingService {
    private final static Logger log = Logger.getLogger(XmppMessagingServiceImpl.class);

    @Autowired
    private ReceivedMessageService receivedMessageService;

    @Override
    public void sendMessage(String receiver, String message) {
        JID jid = new JID(receiver);
        sendMessage(jid, message);
    }

    @Override
    public void handleReceivedMessage(Message receivedMessage) {
        JID fromJid = receivedMessage.getFromJid();
        String body = receivedMessage.getBody();

        receivedMessageService.createReceivedMessage(fromJid.getId(),body);

        sendMessage(fromJid,"Thank you for your response");
    }

    private void sendMessage(JID jid, String message) {
        Message msg = new MessageBuilder()
                .withRecipientJids(jid)
                .withBody(message)
                .build();
        boolean messageSent = false;
        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        if (xmpp.getPresence(jid).isAvailable()) {
            SendResponse status = xmpp.sendMessage(msg);
            messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
        }

        if (!messageSent) {
            log.error("Cannot send a message using xmpp to : " + jid.toString());
        }
    }
}