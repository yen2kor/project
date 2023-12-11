package com.ogae.admin.board.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ogae.admin.board.FacilityDAO;
import com.ogae.admin.board.FacilityImgVO;
import com.ogae.admin.board.FacilityVO;
import com.ogae.admin.board.service.FacilityService;


@Service("FacilityService")
public class FacilityServiceImpl implements FacilityService{

    @Autowired
    private FacilityDAO FacilityDAO;

    @Override
    @Transactional
    public void insertFacility(FacilityVO vo) {
    	System.out.println("===> FacilityServiceImpl insertFacility");
        FacilityDAO.insertFacility(vo);
        
    }
   
    @Override
    @Transactional
    public void updateFacility(FacilityVO vo) {
        // 기존의 게시글을 불러옵니다.
        FacilityVO existingFacility = FacilityDAO.getFac(vo);
        System.out.println("===> FacilityServiceImpl updateFacility");


        if (existingFacility != null) {

            vo.setFacility_idx(existingFacility.getFacility_idx());

            FacilityDAO.updateFacility(vo);
        } else {
            System.out.println("게시글이 존재하지 않습니다.");
        }
    }

    
    @Override
    @Transactional
    public void deleteFacility(FacilityVO vo) {
        FacilityDAO.deleteFacility(vo);
    }

    @Override
    public FacilityVO getFac(FacilityVO vo) {
        return FacilityDAO.getFac(vo);
    }
    
  @Override
  public List<FacilityVO> getFacList(FacilityVO vo) {
      return FacilityDAO.getFacList(vo);
  }

       
    
  }


