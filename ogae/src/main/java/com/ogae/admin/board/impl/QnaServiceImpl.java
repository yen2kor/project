package com.ogae.admin.board.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogae.admin.board.Pagination;
import com.ogae.admin.board.QnaDAO;
import com.ogae.admin.board.QnaVO;
import com.ogae.admin.board.service.QnaService;

@Service("QnaService")
public class QnaServiceImpl implements QnaService{

    @Autowired
    private QnaDAO qnaDAO;

    @Override
    @Transactional
    public void insertQna(QnaVO vo) {
    	System.out.println("===> QnaServiceImpl insertQna");
        qnaDAO.insertQna(vo);
    }
    


    @Override
    @Transactional
    public void updateQna(QnaVO vo) {

        QnaVO existingQna = qnaDAO.getQna(vo);

        if (existingQna != null) {

            vo.setQnaIdx(existingQna.getQnaIdx());
            vo.setQnaRegDate(existingQna.getQnaRegDate());

            qnaDAO.updateQna(vo);
        } else {

            System.out.println("게시글이 존재하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteQna(QnaVO vo) {
    	System.out.println("1");
        qnaDAO.deleteQna(vo);
    }

    @Override
    public QnaVO getQna(QnaVO vo) {
        return qnaDAO.getQna(vo);
    }
  

    //페이징
	@Override
	public List<QnaVO> getQnaList(QnaVO vo) {
		  return qnaDAO.getQnaList(vo);
	}

	@Override
	public List<QnaVO> getQnaList(Pagination pagination) {
		return qnaDAO.getQnaList(pagination);
	}

	@Override
	public int getQnaListCnt() {
		  return qnaDAO.getQnaListCnt();
	}
	//검색
	@Override
	public List<QnaVO> getSearchQnaList(String searchCondition, String searchKeyword, Pagination pagination) {
		  return qnaDAO.getSearchQnaList(searchCondition, searchKeyword, pagination);
	}

	@Override
	public int getSearchQnaListCnt(String searchCondition, String searchKeyword) {
		  return qnaDAO.getSearchQnaListCnt(searchCondition, searchKeyword);
	  
	}
	//이전글 다음글
	@Override
	public QnaVO prevNext(QnaVO vo) {
		return qnaDAO.prevNext(vo);
	}


	//댓글
	@Override
	@Transactional
	public void insertQnaReply(QnaVO vo) {
		qnaDAO.insertQnaReply(vo);;
		
		
	}



	@Override
	@Transactional
	public void deleteQnaReply(QnaVO vo) {
		 qnaDAO.deleteQnaReply(vo);
		
	}


	@Override
    public String getQnaReply(QnaVO vo) {
        QnaVO qna = qnaDAO.getQna(vo);
        if (qna != null && qna.isHasReply()) {
            return qna.getQnaReply();
        } else {
            return null;
        }
	}
}

