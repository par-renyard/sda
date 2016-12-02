/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.staples.sda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.AbstractMessage;
import com.staples.sda.dialog.message.MessageContext;
import com.staples.sda.dialog.message.MessageProcessor;
import com.staples.sda.dialog.message.StandardMessage;
import com.staples.sda.statemachine.StateMachineConfig;

@Configuration
@ComponentScan
@PropertySource("dialog.properties")
public class SDAConsoleClient implements CommandLineRunner {
	
	@Autowired
	private MessageProcessor messageProcessor;
	
	private static ApplicationContext context;
	
	private static Logger log = LoggerFactory.getLogger(SDAConsoleClient.class);

	@Override
	public void run(String... args) throws IOException {
		InputStream in = System.in;
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
		String input = null;
		String conversationId = "A1";
		messageProcessor.initialize(conversationId);
		while ((input = rdr.readLine()) != null) {
			System.out.println("[USER @" + conversationId + "] " + input);
			StandardMessage message = StandardMessage.builder().inbound(input).conversationId(conversationId).build();
			messageProcessor.handleMessage(message);
		}
	}

	public static void main(String[] args) throws Exception {
		log.info("Starting SDAConsoleClient");
		context = new AnnotationConfigApplicationContext(StateMachineConfig.class, SDAConsoleClient.class);
		log.info("Spring context created. Running dialog.");
		SDAConsoleClient client = context.getBean(SDAConsoleClient.class);
		client.run(args);
	}
	
	@Bean
	public OutputChannel outputChannel() {
		return new OutputChannel() {
			
			@Override
			public void outputMessage(AbstractMessage message) {
				System.out.println("[SDA @" + message.getConversationId() + "] " + message.getRaw());
			}

			@Override
			public void transferToAgent(MessageContext lastMessage) {
				System.out.println("[SDA @" + lastMessage.getMessage().getConversationId() + "] ** AGENT TRANSFER INVOKED **");
				System.exit(0);
			}

			@Override
			public void terminate(MessageContext lastMessage) {
				System.out.println("[SDA @" + lastMessage.getMessage().getConversationId() + "] ** TERMINATE INVOKED **");
				System.exit(0);
			}
		};
	}
	/*
	@Bean
	public static CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		Map<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("session", new SimpleThreadScope());
		configurer.setScopes(scopes);
		return configurer;
	}
	*/
	@Bean
	public static MessageSource messageSource() {
		ResourceBundleMessageSource ret = new ResourceBundleMessageSource();
		ret.setBasename("messages");
		return ret;
	}
}
