package com.example.eventstream.delivery.service;

import org.springframework.stereotype.Service;

@Service
public class DeliveryAssignmentService {
    private static final String[] PARTNERS = {
            "Rahul Sharma",
            "Amit Patel",
            "Neha Singh",
            "Rohit Verma",
            "Priya Mehta"
    };
    public String assignPartner() {
        int index = (int) (Math.random() * PARTNERS.length);
        return PARTNERS[index];
    }
}