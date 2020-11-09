package com.dgex.backend.service;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.PushInfo;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.PushInfoRepository;
import com.dgex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PushInfoService {

    private final UserRepository userRepository;
    private final PushInfoRepository pushInfoRepository;

    @Transactional
    public Map<String,Object> sendPush(Integer userId, String title, String content) {
        Map<String,Object> result = new HashMap<>();

        User user = userRepository.findById(userId).get();

        if(user != null){
            try{
                HttpResponse response = null;
                String fcmKey = "AAAAa05wdas:APA91bFl_7_5c54Iunq64ySL7Pc45fuqgYZPojJE1gJwhzkPzWc9g5ahyOGAyJ8DaBM-z2OD1XEz2GUmQOeagUAZZfX32HX8KBNCPKj8VPM21qiv2Gic1g_8DgILzmQ6KZ0SWYkUJ1rN";
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", "key="+fcmKey);

                JSONObject message = new JSONObject();

//                List<String> tokenList = new ArrayList<String>();
//                tokenList.add(user.getDeviceToken());

                message.put("to", user.getDeviceToken());

                JSONObject notification = new JSONObject();
                notification.put("title",title);
                notification.put("body",content);

                message.put("notification", notification);

                post.setEntity(new StringEntity(message.toString(), "UTF-8"));
                response = client.execute(post);

                PushInfo pushInfo = new PushInfo();
                pushInfo.setContents(content);
                pushInfo.setTitle(title);
                pushInfo.setUser(user);
                pushInfo.setReadYn("N");
                pushInfo.setCreateDatetime(new Date());
                pushInfoRepository.save(pushInfo);

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            result.put("msg", "푸시 전송 실패");
            result.put("result", false);
        }

        return result;
    }

    @Transactional
    public Map<String,Object> sendAllPush(List<User> user, String title, String content) {
        Map<String,Object> result = new HashMap<>();

        ArrayList registerId = new ArrayList<String>();

        for(int i = 0; i < user.size(); i++){
            registerId.add(user.get(i).getDeviceToken());
        }

        if(user != null){
            try{
                HttpResponse response = null;
                String fcmKey = "AAAAa05wdas:APA91bFl_7_5c54Iunq64ySL7Pc45fuqgYZPojJE1gJwhzkPzWc9g5ahyOGAyJ8DaBM-z2OD1XEz2GUmQOeagUAZZfX32HX8KBNCPKj8VPM21qiv2Gic1g_8DgILzmQ6KZ0SWYkUJ1rN";
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", "key="+fcmKey);

                JSONObject message = new JSONObject();

//                List<String> tokenList = new ArrayList<String>();
//                tokenList.add(user.getDeviceToken());

                message.put("registration_ids",registerId);

                JSONObject notification = new JSONObject();
                notification.put("title",title);
                notification.put("body",content);

                message.put("notification", notification);

                post.setEntity(new StringEntity(message.toString(), "UTF-8"));
                response = client.execute(post);

                for(int i = 0; i < user.size(); i++){
                    PushInfo pushInfo = new PushInfo();
                    pushInfo.setContents(content);
                    pushInfo.setTitle(title);
                    pushInfo.setUser(user.get(i));
                    pushInfo.setReadYn("N");
                    pushInfo.setCreateDatetime(new Date());
                    pushInfoRepository.save(pushInfo);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            result.put("msg", "푸시 전송 실패");
            result.put("result", false);
        }

        return result;
    }



    @Transactional
    public Object userPushList(Integer userId) {
        User user = userRepository.findById(userId).get();
        List<PushInfo> pushInfoList = pushInfoRepository.findByDeleteDatetimeIsNullAndUser(user);


        Map<String, Object> result = new HashMap<>();

        result.put("pushList", pushInfoList);

        return result;
    }

    @Transactional
    public void updatePushRead(Integer pushInfoId){
        PushInfo pushInfo = pushInfoRepository.findById(pushInfoId).get();

        pushInfo.setReadYn("Y");
        pushInfoRepository.save(pushInfo);
    }

}
