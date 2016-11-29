package com.staples.sda.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.SimpleThreadScope;

import com.staples.sda.dialog.channel.OutputChannel;
import com.staples.sda.dialog.message.AbstractMessage;

@Configuration
@ComponentScan("com.staples.sda")
public class SpringTestConfiguration {

	@Bean
	public static CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer configurer = new CustomScopeConfigurer();
		Map<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("session", new SimpleThreadScope());
		configurer.setScopes(scopes);
		return configurer;
	}
	
	@Bean
	public static MessageSource messageSource() {
		ResourceBundleMessageSource ret = new ResourceBundleMessageSource();
		ret.setBasename("messages");
		return ret;
	}
	
	@Bean
	public static OutputChannel outputChannel() {
		return new OutputChannel() {			
			@Override
			public void outputMessage(AbstractMessage message) {
				System.out.println(message.getRaw());
			}
		};
	}
}
