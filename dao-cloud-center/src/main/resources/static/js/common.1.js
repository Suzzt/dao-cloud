$(function(){

    // logout
    $("#logoutBtn").click(function(){
        layer.confirm( "确认注销登录?" , {
            icon: 3,
            title: "系统提示" ,
            btn: [ "确认", "取消" ]
        }, function(index){
            layer.close(index);

            $.post(base_url + "/logout", function(data, status) {
                if (data.code == "00000") {
                    layer.msg( "注销成功" );
                    setTimeout(function(){
                        window.location.href = base_url + "/";
                    }, 500);
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || "注销失败" ),
                        icon: '2'
                    });
                }
            });
        });

    });


	// scrollup
	$.scrollUp({
		animation: 'fade',	// fade/slide/none
		scrollImg: true
	});


    // left menu status v: js + server + cookie
    $('.sidebar-toggle').click(function(){
        var dao_cloud_adminlte_settings = $.cookie('dao-cloud_adminlte_settings');	// on=open，off=close
        if ('off' == dao_cloud_adminlte_settings) {
            dao_dao_cloud_adminlte_settings = 'on';
        } else {
            dao_dao_cloud_adminlte_settings = 'off';
        }
        $.cookie('dao-cloud_adminlte_settings', dao_cloud_adminlte_settings, { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
    });

});
