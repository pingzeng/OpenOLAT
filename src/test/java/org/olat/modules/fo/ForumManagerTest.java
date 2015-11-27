/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/ 

package org.olat.modules.fo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.olat.basesecurity.BaseSecurity;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.modules.fo.manager.ForumManager;
import org.olat.modules.fo.model.ForumThread;
import org.olat.modules.fo.model.ForumUserStatistics;
import org.olat.modules.fo.ui.MessagePeekview;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 * @author Felix Jost
 *
 */
public class ForumManagerTest extends OlatTestCase {

	@Autowired
	private DB dbInstance;
	@Autowired
	public UserManager userManager;
	@Autowired
	public ForumManager forumManager;
	@Autowired
	public BaseSecurity securityManager;
	
	@Test
	public void getThread() {
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-4");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message message = forumManager.createMessage(fo, id, false);
		message.setTitle("stufe 0: subject 0");
		message.setBody("body/n dep 0");
		forumManager.addTopMessage(message);
		dbInstance.commit();

		Long messageTopThread = message.getKey();
		List<Message> threadMessageList = forumManager.getThread(messageTopThread);
		Assert.assertEquals("Not the right number of messages for this forum", 1, threadMessageList.size());
		
		// lookup for a none existing thread
		List<Message> noneThreadMessageList = forumManager.getThread(1234l);
		Assert.assertEquals("Not the right number of messages for this forum", 0, noneThreadMessageList.size());
	}

	@Test
	public void createAndGetMessages_loadForumID() throws Exception {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("Create and get message");
		topMessage.setBody("Create and get message");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("Re: Create and get message");
		reply.setBody("Create and get message");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commitAndCloseSession();

		//load the forum
		Forum forum = forumManager.loadForum(fo.getKey());
		List<Message> messageList = forumManager.getMessagesByForum(forum);
		Assert.assertNotNull(messageList);			
		for(Message msg: messageList) {
			Assert.assertNotNull(msg);
		}
		
		Assert.assertEquals("Not the right number of messages for this forum", 2, messageList.size());
	}
	
	@Test
	public void getForumThreads() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get forum threads");
		thread1.setBody("Get forum threads");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();

		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get forum threads");
		reply.setBody("Get forum threads");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		Message thread2 = forumManager.createMessage(forum, id1, false);
		thread2.setTitle("More on get forum threads");
		thread2.setBody("More on get forum threads");
		forumManager.addTopMessage(thread2);
		dbInstance.commit();
		
		List<ForumThread> forumThreads = forumManager.getForumThreads(forum, id1);
		Assert.assertNotNull(forumThreads);
		Assert.assertEquals(2, forumThreads.size());
		
		ForumThread forumThread1 = null;
		ForumThread forumThread2 = null;
		for(ForumThread forumThread:forumThreads) {
			if(forumThread.getKey().equals(thread1.getKey())) {
				forumThread1 = forumThread;
			} else if(forumThread.getKey().equals(thread2.getKey())) {
				forumThread2 = forumThread;
			}
		}
		
		Assert.assertNotNull(forumThread1);
		Assert.assertNotNull(forumThread2);
	}
	
	@Test
	public void getForumUserStatistics() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get forum user statistics");
		thread1.setBody("Get forum user statistics");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();

		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get forum user statistics");
		reply.setBody("Get forum user statistics and other usefull stuff to we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		List<ForumUserStatistics> userStatistics = forumManager.getForumUserStatistics(forum);
		Assert.assertNotNull(userStatistics);
		Assert.assertEquals(2, userStatistics.size());

		ForumUserStatistics userStatistic1 = null;
		ForumUserStatistics userStatistic2 = null;
		for(ForumUserStatistics userStatistic:userStatistics) {
			if(userStatistic.getIdentity().getKey().equals(id1.getKey())) {
				userStatistic1 = userStatistic;
			} else if(userStatistic.getIdentity().getKey().equals(id2.getKey())) {
				userStatistic2 = userStatistic;
			}
		}
		
		Assert.assertNotNull(userStatistic1);
		Assert.assertNotNull(userStatistic2);
		
		//stats user 1
		Assert.assertEquals(1, userStatistic1.getNumOfThreads());
		Assert.assertEquals(0, userStatistic1.getNumOfReplies());
		Assert.assertTrue(userStatistic1.getNumOfWords() > 1);
		Assert.assertTrue(userStatistic1.getNumOfCharacters() > 1);
		
		//stats user 2
		Assert.assertEquals(0, userStatistic2.getNumOfThreads());
		Assert.assertEquals(1, userStatistic2.getNumOfReplies());
		Assert.assertTrue(userStatistic2.getNumOfWords() > 1);
		Assert.assertTrue(userStatistic2.getNumOfCharacters() > 1);
	}
	
	@Test
	public void getLightMessagesByForum() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get messages light by forum");
		thread1.setBody("Get messages light by forum");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get messages light by forum");
		reply.setBody("Get messages light by forum and other usefull stuff we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		//load and check the messages
		List<MessageLight> messages = forumManager.getLightMessagesByForum(forum);
		dbInstance.commitAndCloseSession();
		
		MessageLight message1 = null;
		MessageLight message2 = null;
		for(MessageLight message:messages) {
			if(message.getKey().equals(thread1.getKey())) {
				message1 = message;
			} else if(message.getKey().equals(reply.getKey())) {
				message2 = message;
			}
		}
		
		//check thread
		Assert.assertNotNull(message1);
		Assert.assertEquals(thread1.getKey(), message1.getKey());
		Assert.assertEquals(thread1.getTitle(), message1.getTitle());
		Assert.assertEquals(thread1.getBody(), message1.getBody());
		Assert.assertEquals(thread1.getCreator(), id1);
		Assert.assertNull(message1.getThreadtop());
		
		//check reply
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(message2);
		Assert.assertEquals(reply.getKey(), message2.getKey());
		Assert.assertEquals(reply.getTitle(), message2.getTitle());
		Assert.assertEquals(reply.getBody(), message2.getBody());
		Assert.assertEquals(reply.getCreator(), id2);
		Assert.assertNotNull(message2.getThreadtop());
		Assert.assertEquals(thread1.getKey(), message2.getThreadtop().getKey());
	}
	
	@Test
	public void getLightMessagesByThread() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get messages light by thread");
		thread1.setBody("Get messages light by thread");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get messages light by thread");
		reply.setBody("Get messages light by thread and other usefull stuff we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		//load and check the messages
		List<MessageLight> messages = forumManager.getLightMessagesByThread(forum, thread1);
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		MessageLight message = messages.get(0);
		Assert.assertNotNull(message);
		Assert.assertEquals(reply.getKey(), message.getKey());
		Assert.assertEquals(reply.getTitle(), message.getTitle());
		Assert.assertEquals(reply.getBody(), message.getBody());
		Assert.assertEquals(reply.getCreator(), id2);
		Assert.assertNotNull(message.getThreadtop());
		Assert.assertEquals(thread1.getKey(), message.getThreadtop().getKey());
	}
	
	@Test
	public void getLightMessagesOfGuests() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity guest2 =  securityManager.getAndUpdateAnonymousUserForLanguage(Locale.ENGLISH);
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get messages light of guests");
		thread1.setBody("Get messages light of guests");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, guest2, true);
		reply.setTitle("Re: Get messages light of guests");
		reply.setBody("Get messages light of guests and other usefull stuff we need");
		reply.setPseudonym("Guest pseudo 1289");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		//load and check the messages
		List<MessageLight> messages = forumManager.getLightMessagesOfGuests(forum);
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(messages);
		Assert.assertEquals(1, messages.size());
		MessageLight message = messages.get(0);
		Assert.assertNotNull(message);
		Assert.assertEquals(reply.getKey(), message.getKey());
		Assert.assertEquals(reply.getTitle(), message.getTitle());
		Assert.assertEquals(reply.getBody(), message.getBody());
		Assert.assertEquals("Guest pseudo 1289", message.getPseudonym());
		Assert.assertNull(message.getCreator());
		Assert.assertTrue(message.isGuest());
		Assert.assertNotNull(message.getThreadtop());
		Assert.assertEquals(thread1.getKey(), message.getThreadtop().getKey());
	}
	
	@Test
	public void getLightMessagesByUser() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 =  JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Identity id3 =  JunitTestHelper.createAndPersistIdentityAsRndUser("fo-3");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get messages light by user");
		thread1.setBody("Get messages light by user");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get messages light by user");
		reply.setBody("Get messages light by user and other usefull stuff we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		Message replyPseudo = forumManager.createMessage(forum, id3, false);
		replyPseudo.setTitle("Re: Get messages light by user");
		replyPseudo.setBody("Get messages light by user and other usefull stuff we need");
		replyPseudo.setPseudonym("Id pseudo 3476");
		forumManager.replyToMessage(replyPseudo, thread1);
		dbInstance.commitAndCloseSession();
		
		//load and check the messages of first user
		List<MessageLight> messagesOfUser1 = forumManager.getLightMessagesByUser(forum, id1);
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(messagesOfUser1);
		Assert.assertEquals(1, messagesOfUser1.size());
		MessageLight messageOfUser1 = messagesOfUser1.get(0);
		Assert.assertNotNull(messageOfUser1);
		Assert.assertEquals(thread1.getKey(), messageOfUser1.getKey());
		Assert.assertEquals(thread1.getTitle(), messageOfUser1.getTitle());
		Assert.assertEquals(thread1.getBody(), messageOfUser1.getBody());
		Assert.assertNotNull(messageOfUser1.getCreator());
		Assert.assertEquals(id1, messageOfUser1.getCreator());
		Assert.assertFalse(messageOfUser1.isGuest());
		Assert.assertNull(messageOfUser1.getThreadtop());
		
		//load and check the messages of second user
		List<MessageLight> messagesOfUser2 = forumManager.getLightMessagesByUser(forum, id2);
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(messagesOfUser2);
		Assert.assertEquals(1, messagesOfUser2.size());
		MessageLight messageOfUser2 = messagesOfUser2.get(0);
		Assert.assertNotNull(messageOfUser2);
		Assert.assertEquals(reply.getKey(), messageOfUser2.getKey());
		Assert.assertEquals(reply.getTitle(), messageOfUser2.getTitle());
		Assert.assertEquals(reply.getBody(), messageOfUser2.getBody());
		Assert.assertNotNull(messageOfUser2.getCreator());
		Assert.assertEquals(id2, messageOfUser2.getCreator());
		Assert.assertFalse(messageOfUser2.isGuest());
		Assert.assertNotNull(messageOfUser2.getThreadtop());
		Assert.assertEquals(thread1.getKey(), messageOfUser2.getThreadtop().getKey());
		
		//load and check the messages of third user which use a pseudo
		List<MessageLight> messagesOfUser3 = forumManager.getLightMessagesByUser(forum, id3);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(messagesOfUser3);
		Assert.assertTrue(messagesOfUser3.isEmpty());
	}
	
	@Test
	public void getLightMessagesByUserUnderPseudo() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 =  JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get messages light by user with pseudo");
		thread1.setBody("Get messages light by user with pseudo");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get messages light by user with pseudo");
		reply.setBody("Get messages light by user and other usefull stuff we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		Message replyPseudo = forumManager.createMessage(forum, id2, false);
		replyPseudo.setTitle("Re: Get messages light by user with pseudo");
		replyPseudo.setBody("Get messages light by user and other usefull stuff we need");
		String pseudo = "Id pseudo " + UUID.randomUUID();
		replyPseudo.setPseudonym(pseudo);
		forumManager.replyToMessage(replyPseudo, thread1);
		dbInstance.commitAndCloseSession();
		
		//load and check the messages of user with pseudo
		List<MessageLight> messagesOfUser2 = forumManager.getLightMessagesByUserUnderPseudo(forum, id2, pseudo);
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(messagesOfUser2);
		Assert.assertEquals(1, messagesOfUser2.size());
		MessageLight messageUnderPseudo = messagesOfUser2.get(0);
		Assert.assertNotNull(messageUnderPseudo);
		Assert.assertEquals(replyPseudo.getKey(), messageUnderPseudo.getKey());
		Assert.assertEquals(replyPseudo.getTitle(), messageUnderPseudo.getTitle());
		Assert.assertEquals(replyPseudo.getBody(), messageUnderPseudo.getBody());
		Assert.assertNotNull(messageUnderPseudo.getCreator());
		Assert.assertEquals(id2, messageUnderPseudo.getCreator());
		Assert.assertFalse(messageUnderPseudo.isGuest());
		Assert.assertNotNull(messageUnderPseudo.getThreadtop());
		Assert.assertEquals(thread1.getKey(), messageUnderPseudo.getThreadtop().getKey());
	}
	
	@Test
	public void getMessageById() {
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message message = forumManager.createMessage(forum, id, false);
		message.setTitle("Get message by id");
		message.setBody("Get message by id");
		forumManager.addTopMessage(message);
		dbInstance.commit();
		
		//load the message by id
		Message loadedMessage = forumManager.getMessageById(message.getKey());
		dbInstance.commitAndCloseSession();
		
		Assert.assertNotNull(loadedMessage);
		Assert.assertEquals(message.getKey(), loadedMessage.getKey());
		Assert.assertEquals(message.getTitle(), loadedMessage.getTitle());
		Assert.assertEquals(message.getBody(), loadedMessage.getBody());
		Assert.assertNotNull(loadedMessage.getCreator());
		Assert.assertEquals(id, loadedMessage.getCreator());
		Assert.assertFalse(loadedMessage.isGuest());
		Assert.assertNull(loadedMessage.getThreadtop());
	}
	
	@Test
	public void getPeekviewMessages() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 =  JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get peekview messages");
		thread1.setBody("Get peekview messages");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
		
		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Get peekview messages");
		reply.setBody("Get peekview messages");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		Message replyPseudo = forumManager.createMessage(forum, id2, false);
		replyPseudo.setTitle("Re: Get peekview messages with pseudo");
		replyPseudo.setBody("Get peekview messages and other usefull stuff we need");
		String pseudo = "Id pseudo " + UUID.randomUUID();
		replyPseudo.setPseudonym(pseudo);
		forumManager.replyToMessage(replyPseudo, thread1);
		dbInstance.commitAndCloseSession();
		
		//load the peekview
		List<MessagePeekview> peekViews = forumManager.getPeekviewMessages(forum, 2);
		Assert.assertNotNull(peekViews);
		Assert.assertEquals(2, peekViews.size());
		
		int found = 0;
		for(MessagePeekview peekView:peekViews) {
			if(peekView.getKey().equals(thread1.getKey())
					|| peekView.getKey().equals(reply.getKey())
					|| peekView.getKey().equals(replyPseudo.getKey())) {
				found++;
			}
		}
		Assert.assertEquals(2, found);
	}
	
	@Test
	public void getPseudonym() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 =  JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Get pseudonym");
		thread1.setBody("Get pseudonym");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
	
		Message replyPseudo = forumManager.createMessage(forum, id2, false);
		replyPseudo.setTitle("Re: Get pseudonym");
		replyPseudo.setBody("Get pseudonym in forum and other usefull stuff we need");
		String pseudo = "Id pseudo " + UUID.randomUUID();
		replyPseudo.setPseudonym(pseudo);
		forumManager.replyToMessage(replyPseudo, thread1);
		dbInstance.commitAndCloseSession();
		
		// get pseudonym of id 2
		String alias2 = forumManager.getPseudonym(forum, id2);
		Assert.assertEquals(pseudo, alias2);
		
		// get pseudonym of id 1
		String alias1 = forumManager.getPseudonym(forum, id1);
		Assert.assertNull(alias1);
	}
	
	@Test
	public void readMessages() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum forum = forumManager.addAForum();
		dbInstance.commit();
		
		Message thread1 = forumManager.createMessage(forum, id1, false);
		thread1.setTitle("Read messages workflow");
		thread1.setBody("Read messages workflow");
		forumManager.addTopMessage(thread1);
		dbInstance.commit();
	
		Message replyPseudo = forumManager.createMessage(forum, id1, false);
		replyPseudo.setTitle("Re: Read messages workflow");
		replyPseudo.setBody("Read messages workflow and other usefull stuff we need");
		String pseudo = "Id pseudo " + UUID.randomUUID();
		replyPseudo.setPseudonym(pseudo);
		forumManager.replyToMessage(replyPseudo, thread1);
		dbInstance.commitAndCloseSession();

		Message reply = forumManager.createMessage(forum, id2, false);
		reply.setTitle("Re: Read messages workflow");
		reply.setBody("Read messages workflow and other usefull stuff we need");
		forumManager.replyToMessage(reply, thread1);
		dbInstance.commitAndCloseSession();
		
		//mark thread1 as read
		forumManager.markAsRead(id1, forum, thread1);
		dbInstance.commitAndCloseSession();
		
		//load read set and check for id1
		Set<Long> readSet = forumManager.getReadSet(id1, forum);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(readSet);
		Assert.assertEquals(1, readSet.size());
		Assert.assertTrue(readSet.contains(thread1.getKey()));
		
		//mark thread1 as read
		forumManager.markAsRead(id2, forum, reply);
		forumManager.markAsRead(id2, forum, replyPseudo);
		dbInstance.commitAndCloseSession();
		
		//load read set and check for id2
		Set<Long> readSet2 = forumManager.getReadSet(id2, forum);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(readSet2);
		Assert.assertEquals(2, readSet2.size());
		Assert.assertTrue(readSet2.contains(reply.getKey()));
		Assert.assertTrue(readSet2.contains(replyPseudo.getKey()));
	}
	
	@Test
	public void updateMessage() {
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id, false);
		topMessage.setTitle("Message counter");
		topMessage.setBody("Message counter");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();
		
		//update message
		topMessage.setBody("Message counter and other stuff");
		Message updatedMessage = forumManager.updateMessage(topMessage, true);
		Assert.assertNotNull(updatedMessage);
		Assert.assertEquals(topMessage.getKey(), updatedMessage.getKey());
		Assert.assertEquals("Message counter", updatedMessage.getTitle());
		Assert.assertEquals("Message counter and other stuff", updatedMessage.getBody());
		Assert.assertNotNull(updatedMessage.getNumOfCharacters());
		Assert.assertEquals(27, updatedMessage.getNumOfCharacters().intValue());
		Assert.assertNotNull(updatedMessage.getNumOfWords());
		Assert.assertEquals(5, updatedMessage.getNumOfWords().intValue());
	}
	
	@Test
	public void countMessagesByForumID() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("Messages count by forum");
		topMessage.setBody("Messages count by forum");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("Re: Messages count by forum");
		reply.setBody("Messages count by forum");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commit();
		
		Message reply2 = forumManager.createMessage(fo, id1, false);
		reply2.setTitle("Re: Re: Messages count by forum");
		reply2.setBody("Messages count by forum");
		forumManager.replyToMessage(reply2, reply);
		dbInstance.commit();
		
		int numOfMessages = forumManager.countMessagesByForumID(fo.getKey());
		Assert.assertEquals("Not the right number of messages for this forum", 3, numOfMessages);
	}

	@Test
	public void countThreadsByForumID() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-1");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-2");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("Threads count by forum");
		topMessage.setBody("Threads count by forum");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("Re: Threads count by forum");
		reply.setBody("Threads count by forum");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commit();
		
		Message topMessage2 = forumManager.createMessage(fo, id2, false);
		topMessage2.setTitle("More on threads count by forum");
		topMessage2.setBody("More on threads count by forum");
		forumManager.addTopMessage(topMessage2);
		dbInstance.commit();
		
		int numOfThreads = forumManager.countThreadsByForumID(fo.getKey());
		Assert.assertEquals("Not the right number of threads for this forum", 2, numOfThreads);
	}
	
	@Test
	public void getNewMessageInfo() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-5");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-6");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("New message 1");
		topMessage.setBody("The newest stuff");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("New message 2");
		reply.setBody("The more newest stuff");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commit();

		sleep(1500);//we must ensure a lap of 1 second
		
		//check the newest messages, limit now
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		List<Message> newestMessages = forumManager.getNewMessageInfo(fo.getKey(), cal.getTime());
		Assert.assertEquals(0, newestMessages.size());
		
		//check the newest messages, limit one hour in past
		cal.add(Calendar.HOUR_OF_DAY, - 1);
		List<Message> olderLastMessages = forumManager.getNewMessageInfo(fo.getKey(), cal.getTime());
		Assert.assertEquals(2, olderLastMessages.size());
	}
	
	@Test
	public void deleteMessageTree() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-5");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-6");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("Future deleted message 1");
		topMessage.setBody("Future deleted  stuff");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("Future deleted 2");
		reply.setBody("Future deleted  stuff");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commit();
		
		Message reply2 = forumManager.createMessage(fo, id1, false);
		reply2.setTitle("Future deleted 3");
		reply2.setBody("Future deleted  stuff");
		forumManager.replyToMessage(reply2, reply);
		dbInstance.commit();
		
		//delete a message
		forumManager.deleteMessageTree(fo.getKey(), reply2);
		dbInstance.commitAndCloseSession();
		
		//delete a top message
		forumManager.deleteMessageTree(fo.getKey(), topMessage);
		dbInstance.commitAndCloseSession();
	}
	
	@Test
	public void deleteForum() {
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-7");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("fo-8");
		Forum fo = forumManager.addAForum();
		dbInstance.commit();

		Message topMessage = forumManager.createMessage(fo, id1, false);
		topMessage.setTitle("Future deleted forum part. 1");
		topMessage.setBody("Future deleted  stuff");
		forumManager.addTopMessage(topMessage);
		dbInstance.commit();

		Message reply = forumManager.createMessage(fo, id2, false);
		reply.setTitle("Future deleted forum part. 2");
		reply.setBody("Future deleted  stuff");
		forumManager.replyToMessage(reply, topMessage);
		dbInstance.commit();
		
		Message reply2 = forumManager.createMessage(fo, id1, false);
		reply2.setTitle("Future deleted forum part. 3");
		reply2.setBody("Future deleted  stuff");
		forumManager.replyToMessage(reply2, reply);
		dbInstance.commitAndCloseSession();

		//delete the forum
		forumManager.deleteForum(fo.getKey());
		dbInstance.commit();
	}
}
