package com.familyFirstSoftware.SecureDocAIBackend.event.listener;

import com.familyFirstSoftware.SecureDocAIBackend.event.UserEvent;
import com.familyFirstSoftware.SecureDocAIBackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/11/2024
 *
 * This is called indirectly from the UserEvent class using the @EventListener annotation
 */

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @EventListener
    public void onUserCreated(UserEvent event) {
        switch(event.getEventType()){
            case REGISTRATION:
                emailService.sendNewAccountEmail(event.getUser().getFirstName(), event.getUser().getEmail(), event.getData().get("key").toString());
                break;
            case PASSWORD_RESET:
                emailService.sendPasswordResetEmail(event.getUser().getFirstName(), event.getUser().getEmail(), event.getData().get("key").toString());
                break;
            default:

        }
    }

}

