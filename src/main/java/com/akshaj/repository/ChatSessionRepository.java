package com.akshaj.repository;

import com.akshaj.model.ChatSession;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSessionRepository {
    private  static ChatSessionRepository instance=null;
    private final ConcurrentHashMap<String, ChatSession> session;

    public static ChatSessionRepository getInstance(){
        if(instance==null){
            instance= new ChatSessionRepository(); //this calls the constructor once
        }
        return instance;
    }
    private ChatSessionRepository(){
        session=new ConcurrentHashMap<>();
    }

    public void addSession(String userId, ChatSession chatSession){
        session.putIfAbsent(userId,chatSession);
    }
    public ChatSession getSession(String userId){
        return session.get(userId);
    }
    public int getNoOfSessions(){
        return session.size();
    }
}
