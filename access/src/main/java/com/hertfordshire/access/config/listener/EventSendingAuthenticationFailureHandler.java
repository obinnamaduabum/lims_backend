package com.hertfordshire.access.config.listener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class EventSendingAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler
        implements ApplicationEventPublisherAware {

    protected ApplicationEventPublisher eventPublisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onAuthenticationFailure(javax.servlet.http.HttpServletRequest request,
                                        javax.servlet.http.HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException,
            javax.servlet.ServletException {
        // use eventPublisher to publish the event according to exception
        super.onAuthenticationFailure(request, response, exception);
    }
}