package com.hoangvo.chatappsocial.fcm.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/fcm")
public class FirebasePublisherController {

    private final FirebaseMessaging fcm;

}