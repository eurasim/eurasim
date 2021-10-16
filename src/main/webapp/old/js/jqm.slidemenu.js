$(document).on("pageinit", function(e){

	$("#"+ $(e.target).attr('id') +" :jqmData(slidemenu)").addClass('slidemenu_btn');
	var sm = $($("#"+ $(e.target).attr('id') +" :jqmData(slidemenu)").data('slidemenu'));
	sm.addClass('slidemenu');

	$(document).on("click", ".ui-page-active :jqmData(slidemenu)", function(e) {
		//document.getElementById("inputSource").value += "fun:hello ";
		slidemenu(sm, sm.data('slideopen'));
		if(document.getElementById("inputSource").hidden ) {
			document.getElementById("inputSource").hidden = false;
			document.getElementById("inputSourcePop").hidden = false;
			document.getElementById("inputTarget1").hidden = false;
			document.getElementById("inputTarget1Pop").hidden = false;
			document.getElementById("inputTarget2").hidden = false;
			document.getElementById("inputTarget2Pop").hidden = false;
			document.getElementById("inputTarget3").hidden = false;
			document.getElementById("inputTarget3Pop").hidden = false;
			document.getElementById("inputTarget4").hidden = false;
			document.getElementById("inputTarget4Pop").hidden = false;
			document.getElementById("inputTarget5").hidden = false;
			document.getElementById("inputTarget5Pop").hidden = false;
		}
		else {
			document.getElementById("inputSource").hidden = true;
			document.getElementById("inputSourcePop").hidden = true;
			document.getElementById("inputTarget1").hidden = true;
			document.getElementById("inputTarget1Pop").hidden = true;
			document.getElementById("inputTarget2").hidden = true;
			document.getElementById("inputTarget2Pop").hidden = true;
			document.getElementById("inputTarget3").hidden = true;
			document.getElementById("inputTarget3Pop").hidden = true;
			document.getElementById("inputTarget4").hidden = true;
			document.getElementById("inputTarget4Pop").hidden = true;
			document.getElementById("inputTarget5").hidden = true;
			document.getElementById("inputTarget5Pop").hidden = true;
		}
		e.stopImmediatePropagation();
		e.preventDefault();
	});
	
	$(document).on("click", "a:not(:jqmData(slidemenu))", function(e) {	
		//document.getElementById("inputSource").value += "fun:partial ";
		//slidemenu(sm, true);
	});

	$(window).on('resize', function(e){
		if (sm.data('slideopen')) {
			console.log('sd');
			sm.css('top', getPageScroll()[1] + 'px');
			sm.css('width', '320px');
			sm.height(viewport().height);
			$(":jqmData(role='page')").css('left', '320px');
		}
	});
	
	$(window).scroll(function() {
		if (sm.data('slideopen')) {
			sm.css('top', getPageScroll()[1] + 'px');
		}
	});

});

$(document).on("pageshow", function(e){
	//document.getElementById("inputSource").value += "fun:pageshow ";
	var sm = $($("#"+ $(e.target).attr('id') +" :jqmData(slidemenu)").data('slidemenu'));
	slidemenu(sm, sm.data('slideopen'));
	document.getElementById("inputSource").hidden = false;
	document.getElementById("inputSourcePop").hidden = false;
	document.getElementById("inputTarget1").hidden = false;
	document.getElementById("inputTarget1Pop").hidden = false;
	document.getElementById("inputTarget2").hidden = false;
	document.getElementById("inputTarget2Pop").hidden = false;
	document.getElementById("inputTarget3").hidden = false;
	document.getElementById("inputTarget3Pop").hidden = false;
	document.getElementById("inputTarget4").hidden = false;
	document.getElementById("inputTarget4Pop").hidden = false;
	document.getElementById("inputTarget5").hidden = false;
	document.getElementById("inputTarget5Pop").hidden = false;
	e.stopImmediatePropagation();
	e.preventDefault();
});


function slidemenu(sm, only_close) {
	//document.getElementById("inputSource").value += "fun:slidemenu ";
	sm.height(viewport().height);
	if (!sm.data('slideopen') && !only_close) {
		sm.show().animate({width: '320px', avoidTransforms: false, useTranslate3d: true}, 'fast');
		$(".ui-page-active").css('left', '320px');
		sm.data('slideopen', true);
		if ($(".ui-page-active :jqmData(role='header')").data('position') == 'fixed') {
			$(".ui-page-active :jqmData(slidemenu)").css('margin-left', '250px');
		} else {
			$(".ui-page-active :jqmData(slidemenu)").css('margin-left', '10px');
		}
	} else {
		sm.animate({width: '0px', avoidTransforms: false, useTranslate3d: true}, 'fast', function(){sm.hide()});
		$(".ui-page-active").css('left', '0px');
		sm.data('slideopen', false);
		$(".ui-page-active :jqmData(slidemenu)").css('margin-left', '0px');
	}
	return false;
}

function viewport(){
	var e = window;
	var a = 'inner';
	if (!('innerWidth' in window)) {
		a = 'client';
		e = document.documentElement || document.body;
	}
	return { width : e[ a+'Width' ] , height : e[ a+'Height' ] }
}

function getPageScroll() {
    var xScroll, yScroll;
    if (self.pageYOffset) {
      yScroll = self.pageYOffset;
      xScroll = self.pageXOffset;
    } else if (document.documentElement && document.documentElement.scrollTop) {
      yScroll = document.documentElement.scrollTop;
      xScroll = document.documentElement.scrollLeft;
    } else if (document.body) {// all other Explorers
      yScroll = document.body.scrollTop;
      xScroll = document.body.scrollLeft;
    }
    return new Array(xScroll,yScroll)
}