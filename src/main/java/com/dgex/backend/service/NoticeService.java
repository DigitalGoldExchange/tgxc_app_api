package com.dgex.backend.service;

import com.dgex.backend.entity.Notice;
import com.dgex.backend.entity.NoticeEng;
import com.dgex.backend.entity.NoticeKr;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.NoticeEngRepository;
import com.dgex.backend.repository.NoticeKrRepository;
import com.dgex.backend.repository.NoticeRepository;
import com.dgex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeKrRepository noticeKrRepository;
    private final NoticeEngRepository noticeEngRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final PushInfoService pushInfoService;

    @Transactional
    public void insert(String title, String contents, String koreanYn){
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContents(contents);
        notice.setCreateDatetime(new Date());
        notice.setDispYn("N");
        if("Y".equals(koreanYn) || koreanYn == null){
            notice.setType("KO");
        }else{
            notice.setType("EN");
        }

        noticeRepository.save(notice);

        List<User> koreanUser = userRepository.findByDeleteDatetimeIsNullAndKoreanYnIsAndPushTypeIs("Y","A");
        List<User> foreignerUser = userRepository.findByDeleteDatetimeIsNullAndKoreanYnIsAndPushTypeIs("N","A");

        if("Y".equals(koreanYn) || koreanYn == null){
            pushInfoService.sendAllPush(koreanUser, title, contents);
        }else{
            pushInfoService.sendAllPush(foreignerUser, title, contents);
        }



    }

    @Transactional
    public Object getList(Integer page, String searchWord, String koreanYn) {
        Map<String, Object> result = new HashMap<>();
        List<Notice> list;
        Integer totalPages;

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");

        if("Y".equals(koreanYn)|| koreanYn == null){
            Page<Notice> pageList = noticeRepository.findByTitleAndTypeAndDeleteDatetimeIsNull("%"+searchWord+"%", "KO",PageRequest.of(page-1, 10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
            result.put("list",list);
            result.put("totalPages",totalPages);

        }else{
            Page<Notice> pageList = noticeRepository.findByTitleAndTypeAndDeleteDatetimeIsNull("%"+searchWord+"%" ,"EN", PageRequest.of(page-1, 10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
            result.put("list",list);
            result.put("totalPages",totalPages);
        }

        return result;
    }

    @Transactional
    public void update(Integer noticeId, String status, String koreanYn) {
        Notice notice = noticeRepository.findById(noticeId).get();
        notice.setDispYn(status);
        if("D".equals(status)){
            notice.setDeleteDatetime(new Date());
        }
        notice.setUpdateDatetime(new Date());
        noticeRepository.save(notice);

    }

    @Transactional
    public void updateContent(Integer noticeId, String title, String contents, String koreanYn) {
        Notice notice = noticeRepository.findById(noticeId).get();
        notice.setTitle(title);
        notice.setContents(contents);
        if("Y".equals(koreanYn)){
            notice.setType("KO");
        }else{
            notice.setType("EN");
        }
        notice.setUpdateDatetime(new Date());
        noticeRepository.save(notice);

    }

    @Transactional
    public void delete(Integer noticeId, String koreanYn) {
        if("Y".equals(koreanYn)){
            NoticeKr noticeKr = noticeKrRepository.findById(noticeId).get();
            noticeKr.setDeleteDatetime(new Date());
            noticeKrRepository.save(noticeKr);
        }else{
            NoticeEng noticeEng = noticeEngRepository.findById(noticeId).get();
            noticeEng.setDeleteDatetime(new Date());
            noticeEngRepository.save(noticeEng);
        }
    }

}
