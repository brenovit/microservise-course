package io.github.brenovit.courseservice.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserApi {
	
	private RestTemplate restTemplate;
	private UserProperties propertie;
	
	public User findUser(String userId) {
		
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(propertie.getHost())
				.path("/users");
		builder.pathSegment("{id}");
		Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("id", userId);
		
		builder.uriVariables(uriVariables);
		
		ResponseEntity<User> responseEntity = restTemplate.exchange(builder.toUriString(), 
				HttpMethod.GET,  
				HttpEntity.EMPTY, 
				User.class);
		
		
		return responseEntity.getBody();
	}
}
