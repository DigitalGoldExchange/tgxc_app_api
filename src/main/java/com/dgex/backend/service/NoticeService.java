package com.dgex.backend.service;

import com.dgex.backend.entity.NoticeEng;
import com.dgex.backend.entity.NoticeKr;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.NoticeEngRepository;
import com.dgex.backend.repository.NoticeKrRepository;
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

    @Transactional
    public void insert(String title, String contents, String koreanYn){

        if("Y".equals(koreanYn)){
            NoticeKr noticeKr = new NoticeKr();
            noticeKr.setTitle(title);
            noticeKr.setContents(contents);
            noticeKr.setCreateDatetime(new Date());
            noticeKr.setDispYn("N");
            noticeKrRepository.save(noticeKr);
        }else{
            NoticeEng noticeEng = new NoticeEng();
            noticeEng.setContents(contents);
            noticeEng.setTitle(title);
            noticeEng.setCreateDatetime(new Date());
            noticeEng.setDispYn("N");
            noticeEngRepository.save(noticeEng);
        }

    }

    @Transactional
    public Object getList(Integer page, String searchWord, String koreanYn) {
        Map<String, Object> result = new HashMap<>();
        List<NoticeKr> krList;
        List<NoticeEng> engList;
        Integer totalPages;

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");

        if("Y".equals(koreanYn)|| koreanYn == null){
            Page<NoticeKr> pageList = noticeKrRepository.findByDeleteDatetimeIsNull(PageRequest.of(page-1, 10, sort));
            krList = pageList.toList();
            totalPages = pageList.getTotalPages();
            result.put("list",krList);
            result.put("totalPages",totalPages);

        }else{
            Page<NoticeEng> pageList = noticeEngRepository.findByDeleteDatetimeIsNull(PageRequest.of(page-1, 10, sort));
            engList = pageList.toList();
            totalPages = pageList.getTotalPages();
            result.put("list",engList);
            result.put("totalPages",totalPages);
        }

        return result;
    }

    @Transactional
    public void update(Integer noticeId, String status, String koreanYn) {
       if("Y".equals(koreanYn)){
           NoticeKr noticeKr = noticeKrRepository.findById(noticeId).get();
           noticeKr.setDispYn(status);
           noticeKr.setUpdateDatetime(new Date());
           noticeKrRepository.save(noticeKr);
       }else{
           NoticeEng noticeEng = noticeEngRepository.findById(noticeId).get();
           noticeEng.setDispYn(status);
           noticeEng.setUpdateDatetime(new Date());
           noticeEngRepository.save(noticeEng);
       }
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
