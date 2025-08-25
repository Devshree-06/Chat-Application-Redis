package com.ChatAppllication.service;

import com.ChatAppllication.model.Response.ChatRoomRes;
import com.ChatAppllication.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@Service
public class JoinChatRoomService {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    public Mono<ChatRoomRes> joinChatRoom(String roomId,String participant){
        if(participant==null || participant.isEmpty()){
            return Mono.just(new ChatRoomRes("fail",null,"Participant name cannot be empty"));
        }

        return chatRoomRepository.findById(roomId)
                .flatMap(results->{
                    if(results.getParticipants()==null){
                        results.setParticipants(new HashSet<>());
                    }

                    results.getParticipants().add(participant);

                    return chatRoomRepository.saveChatRoom(results)
                            .map(updatedChatRoom ->{
                                ChatRoomRes res = new ChatRoomRes();
                                res.setRoomId(updatedChatRoom.getId());
                                res.setStatus("Success");
                                res.setMessage("User '" + participant + "' joined chat room '" + updatedChatRoom.getChatRoomName() + "'.");
                                return res;
                            });
                })
                .switchIfEmpty(Mono.just(new ChatRoomRes("Fail", null, "Chat Room Not Found")));
    }
}
