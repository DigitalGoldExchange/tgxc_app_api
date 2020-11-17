package com.dgex.backend.service;

import com.dgex.backend.entity.ServerManage;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.ServerManageRepository;
import com.dgex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Server;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ServerManageService {

    private final ServerManageRepository serverManageRepository;
    private final UserRepository userRepository;

    @Transactional
    public void update(Integer userId, String status){

        ServerManage serverManage = serverManageRepository.findByDeleteDatetimeIsNull();
        User user = userRepository.findById(userId).get();

        if(serverManage != null){
            serverManage.setDeleteDatetime(new Date());
            serverManage.setUpdateDatetime(new Date());
            serverManageRepository.save(serverManage);
        }

        ServerManage sm = new ServerManage();
        sm.setCreateDatetime(new Date());
        sm.setStatus(status);
        sm.setUser(user);
        serverManageRepository.save(sm);

    }

}
