$(document).ready(function($) {

	/* 전체보기 */
	$(document).on('click','.icon_fullscreen',function(e) {
		if (!document.fullscreenElement && !document.webkitFullscreenElement && !document.msFullscreenElement ) {
			$('#icon-full').hide();
			$('#icon-normal').show();

			if (document.documentElement.requestFullscreen) {
				document.documentElement.requestFullscreen();
			} else if (document.documentElement.msRequestFullscreen) {
				document.documentElement.msRequestFullscreen();
			} else if (document.documentElement.webkitRequestFullscreen) {
				document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
			}
		} else {
			$('#icon-normal').hide();
			$('#icon-full').show();

			if (document.exitFullscreen) {
				document.exitFullscreen();
			}
			else if (document.msExitFullscreen) {
				document.msExitFullscreen();
			}
			else if (document.webkitExitFullscreen) {
				document.webkitExitFullscreen();
			}
		}	
	});


	$(document).on('click','#icon-marker',function(e) {
		location.href = "/include/landscape.php?biz_code="+biz_code+"&tab=about4" ;
	});

	// gnb_menu 자동 계산
	function resize_gnb() {
		var logo_width = $('.logo').width();
		var quick_width = $('.quick_menu').width();
		var window_width = $(window).width();  // 17 = 스크롤바 넓이

		var ww = window_width - logo_width - quick_width - 100; // 100 = 좌우 마진

		$('.gnb_menu').css('max-width', ww).css('margin-left', logo_width+50+'px');
	}

	$(window).resize(function() {
		if($(window).width() > 1400) {
			resize_gnb();
		}else {
			$('.gnb_menu').css('max-width', '100%');
		}
	});

	/** Added By Jubh 2020.02.12 **/
	$(document).ready(function() {
		if($(window).width() > 1400) {
			resize_gnb();
		}else {
			$('.gnb_menu').css('max-width', '100%');
		}
	});


	$(window).scroll(function(){
		if($(window).scrollTop()>80){
			$('.navigation').addClass('fixed');
		}else {
			$('.navigation').removeClass('fixed');
		}

		if($(window).scrollTop() > $('.header').height()+50) {
			$('.scrollTop').addClass('active');
			$('.fixedBooking').addClass('active');
			$('.fixedPhone').addClass('active');
		}else {
			$('.scrollTop').removeClass('active');
			$('.fixedBooking').removeClass('active');
			$('.fixedPhone').removeClass('active');
		}
	});


	$('.header').hover(function() {
		if($(window).width() > 820) {
			if($(this).hasClass('active')) {
				$(this).removeClass('active');
			}else {
				$(this).addClass('active');
			}
		}
	});

	$('#menu_open').click(function() {
		if($(this).hasClass('is-active')) {
			$(this).removeClass('is-active');
			$('.gnb_menu_mobile_inner').removeClass('OPEN');
		}else {
			$(this).addClass('is-active');
			$('.gnb_menu_mobile_inner').addClass('OPEN');
		}
	});

	$('.mobile_menu_sub > li > a').click(function(e) {
		e.preventDefault();
	
		var $li = $(this).parent();	
		var $next_ul = $li.find('> ul');
		
		$('.mobile_menu_sub > li').removeClass('active').find('> ul').stop().slideUp();
		
		if(!$next_ul.is(':visible')){
			$li.addClass('active');
			$next_ul.stop().slideDown();
		}
	});


	/* 메인 서비스 사용안함 제거 */
	for(var t=0;t<data_service.length;t++) {
		if( data_service[t]['use_yn'] == 'N') {
			var tt = data_service[t]['idx'] ;
			$('.service_list_wrap .service_list li').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}


	/* 메인 객실 사용안함 제거 */
	for(var t=0;t<data_room.length;t++) {
		if( data_room[t]['use_yn'] == 'N') {
			var tt = data_room[t]['idx'] ;
			$('.roomList_wrap .swiper-wrapper a').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}


	/* 메인 부대시설 사용안함 제거 */
	for(var t=0;t<data_fac.length;t++) {
		if( data_fac[t]['use_yn'] == 'N') {
			var tt = data_fac[t]['idx'] ;
			$('.facList_wrap .swiper-wrapper a').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}

	
	// GNB 객실 사용안함 제거
	for(var t=0;t<data_room.length;t++) {
		if( data_room[t]['use_yn'] == 'N') {
			var tt = data_room[t]['idx'] ;
			$('.room-big-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});

			$('.room-small-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}


	// GNB 부대시설 사용안함 제거
	for(var t=0;t<data_fac.length;t++) {
		if( data_fac[t]['use_yn'] == 'N') {
			var tt = data_fac[t]['idx'] ;
			$('.fac-big-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});

			$('.fac-small-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}

	// GNB 서비스 사용안함 제거
	for(var t=0;t<data_service.length;t++) {
		if( data_service[t]['use_yn'] == 'N') {
			var tt = data_service[t]['idx'] ;
			$('.service-big-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});

			$('.service-small-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}

	// GNB 여행지 사용안함 제거
	for(var t=0;t<data_travel.length;t++) {
		if( data_travel[t]['use_yn'] == 'N') {
			var tt = data_travel[t]['idx'] ;
			$('.travel-big-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});

			$('.travel-small-menu ul .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}


	// GNB 게시판 사용안함 제거
	for(var t=0;t<data_board.length;t++) {
		if( data_board[t]['use_yn'] == 'N') {
			var tt = data_board[t]['board_idx'] ;
			$('.comm-big-menu .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});

			$('.comm-small-menu ul .page').each(function() {
				if( tt == $(this).attr('data-idx')) {
					$(this).remove() ;
				}
			});
		}
	}
});