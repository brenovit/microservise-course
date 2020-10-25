package io.github.brenovit.courseservice.infraestructure;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HeaderHelper {

	@Autowired
	private HttpServletRequest request;
		
	public String getUserId() {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isEmpty(header)) {
            throw new RuntimeException("No User Code found in request headers");
        }

        return header;
	}

}
