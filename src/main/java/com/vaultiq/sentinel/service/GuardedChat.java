package com.vaultiq.sentinel.service;

import com.vaultiq.sentinel.dto.GuardedChatResponse;

public interface GuardedChat {

    GuardedChatResponse chat(String question);
}
