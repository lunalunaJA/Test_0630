(function($) {
	$.ajaxSetup({
		beforeSend: function(xhr) {
			xhr.setRequestHeader("AJAX", true);
		},
		error: function(xhr, status, err) {
			if (xhr.status == 401) {
				alert("세션이 종료되었습니다.");
				location.href = context+"/login";
			}else if (xhr.status == 403) {
				alert("허용되지 않은 접근경로입니다.");
				location.href = context+"/login";
			} else {
				alert("예외가 발생했습니다. 관리자에게 문의하세요.");
				location.href = context+"/login";
			}
		}
	});
})(jQuery);
