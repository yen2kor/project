package com.ogae.user.board.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogae.admin.board.RoomImgVO;
import com.ogae.admin.board.RoomVO;
import com.ogae.common.Utility;
import com.ogae.service.GuestService;
import com.ogae.service.PaymentService;
import com.ogae.service.ReserveService;
import com.ogae.vo.GuestVO;
import com.ogae.vo.PaymentVO;
import com.ogae.vo.ReserveVO;
import com.ogae.vo.TempDTO;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Slf4j
@Controller
public class UserReserveController {

	@Autowired
	private ReserveService reserService;
	@Autowired
	private GuestService guestService;
	@Autowired
	private PaymentService payService;

	
	@GetMapping("/reserveMain.do")
	public String reserMainView() {
		return "reserve/reserveMain";
	}
	
	@GetMapping("/reserCalender.do")
	public String reserCalendar() {
		return "reserve/reserCalendar";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/getReserList.do", produces = "application/json; charset=utf-8")
	public JSONObject getReserList(@RequestParam Map<String, String> param) throws IOException, java.text.ParseException {
	
		log.debug("year: {}, month: {}", param.get("year"), param.get("month"));
		int year = Integer.valueOf(param.get("year"));
		int month = Integer.valueOf(param.get("month"));
		
		List<RoomVO> roomList = reserService.getUseRoomList();
		log.debug("------------------ roomList: {}", roomList);
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 1);
		Date start = cal.getTime();
		int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month-1, dayOfMonth);
		Date end = cal.getTime();
		
		Map<String, Date> sendParam = new HashMap<String, Date>();
		
		log.debug("startDate: {} endDate: {}", start, end);
		sendParam.put("start", start);
		sendParam.put("end", end);
		
		List<ReserveVO> list = reserService.getReserList(sendParam);		
		
		JSONObject jsonObject = new JSONObject();
		JSONArray roomArray = new JSONArray();
		JSONArray listArray = new JSONArray();

		for(int i=0; i<roomList.size(); i++) {
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("room_idx", roomList.get(i).getRoom_idx());
			jsonTemp.put("room_name", roomList.get(i).getRoom_name());
			jsonTemp.put("room_price", roomList.get(i).getRoom_price());
			roomArray.add(jsonTemp);
		}		

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0; i<list.size(); i++) {
			String reserStart = dateFormat.format(list.get(i).getReserve_start());
			String reserEnd = dateFormat.format(list.get(i).getReserve_end());
			
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("room_idx", list.get(i).getRoom_idx());
			jsonTemp.put("reserve_start", reserStart);
			jsonTemp.put("reserve_end", reserEnd);
			jsonTemp.put("reserve_state", list.get(i).getReserve_state());
			listArray.add(jsonTemp);
		}		
		
		jsonObject.put("list", listArray);
		jsonObject.put("room", roomArray);
		
		log.debug("-----------------2 {}", jsonObject.toString());
		
		return jsonObject;
	}
	
	@GetMapping("/reserApply.do")
	public String reserApply() {
		return "reserve/reserApply";
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/getReserRoom.do", produces = "application/json; charset=utf-8")
	public JSONObject getReserRoom(@RequestParam Map<String, String> param) {
		
		log.debug("{}", param);
		
		JSONObject jsonObject = new JSONObject();
		JSONArray roomArray = new JSONArray();
		JSONArray listArray = new JSONArray();
		
		String[] dateArr = param.get("start").split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(dateArr[0]), Integer.valueOf(dateArr[1]) - 1, Integer.valueOf(dateArr[2]));
		Date startDate = cal.getTime();
		dateArr = param.get("end").split("-");
		cal.set(Integer.valueOf(dateArr[0]), Integer.valueOf(dateArr[1]) - 1, Integer.valueOf(dateArr[2]));
		Date endDate = cal.getTime();
		Map<String, Date> sendParam = new HashMap<String, Date>();

		log.debug("startDate: {} endDate: {}", startDate, endDate);
		sendParam.put("start", startDate);
		sendParam.put("end", endDate);
		
		List<RoomVO> roomList = reserService.getUseRoomList();
		List<ReserveVO> list = reserService.getReserList(sendParam);
		List<RoomImgVO> roomImg = reserService.getImageList();
		

		String imgServer = "https://***.amazonaws.com/";
		for(int i=0; i<roomList.size(); i++) {
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("room_idx", roomList.get(i).getRoom_idx());
			jsonTemp.put("room_name", roomList.get(i).getRoom_name());
			jsonTemp.put("room_info", roomList.get(i).getRoom_info());
			jsonTemp.put("room_person", roomList.get(i).getPerson_num());
			jsonTemp.put("room_category", roomList.get(i).getRoom_category());
			jsonTemp.put("room_size", roomList.get(i).getRoom_size());
			jsonTemp.put("room_price", roomList.get(i).getRoom_price());
			for(int j=0; j<roomImg.size(); j++) {
				if(roomList.get(i).getRoom_idx() == roomImg.get(j).getRoom_idx()) {
					jsonTemp.put("room_img", imgServer + roomImg.get(j).getImg_path());
					break;
				}
			}
			roomArray.add(jsonTemp);
		}

		for(int i=0; i<list.size(); i++) {
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("room_idx", list.get(i).getRoom_idx());
			jsonTemp.put("reserve_start", list.get(i).getReserve_start().toString());
			jsonTemp.put("reserve_end", list.get(i).getReserve_end().toString());
			jsonTemp.put("reserve_state", list.get(i).getReserve_state());
			listArray.add(jsonTemp);
		}		

		jsonObject.put("list", listArray);
		jsonObject.put("room", roomArray);
		
		log.debug("-----------------2 {}", jsonObject.toString());

		return jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/reserApply.do", produces = "application/json; charset=utf-8")
	public JSONObject reserApply(String param) throws ParseException, JsonProcessingException {
		System.err.println("-----------------------------------------------");
		log.debug("{}", param);

		JSONObject jsonObject = new JSONObject();
		JSONArray roomArray = new JSONArray();
		JSONArray listArray = new JSONArray();
		
		if(param != null) {
		
			String[] paramTemp = param.split("&");
			String[] arrTemp = paramTemp[0].split("=");
			String selDate = arrTemp[1];
			//arrTemp = paramTemp[1].split("=");
			
			String[] dateArr = selDate.split("-");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.valueOf(dateArr[0]), Integer.valueOf(dateArr[1]) - 1, Integer.valueOf(dateArr[2]));
			Date startDate = cal.getTime();
			cal.set(Integer.valueOf(dateArr[0]), Integer.valueOf(dateArr[1]) - 1, Integer.valueOf(dateArr[2]) + 1);
			Date endDate = cal.getTime();
			Map<String, Date> sendParam = new HashMap<String, Date>();
			
			log.debug("startDate: {} endDate: {}", startDate, endDate);
			sendParam.put("start", startDate);
			sendParam.put("end", endDate);
			
			// 231127 중간 날짜 계산하기 숙박기간이 2일 이상인 경우 사이의 방도 예약 금지
			
			List<RoomVO> roomList = reserService.getUseRoomList();
			List<ReserveVO> list = reserService.getReserList(sendParam);
			List<RoomImgVO> roomImg = reserService.getImageList();
			
			log.debug("roomList: {}", roomList);
			log.debug("ReserList: {}", list);


			String imgServer = "https://***.amazonaws.com/";
			for(int i=0; i<roomList.size(); i++) {
				JSONObject jsonTemp = new JSONObject();
				jsonTemp.put("room_idx", roomList.get(i).getRoom_idx());
				jsonTemp.put("room_name", roomList.get(i).getRoom_name());
				jsonTemp.put("room_info", roomList.get(i).getRoom_info());
				jsonTemp.put("room_person", roomList.get(i).getPerson_num());
				jsonTemp.put("room_category", roomList.get(i).getRoom_category());
				jsonTemp.put("room_size", roomList.get(i).getRoom_size());
				jsonTemp.put("room_price", roomList.get(i).getRoom_price());
				for(int j=0; j<roomImg.size(); j++) {
					if(roomList.get(i).getRoom_idx() == roomImg.get(j).getRoom_idx()) {
						jsonTemp.put("room_img", imgServer + roomImg.get(j).getImg_path());
						break;
					}
				}
				roomArray.add(jsonTemp);
			}
	
			for(int i=0; i<list.size(); i++) {
				JSONObject jsonTemp = new JSONObject();
				jsonTemp.put("room_idx", list.get(i).getRoom_idx());
				jsonTemp.put("reserve_start", list.get(i).getReserve_start().toString());
				jsonTemp.put("reserve_end", list.get(i).getReserve_end().toString());
				jsonTemp.put("reserve_state", list.get(i).getReserve_state());
				listArray.add(jsonTemp);
			}		
	
			jsonObject.put("list", listArray);
			jsonObject.put("room", roomArray);
			
			log.debug("-----------------2 {}", jsonObject.toString());
	
			return jsonObject;
		}

		return jsonObject;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getRoomImages.do", produces = "application/json; charset=utf-8")
	public String getRoomImages(@RequestParam("idx") int idx) throws Exception {
		
		log.debug("{}", idx);
		RoomImgVO imgVO = new RoomImgVO();

		System.out.println(imgVO);
		
		List<RoomImgVO> roomImg = reserService.getImages(imgVO);
		
		System.out.println(roomImg);
		
		ObjectMapper mapper = new ObjectMapper(); 
		String jsonString = mapper.writeValueAsString(roomImg);
		
		return jsonString;
	}
	
	@GetMapping("/reserTabMenu.do")
	public String calendar() {
		return "reserve/reserTabMenu";
	}
	
	@GetMapping("/reserCheck.do")
	public String reserCheck() {
		return "reserve/reserCheck";
	}

	@ResponseBody
	@RequestMapping(value = "/reserPaydata.do", produces = "application/json; charset=utf-8")
	public String reserPaydata(@RequestBody TempDTO vo, HttpServletRequest request) throws Exception {
		log.debug("{}", vo.getTempList());

		String reserNum = Utility.getInstance().createFileName(0);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(vo.getTempList());
		System.out.println(jsonString);
		
		ServletContext servletContext = request.getSession().getServletContext();
		System.out.println(servletContext);
		String realPath = servletContext.getRealPath("/");
		String path = realPath + reserNum + ".json";
		System.out.println(path);
		
		FileWriter file = new FileWriter(path);
		file.write(jsonString);
		file.flush();
		file.close();
		
		return reserNum;
	}
	
	@GetMapping("/reserPayment.do")
	public String reserPayment() {
		log.debug("{}");
		return "reserve/reserPayment";
	}
	
	@ResponseBody
	@RequestMapping(value = "/getJson.do", produces = "application/json; charset=utf-8")
	public String getJson(@RequestParam String code, HttpServletRequest request) {
		
		log.debug("code: {}", code);
		ServletContext servletContext = request.getSession().getServletContext();
		System.out.println(servletContext);
		String realPath = servletContext.getRealPath("/");
		String path = realPath + code + ".json";
		System.out.println(path);
		JSONArray jsonArray = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(path)) {
			jsonArray = (JSONArray) jsonParser.parse(reader);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println(jsonArray.toString());
		
		return jsonArray.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/completePayment.do", produces = "application/json; charset=utf-8", method= {RequestMethod.POST, RequestMethod.GET})
	public String completePayment(@RequestBody Map<String, Object> param, GuestVO gvo ,PaymentVO pvo, ReserveVO rvo) throws java.text.ParseException {
		
		log.debug("{}", param);
		
		// guest insert
		String guest_idx = (String) param.get("guest_idx");
		String guest_name = (String) param.get("guest_name");
		String guest_birth = (String) param.get("guest_birth");
		String guest_phone = (String) param.get("guest_phone");
		String guest_ephone = (String) param.get("guest_ephone");
		String guest_email = (String) param.get("guest_email");
		String guest_time = (String) param.get("guest_time");
		String guest_request = (String) param.get("guest_request");
		
		gvo.setGuest_idx(guest_idx);
		gvo.setGuest_name(guest_name);
		gvo.setGuest_birth(guest_birth);
		gvo.setGuest_phone(guest_phone);
		gvo.setGuest_emerge_phone(guest_ephone);
		gvo.setGuest_email(guest_email);
		gvo.setGuest_arrive_time(guest_time);
		gvo.setGuest_request(guest_request);
		
		log.debug("{}", gvo);
//		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// reserve insert
		String reserve_idx = (String) param.get("reser_idx");
		Date reserve_start = dateFormat.parse((String) param.get("reser_start"));
		Date reserve_end = dateFormat.parse((String) param.get("reser_end"));
		String reserve_state = (String) param.get("reser_state");
		int room_idx =  Integer.parseInt((String) param.get("reser_room"));
		
		rvo.setReserve_idx(reserve_idx); 
		rvo.setReserve_start(reserve_start); 
		rvo.setReserve_end(reserve_end); 
		rvo.setReserve_state(reserve_state); 
		rvo.setRoom_idx(room_idx); 
		rvo.setGuest_idx(guest_idx);
		
		log.debug("{}", rvo);

		
		// payment insert
		String payment_idx = (String) param.get("merchant_uid");
		String payment_type = (String) param.get("pay_type");
		String payment_status = "1";
		String payment_bank = "은행";
		String payment_approval = (String) param.get("pay_apply");
		String payment_date = new Date().toString();
		int payment_total =Integer.parseInt((String) param.get("pay_total"));
		
		pvo.setPayment_idx(payment_idx);
		pvo.setPayment_type(payment_type);
		pvo.setPayment_status(payment_status);
		pvo.setPayment_bank(payment_bank);
		pvo.setPayment_approval(payment_approval);
		pvo.setPayment_date(payment_date);
		pvo.setPayment_total(payment_total);
		pvo.setReserve_idx(reserve_idx);
		
		log.debug("{}", pvo);

		String room_name = (String) param.get("reser_room_name");
		int room_price = Integer.parseInt((String) param.get("reser_room_price"));
		String reser_peoInfo = (String) param.get("reser_peoInfo");
		String[] peoArr = reser_peoInfo.split("/");
		int reser_adult = Integer.parseInt(peoArr[0]);
		int reser_child = Integer.parseInt(peoArr[1]);
		int reser_baby = Integer.parseInt(peoArr[2]);
		

		guestService.insertGuest(gvo);
		reserService.insertReserve(rvo);
		payService.insertPayment(pvo);
		
		
		String peoInfo = (reser_adult > 0 ? "성인" + reser_adult : "") 
						+ (reser_child > 0 ? "/유아" + reser_child : "")
						+ (reser_baby > 0 ? "/아동" + reser_baby : "");
		long diffDays = ((reserve_end.getTime() - reserve_start.getTime()) / 1000) / (24*60*60);

		sendSmsReserve(gvo.getGuest_phone(), rvo.getReserve_idx(), room_name, peoInfo, dateFormat.format(reserve_start) + "(" + diffDays + "박)");

		
		return "success";
	}
	
	@GetMapping("/reserComplete.do")
	public ModelAndView reserComplete() {

		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("reserve/reserComplete");
		return mav;
	}

	
	public SingleMessageSentResponse sendSmsReserve(String phone, String reserNo, String roomInfo, String peoInfo, String reserDate) {
		log.debug("phone:{} reserNo:{}", phone, reserNo);
		
		String api_key = "***";
		String api_secret = "***";

		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(api_key, api_secret, "https://api.coolsms.co.kr");
		String strMsg = String.format("예약완료 여기오개 펜션/%s/%s/%s\n예약번호:%s", roomInfo, reserDate, peoInfo, reserNo);
		System.out.println(strMsg);
		
		
		Message message = new Message();
		message.setFrom("01053242514");
		message.setTo(phone);
		message.setText(strMsg);
		
		SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
		System.out.println(response);
		
		return response;
	}
	
	@RequestMapping(value = "/getReserve.do", method = {RequestMethod.POST, RequestMethod.GET})
	public String getReserve(@RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("reserveno") String reserveNo,
							@RequestParam("peoInfo") String peoInfo, Model model) {
		
		log.debug("{} {} {} {}", name, phone, reserveNo, peoInfo);
		Map<String, String> sendParam = new HashMap<String, String>();
		
		sendParam.put("guest_name", name);
		sendParam.put("guest_phone", phone);
		sendParam.put("reserve_idx", reserveNo);
		
		Map<String, String> result = reserService.getReserve(sendParam);

		log.debug("{}", result);
		
		model.addAttribute("reserno", result.get("reserve_idx"));
		model.addAttribute("name", result.get("guest_name"));
		model.addAttribute("phone", result.get("guest_phone"));
		if(result.get("guest_emerge_phone").length() > 5)
			model.addAttribute("ephone", result.get("guest_emerge_phone"));
		model.addAttribute("email", result.get("guest_email"));
		model.addAttribute("indate", result.get("reserve_start"));

		PaymentVO payment = payService.getPayment(reserveNo);
		
		log.debug("{}", payment);

		model.addAttribute("payway", payment.getPayment_type().equals("card") ? "카드결제" : "무통장입금");
		model.addAttribute("paystate", payment.getPayment_status().equals("1") ? "결제완료" : "결제대기");
		model.addAttribute("payprice", comma(payment.getPayment_total()));
		model.addAttribute("total", comma(payment.getPayment_total()));
		
		RoomVO roomInfo = reserService.getRoomInfo(Integer.parseInt(String.valueOf(result.get("room_idx"))));
		
		log.debug("{}", roomInfo);
		
		model.addAttribute("roomname", roomInfo.getRoom_name());
		model.addAttribute("roomprice", comma(roomInfo.getRoom_price()));
		
		if(peoInfo != null && !peoInfo.equals("")) {
			String[] arr = peoInfo.split("/");
			model.addAttribute("reser_adult", arr[0]);
			model.addAttribute("reser_child", arr[1]);
			model.addAttribute("reser_baby", arr[2]);
		}
		
		return "reserve/reserComplete";
	}
	
	private String comma(int value) {
		DecimalFormat df = new DecimalFormat("###,###");
		String value_str = df.format(value);
		return value_str;
	}
}
