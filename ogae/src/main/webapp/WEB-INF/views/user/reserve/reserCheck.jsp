<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>예약확인</title>
<link rel="stylesheet" href="resources/calendar/css/calendar.css">
<link rel="stylesheet" href="resources/calendar/css/reserCalendar.css">
<link rel="stylesheet" href="resources/calendar/css/reset.css">
<script src="https://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
<div class="reserCheck">
	<h3>예약 확인 및 취소</h3>
	<form action="getReserve.do" method="post" id="frm">
		<input type="hidden" name="peoInfo" value="">
		<fieldset>
			<legend>예약 확인 및 취소</legend>
			<table>
				<caption>예약 확인 및 취소</caption>
				<colgroup>
					<col style="width: 22%;">
					<col style="width: 78%;">
				</colgroup>
				<tbody>
					<tr>
						<th scope="row"><label for="name">예약자명</label></th>
						<td><input type="text" id="name" name="name" placeholder="예약자명 입력"></td>
					</tr>
					<tr class="contact">
						<th scope="row"><label for="contact">연락처</label></th>
						<td>
							<select id="contact" name="phone1">
								<option value="010" selected>010</option>
									<option value="011">011</option>
									<option value="016">016</option>
									<option value="017">017</option>
									<option value="018">018</option>
									<option value="019">019</option>
							</select> - 
							<input type="text" id="phone2" name="phone2"> - 
							<input type="text" id="phone3" name="phone3">
							<input type="hidden" id="phone" name="phone">
						</td>
					</tr>
					<tr>
						<th scope="row"><label for="reserveno">예약번호</label></th>
						<td>
							<input type="text" id="reserveno" name="reserveno" placeholder="예약시 휴대폰으로 발송된 예약번호를 입력">
						</td>
					</tr>
					<tr>
						<th></th>
						<td><button type="button" onclick="chkData();">예약조회</button></td>
					</tr>
				</tbody>
			</table>
		</fieldset>
	</form>
</div>
<script>
function chkData() {
    
    var data = $('.reserCheck input');
 	
 	for(var i = 0; i < data.length; i++) {
 		if(data[i].value == '') {
 			var elId =  data[i].id;
 			if(elId == '') {
 				elId = data[i].name;
 			}
 			if(elId == 'name') {
 				alert('예약자명을 입력해주세요.');
 				return false;
 			} 
			if(elId == 'phone2' || elId == 'phone3') {
 				alert('연락처를 입력해주세요.');
 				return false;
 			}
 			if(elId == 'reserveno') {
 				alert('예약번호를 입력해주세요.');
 				return false;
 			} 
 		}
 	}
 	
 	var phone = $('#contact').val() + '-' + $('input[name="phone2"]').val() + '-' + $('input[name="phone3"]').val()
   	$('#phone').val(phone);
 	//alert($('#phone').val());
 	$('#frm').submit();
}


</script>
</body>
</html>